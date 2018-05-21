package ecalle.com.bmybank.firebase;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ecalle.com.bmybank.firebase.bo.Channel;
import ecalle.com.bmybank.realm.bo.User;

/**
 * Created by thoma on 19/01/2018.
 */

public class ChannelsFinder
{
    private ChannelsFinderCallbacks callbacks;
    private DatabaseReference mDatabaseReference;
    private Context context;

    public ChannelsFinder(Context context, ChannelsFinderCallbacks callbacks)
    {
        this.callbacks = callbacks;
        this.context = context;
    }

    public void start(User user)
    {

        if (!isNetworkAvailable())
        {
            callbacks.onNetworkError();
            return;
        }

        mDatabaseReference = Utils.getDatabase().getReference("user-channels").child(String.valueOf(user.getId()));

        callbacks.onStartSearching();

        final ArrayList<Integer> userChannelsIds = new ArrayList<>();

        mDatabaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (!dataSnapshot.exists())
                {
                    callbacks.onNoChannelFound();
                }
                else
                {
                    final ecalle.com.bmybank.firebase.bo.User userInFirebase = dataSnapshot.getValue(ecalle.com.bmybank.firebase.bo.User.class);
                    userChannelsIds.addAll(userInFirebase.getChannels());

                    for (Integer channelId : userChannelsIds)
                    {
                        final DatabaseReference channelReference = Utils.getDatabase().getReference("channels").child(String.valueOf(channelId));

                        channelReference.addChildEventListener(new ChildEventListener()
                        {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s)
                            {

                                channelReference.addValueEventListener(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        if (null == dataSnapshot.getValue())
                                        {
                                            channelReference.removeEventListener(this);
                                            return;
                                        }
                                        final Channel channel = dataSnapshot.getValue(Channel.class);
                                        callbacks.onChannelFound(channel);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError)
                                    {
                                        Log.i("thomas", "onCancelled");
                                    }
                                });
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s)
                            {
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot)
                            {
                                callbacks.onChannelRemoved(new Channel());
                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s)
                            {
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError)
                            {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.i("thomas", "searching for user in firebase cancelled ");
                callbacks.onNoChannelFound();

            }
        });



    }

    private boolean isNetworkAvailable()
    {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
