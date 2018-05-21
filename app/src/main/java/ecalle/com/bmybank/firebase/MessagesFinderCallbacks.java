package ecalle.com.bmybank.firebase;

import ecalle.com.bmybank.firebase.bo.Message;

/**
 * Created by thoma on 19/01/2018.
 */

public interface MessagesFinderCallbacks
{
    void onMessageFound(Message message);

    void onMessageRemoved(Message message);

    void onNoMessageFound();

    void onStartSearching();

    void onNetworkError();

}
