package ecalle.com.bmybank.custom_components

import android.animation.Animator
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import ecalle.com.bmybank.R
import kotlinx.android.synthetic.main.dialog_bemybank.*


/**
 * Created by Thomas Ecalle on 04/03/2018.
 */
class BeMyDialog private constructor(context: Context?, message: String, type: TYPE) : Dialog(context), Animator.AnimatorListener
{
    private var message: String
    private var type: TYPE

    enum class TYPE
    {
        LOADING, SUCCESS, FAILURE
    }

    class Builder(context: Context)
    {
        val context: Context = context
        var message: String = ""
            private set
        var type: TYPE = TYPE.LOADING
            private set

        fun message(message: Int): Builder
        {
            this.message = context.getString(message)
            return this
        }

        fun type(type: TYPE): Builder
        {
            this.type = type
            return this
        }

        fun build(): BeMyDialog
        {
            return BeMyDialog(context, message, type)
        }
    }

    init
    {
        this.message = message
        this.type = type
    }


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_bemybank)

        setCancelable(false)

        val animationJSON: String = if (type == TYPE.SUCCESS) "success.json" else "failure.json"

        animation.setAnimation(animationJSON)
        animation.loop(false)

        animation.addAnimatorListener(this)
        animation.playAnimation()


        messageTextView.text = message
    }

    override fun onAnimationRepeat(p0: Animator?)
    {

    }

    override fun onAnimationEnd(p0: Animator?)
    {
        setCancelable(true)
    }

    override fun onAnimationCancel(p0: Animator?)
    {

    }

    override fun onAnimationStart(p0: Animator?)
    {

    }
}