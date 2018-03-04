package ecalle.com.bmybank.extensions

import android.view.View
import android.widget.TextView
import android.widget.Toast

/**
 * Created by thoma on 04/03/2018.
 */


var TextView.textColor: Int
    get() = currentTextColor
    set(v) = setTextColor(v)

fun View.slideExit()
{
    if (translationY == 0f) animate().translationY(-height.toFloat())
}

fun View.slideEnter()
{
    if (translationY < 0f) animate().translationY(0f)
}