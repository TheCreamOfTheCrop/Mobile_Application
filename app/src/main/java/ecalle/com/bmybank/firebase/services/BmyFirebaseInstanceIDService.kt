package ecalle.com.bmybank.firebase.services

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import ecalle.com.bmybank.extensions.log
import ecalle.com.bmybank.firebase.Utils
import ecalle.com.bmybank.realm.RealmServices


/**
 * Created by Thomas Ecalle on 24/05/2018.
 */
class BmyFirebaseInstanceIDService : FirebaseInstanceIdService()
{

    override fun onTokenRefresh()
    {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        log("Refreshed token: " + refreshedToken!!)

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken)
    }

    private fun sendRegistrationToServer(token: String)
    {
        log("sendRegistrationToServer: sending token to server: $token")
        val currentUser = RealmServices.getCurrentUser(this)
        val reference = Utils.getDatabase().reference
        reference.child("users")
                .child(currentUser?.id.toString())
                .child("messaging_token")
                .setValue(token)
    }
}