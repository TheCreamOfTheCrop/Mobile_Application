package ecalle.com.bmybank.custom_components

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import ecalle.com.bmybank.R
import kotlinx.android.synthetic.main.custom_simple_linked_card.view.*


/**
 * Created by Thomas Ecalle on 26/02/2018.
 */
class CustomLinkedCard @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
        defStyleRes: Int = 0
) : CardView(context, attrs)
{

    init
    {
        LayoutInflater.from(context).inflate(R.layout.custom_simple_linked_card, this, true)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.custom_linked_card, 0, 0)
            val value = resources.getText(typedArray.getResourceId(R.styleable.custom_linked_card_text, R.string.lorem))

            messageTextView.text = value

            cardElevation = resources.getDimension(R.dimen.generalCardElevation)

            /*
            val outValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            setBackgroundResource(outValue.resourceId)
            isClickable = true
            */



            typedArray.recycle()
        }
    }
}