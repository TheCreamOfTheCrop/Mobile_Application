package ecalle.com.bmybank

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import ecalle.com.bmybank.extensions.changeStatusBar
import ecalle.com.bmybank.extensions.log
import ecalle.com.bmybank.firebase.Utils
import ecalle.com.bmybank.firebase.bo.Channel
import ecalle.com.bmybank.firebase.bo.ListMessages
import ecalle.com.bmybank.firebase.bo.Message
import ecalle.com.bmybank.firebase.bo.User
import ecalle.com.bmybank.realm.RealmServices
import org.jetbrains.anko.startActivity
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Thomas Ecalle on 12/04/2018.
 */
class SplashscreenActivity : AppCompatActivity()
{


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashscreen)

        changeStatusBar(R.color.colorPrimary, this)

        //initializeFakeFirebaseDatas()

        Handler().postDelayed({

            checkForUserTheNavigate()

        }, 2000)


    }

    private fun initializeFakeFirebaseDatas()
    {

        // Write a message to the database
        val database = Utils.getDatabase()

        val user1 = User(14)
        val user2 = User(32)
        val user3 = User(16)

        val message1 = Message("salut, premier message", user2.id, SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().time))
        val message2 = Message("salut, enchantée", user1.id, SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().time))
        val message3 = Message("plop", user3.id, SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().time))
        val message4 = Message("Je veux la thune pour mon prêt !", user1.id, SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().time))


        val list = ListMessages("${user1.id}${user2.id}".toInt(), mutableListOf(message1, message2))
        val list2 = ListMessages("${user1.id}${user3.id}".toInt(), mutableListOf(message3, message4))


        //val channel = Channel(null, 14, 32, list.id)
        val channel2 = Channel(null, 14, 16, list2.id, message4.text)

        user1.channels = listOf(channel2)
        //user2.channels = listOf(channel)
        user3.channels = listOf(channel2)

        val userRef = database.getReference("user-channels")

        //userRef.child(user1.id.toString()).setValue(user1)
        //userRef.child(user2.id.toString()).setValue(user2)
        //userRef.child(user3.id.toString()).setValue(user3)

        for (channel in user1.channels)
        {
            userRef.child(user1.id.toString()).child("channels").child("${channel.id_user_1}${channel.id_user_2}").setValue(channel)
        }

        for (channel in user2.channels)
        {
            userRef.child(user2.id.toString()).child("channels").child("${channel.id_user_1}${channel.id_user_2}").setValue(channel)
        }

        for (channel in user3.channels)
        {
            userRef.child(user3.id.toString()).child("channels").child("${channel.id_user_1}${channel.id_user_2}").setValue(channel)
        }

        //val myRef = database.getReference("channels")

        //myRef.child(channel.id.toString()).setValue(channel)
        //myRef.child(channel2.id.toString()).setValue(channel2)

        val listMessageRef = database.getReference("listMessages")

        for (message in list.messages)
        {
            //listMessageRef.child(channel.list_messages_id.toString()).push().setValue(message)
        }

        for (message in list2.messages)
        {
            listMessageRef.child(channel2.list_messages_id.toString()).push().setValue(message)
        }

    }

    private fun checkForUserTheNavigate()
    {
        if (RealmServices.getCurrentUser(this) != null)
        {
            log("A user seems already logged in")
            goToMainActivity()
        }
        else
        {
            log("No user already logged in")
            goToLoginActivity()
        }
    }

    private fun goToMainActivity()
    {
        startActivity<MainActivity>()
        finish()
    }

    private fun goToLoginActivity()
    {
        startActivity<LoginActivity>()
        finish()
    }
}
