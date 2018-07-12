package ecalle.com.bmybank

import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.WindowManager
import ecalle.com.bmybank.adapters.LoansAdapter
import ecalle.com.bmybank.extensions.slideEnter
import ecalle.com.bmybank.extensions.slideExit


/**
 * Created by Thomas Ecalle on 04/03/2018.
 */
interface ToolbarManager
{
    val toolbar: Toolbar
    var toolbarTitle: String
        get() = toolbar.title.toString()
        set(value)
        {
            toolbar.title = value
        }

    private fun createUpDrawable() = with(DrawerArrowDrawable(toolbar.context))
    {
        progress = 1f
        this
    }

    fun enableHomeAsUp(up: () -> Unit)
    {
        toolbar.navigationIcon = createUpDrawable()
        toolbar.setNavigationOnClickListener { up() }
    }


    fun changeColor(color: LoansAdapter.Color, context: AppCompatActivity)
    {
        var colorId = 0

        colorId = when (color)
        {
            LoansAdapter.Color.BLUE -> R.color.colorPrimary
            LoansAdapter.Color.ORANGE -> R.color.colorAccent
            LoansAdapter.Color.RED -> R.color.red
            else -> R.color.labelGrey
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            val window = context.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(context, colorId)
        }

        toolbar.setBackgroundColor(ContextCompat.getColor(context, colorId))
    }

    fun attachToScroll(recyclerView: RecyclerView)
    {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int)
            {
                if (dy > 0) toolbar.slideExit() else toolbar.slideEnter()
            }
        })
    }

}