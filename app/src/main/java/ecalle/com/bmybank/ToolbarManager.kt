package ecalle.com.bmybank

import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
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