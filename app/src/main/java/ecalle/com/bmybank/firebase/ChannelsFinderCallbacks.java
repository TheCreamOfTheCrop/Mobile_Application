package ecalle.com.bmybank.firebase;

import ecalle.com.bmybank.firebase.bo.Channel;

/**
 * Created by thoma on 19/01/2018.
 */

public interface ChannelsFinderCallbacks
{
    void onChannelFound(Channel story);

    void onChannelRemoved(Channel story);

    void onNoChannelFound();

    void onStartSearching();

    void onNetworkError();

}
