package com.aniruddha.flickrdemo.main.ui.custom

import android.view.View
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

/***
 * With help from: https://github.com/d4vidi/VP2RV
 *
 * These custom classes help with snapping the recyclerView items correctly
 * */
open class PagerSnapHelperVerbose(protected val recyclerView: RecyclerView,
                                  val externalListener: RVPagerStateListener?)
    : PagerSnapHelper()
    , ViewTreeObserver.OnGlobalLayoutListener {

    var lastPage = RecyclerView.NO_POSITION

    init {
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        val position = (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        if (position != RecyclerView.NO_POSITION) {
            notifyNewPageIfNeeded(position)
            recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
        val view = super.findSnapView(layoutManager)
        view?.let {
            notifyNewPageIfNeeded(recyclerView.getChildAdapterPosition(it))
        }
        return view
    }

    override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager?, velocityX: Int, velocityY: Int): Int {
        val position = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)

        if (position < recyclerView.adapter?.itemCount ?: 0) { // Making up for a "bug" in the original snap-helper.
            notifyNewPageIfNeeded(position)
        }
        return position
    }

    protected fun notifyNewPageIfNeeded(page: Int) {
        if (page != lastPage) {
            this.externalListener?.onPageSelected(page)
            lastPage = page
        }
    }
}
