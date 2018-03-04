package ecalle.com.bmybank.custom_components

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import ecalle.com.bmybank.R
import kotlinx.android.synthetic.main.dialog_bemybank.*


/**
 * Created by thoma on 04/03/2018.
 */
class BeMyDialog private constructor(context: Context?, message: String, type: TYPE) : Dialog(context)
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

        val animationJSON: String = if (type == TYPE.SUCCESS) "success.json" else "failure.json"

        animation.setAnimation(animationJSON)
        animation.loop(false)
        animation.playAnimation()


        messageTextView.text = message
    }
}