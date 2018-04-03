package ecalle.com.bmybank

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import ecalle.com.bmybank.extensions.makeEditTextScrollableInScrollview
import ecalle.com.bmybank.extensions.textValue
import org.jetbrains.anko.find

/**
 * Created by Thomas Ecalle on 02/04/2018.
 */
class AddLoanActivity : AppCompatActivity(), ToolbarManager, View.OnClickListener
{

    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }

    private lateinit var description: EditText
    private lateinit var validate: Button
    private lateinit var errorView: TextView
    private lateinit var scrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_loan)

        toolbarTitle = getString(R.string.add_loan_title)
        enableHomeAsUp { onBackPressed() }

        description = find(R.id.description)
        errorView = find(R.id.errorView)
        scrollView = find(R.id.scrollView)
        validate = find(R.id.validate)

        description.makeEditTextScrollableInScrollview()
        validate.setOnClickListener(this)
    }

    override fun onClick(view: View?)
    {
        when (view?.id)
        {
            validate.id -> checkThenSend()
        }
    }

    private fun checkThenSend()
    {
        if (descriptionIsNotWellFormat())
        {
            showError()
            return
        }
    }

    fun showError(error: String = getString(R.string.not_well_format_description))
    {
        errorView.text = error
        errorView.visibility = View.VISIBLE
        scrollView.fullScroll(ScrollView.FOCUS_UP)
    }


    private fun descriptionIsNotWellFormat(): Boolean
    {
        return description.textValue.length >= Constants.DESCRIPTION_MAX_LENGTH
    }
}