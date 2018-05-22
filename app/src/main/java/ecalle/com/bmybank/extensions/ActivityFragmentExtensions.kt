package ecalle.com.bmybank.extensions

import android.app.Fragment
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.tapadoo.alerter.Alerter
import ecalle.com.bmybank.R
import ecalle.com.bmybank.custom_components.BeMyDialog
import org.jetbrains.anko.act


/**
 * Created by Thomas Ecalle on 04/03/2018.
 */
fun AppCompatActivity.log(message: String, tag: String = "thomasecalle", type: Int = Log.INFO) = logger(message, tag, type)

fun log(message: String, tag: String = "thomasecalle", type: Int = Log.INFO) = logger(message, tag, type)

fun AppCompatActivity.alertError(text: String)
{
    Alerter.create(this)
            .setTitle(R.string.error_alerter_title)
            .setText(text)
            .setBackgroundColorRes(R.color.red)
            .setIcon(R.drawable.ic_cross)
            .enableSwipeToDismiss()
            .setIconColorFilter(0) // Optional - Removes white tint
            .show()
}

fun Fragment.alertError(text: String)
{
    Alerter.create(act)
            .setTitle(R.string.error_alerter_title)
            .setText(text)
            .setBackgroundColorRes(R.color.red)
            .setIcon(R.drawable.ic_cross)
            .enableSwipeToDismiss()
            .setIconColorFilter(0) // Optional - Removes white tint
            .show()
}

fun AppCompatActivity.changeStatusBar(color: Int, context: AppCompatActivity)
{
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
    {
        val window = context.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(context, color)
    }
}

fun AppCompatActivity.customAlert(type: BeMyDialog.TYPE = BeMyDialog.TYPE.SUCCESS, message: Int, loop: Boolean = true): BeMyDialog
{
    val dialog = BeMyDialog.Builder(this)
            .type(type)
            .message(message)
            .loop(loop)
            .build()
    dialog.show()
    return dialog
}

fun Fragment.customAlert(type: BeMyDialog.TYPE = BeMyDialog.TYPE.SUCCESS, message: Int, loop: Boolean = true): BeMyDialog
{
    val dialog = BeMyDialog.Builder(activity)
            .type(type)
            .message(message)
            .loop(loop)
            .build()
    dialog.show()
    return dialog
}

fun android.support.v4.app.Fragment.customAlert(type: BeMyDialog.TYPE = BeMyDialog.TYPE.SUCCESS, message: Int, loop: Boolean = true): BeMyDialog
{
    val dialog = BeMyDialog.Builder(context!!)
            .type(type)
            .message(message)
            .loop(loop)
            .build()
    dialog.show()
    return dialog
}


private fun logger(message: String, tag: String = "thomasecalle", type: Int = Log.INFO) =
        when (type)
        {
            Log.INFO -> Log.i(tag, message)
            Log.ERROR -> Log.e(tag, message)
            Log.WARN -> Log.w(tag, message)
            else -> Log.i(tag, message)
        }

fun Fragment.toast(message: String, legth: Int = Toast.LENGTH_SHORT) = Toast.makeText(activity, message, legth).show()
fun android.support.v4.app.Fragment.toast(message: String, legth: Int = Toast.LENGTH_SHORT) = Toast.makeText(activity, message, legth).show()