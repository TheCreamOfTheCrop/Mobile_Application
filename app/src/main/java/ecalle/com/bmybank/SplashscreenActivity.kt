package ecalle.com.bmybank

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import ecalle.com.bmybank.extensions.log
import ecalle.com.bmybank.realm.RealmServices
import org.jetbrains.anko.startActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ecalle.com.bmybank.firebase.Channel
import ecalle.com.bmybank.firebase.Message
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import ecalle.com.bmybank.firebase.User


/**
 * Created by Thomas Ecalle on 12/04/2018.
 */
class SplashscreenActivity : AppCompatActivity()
{

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.splashscreen)

    // Write a message to the database
    val database = FirebaseDatabase.getInstance()

    val message1 = Message("salut, premier message", 1)
    val message2 = Message("salut mec, enchanté", 2)

    val message3 = Message("plop", 1)
    val message4 = Message("Je veux la thune pour mon prêt !", 3)

    val list = listOf(message1, message2)
    val list2 = listOf(message3, message4)

    val channel = Channel(0, null, 1, 2, list)
    val channel2 = Channel(1, null, 1, 3, list2)

    val userRef = database.getReference("user-channels")
    val user1 = User(1, listOf(channel.id, channel2.id))
    val user2 = User(2, listOf(channel.id))
    val user3 = User(3, listOf(channel2.id))

    userRef.child(user1.id.toString()).setValue(user1)
    userRef.child(user2.id.toString()).setValue(user2)
    userRef.child(user3.id.toString()).setValue(user3)

    val myRef = database.getReference("channels")

    myRef.child(channel.id.toString()).setValue(channel)
    myRef.child(channel2.id.toString()).setValue(channel2)


    // Read from the database
    myRef.addValueEventListener(object : ValueEventListener
    {
      override fun onDataChange(dataSnapshot: DataSnapshot)
      {
        // This method is called once with the initial value and again
        // whenever data at this location is updated.
        val channels = mutableListOf<Channel>()

        val value = dataSnapshot.children.mapNotNullTo(channels) {
          it.getValue<Channel>(Channel::class.java)
        }

        Log.d("thomasecalle", "channels are : $channels")
      }

      override fun onCancelled(error: DatabaseError)
      {
        // Failed to read value
        Log.w("thomasecalle", "Failed to read value.", error.toException())
      }
    })



    Handler().postDelayed({

      checkForUserTheNavigate()

    }, 2000)


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
