package ecalle.com.bmybank.extensions

import android.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast

/**
 * Created by thoma on 04/03/2018.
 */
fun AppCompatActivity.log(message: String, tag: String = "thomasecalle", type: Int = Log.INFO) = logger(message, tag, type)

fun android.support.v4.app.Fragment.log(message: String, tag: String = "thomasecalle", type: Int = Log.INFO) = logger(message, tag, type)

fun logger(message: String, tag: String = "thomasecalle", type: Int = Log.INFO) =
        when (type)
        {
            Log.INFO -> Log.i(tag, message)
            Log.ERROR -> Log.e(tag, message)
            Log.WARN -> Log.w(tag, message)
            else -> Log.i(tag, message)
        }

fun Fragment.toast(message: String, legth: Int = Toast.LENGTH_SHORT) = Toast.makeText(activity, message, legth).show()