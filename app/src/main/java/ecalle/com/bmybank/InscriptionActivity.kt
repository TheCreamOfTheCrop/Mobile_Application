package ecalle.com.bmybank

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import ecalle.com.bmybank.adapters.InscriptionStepperAdapter
import kotlinx.android.synthetic.main.activity_inscription.*
import org.jetbrains.anko.find

/**
 * Created by thoma on 04/03/2018.
 */
class InscriptionActivity : AppCompatActivity(), ToolbarManager
{
    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscription)

        toolbarTitle = getString(R.string.inscription)

        stepperLayout.adapter = InscriptionStepperAdapter(supportFragmentManager, this)
    }
}