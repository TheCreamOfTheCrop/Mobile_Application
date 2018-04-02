package ecalle.com.bmybank.extensions

import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView

/**
 * Created by Thomas Ecalle on 04/03/2018.
 */


var TextView.textColor: Int
    get() = currentTextColor
    set(v) = setTextColor(v)

fun EditText.isEmpty(): Boolean
{
    return textValue.isEmpty()
}

val EditText.textValue: String
    get() = text.toString()

fun EditText.hasOnlyLetters(): Boolean
{
    return textValue.matches(Regex("[A-zÀ-ÿ]+"))
}

fun EditText.makeEditTextScrollableInScrollview()
{

    isVerticalScrollBarEnabled = true
    overScrollMode = View.OVER_SCROLL_ALWAYS
    scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
    movementMethod = ScrollingMovementMethod.getInstance()

    setOnTouchListener(
            { view, motionEvent ->
                view?.parent?.requestDisallowInterceptTouchEvent(true)
                if (motionEvent.action and MotionEvent.ACTION_UP != 0 && motionEvent.actionMasked and MotionEvent.ACTION_UP != 0)
                {
                    view?.parent?.requestDisallowInterceptTouchEvent(false)
                }
                false
            })

}

fun View.slideExit()
{
    if (translationY == 0f) animate().translationY(-height.toFloat())
}

fun View.slideEnter()
{
    if (translationY < 0f) animate().translationY(0f)
}