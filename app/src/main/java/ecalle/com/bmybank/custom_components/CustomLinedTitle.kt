package ecalle.com.bmybank.custom_components

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import ecalle.com.bmybank.R
import kotlinx.android.synthetic.main.custom_lined_title.view.*

/**
 * Created by Thomas Ecalle on 07/03/2018.
 */
class CustomLinedTitle @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
        defStyleRes: Int = 0
) : ConstraintLayout(context, attrs)
{

    init
    {
        LayoutInflater.from(context).inflate(R.layout.custom_lined_title, this, true)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.custom_lined_title, 0, 0)
            val textValue = resources.getText(typedArray.getResourceId(R.styleable.custom_lined_title_title, R.string.lorem))

            text.text = textValue

            typedArray.recycle()
        }
    }
}