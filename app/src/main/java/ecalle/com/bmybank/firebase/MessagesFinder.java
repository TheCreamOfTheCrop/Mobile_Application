package ecalle.com.bmybank.firebase;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import ecalle.com.bmybank.firebase.bo.Message;

/**
 * Created by thoma on 19/01/2018.
 */

public class MessagesFinder
{
    private MessagesFinderCallbacks callbacks;
    private DatabaseReference messagesReference;
    private Context context;

    public MessagesFinder(Context context, MessagesFinderCallbacks callbacks)
    {
        this.callbacks = callbacks;
        this.context = context;
    }

    public void start(final int channelMessagesListId)
    {

        if (!isNetworkAvailable())
        {
            callbacks.onNetworkError();
            return;
        }

        messagesReference = Utils.getDatabase().getReference("listMessages").child(String.valueOf(channelMessagesListId)).child("messages");

        callbacks.onStartSearching();

        messagesReference.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                final Message message = dataSnapshot.getValue(Message.class);
                callbacks.onMessageFound(message);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                callbacks.onMessageRemoved(new Message());
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

    private boolean isNetworkAvailable()
    {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
