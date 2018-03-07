package ecalle.com.bmybank.extensions

import android.view.View
import android.widget.EditText
import android.widget.TextView

/**
 * Created by thoma on 04/03/2018.
 */


var TextView.textColor: Int
    get() = currentTextColor
    set(v) = setTextColor(v)

fun EditText.isEmpty(): Boolean
{
    return text.isEmpty()
}

fun EditText.text(): String
{
    return text.toString()
}

fun EditText.hasOnlyLetters(): Boolean
{
    return text.matches(Regex("[a-zA-Z]+"));
}

fun View.slideExit()
{
    if (translationY == 0f) animate().translationY(-height.toFloat())
}

fun View.slideEnter()
{
    if (translationY < 0f) animate().translationY(0f)
}