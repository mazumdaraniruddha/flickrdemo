package com.aniruddha.flickrdemo.paging.ui.custom

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


open class PagerSnapScrollListener(val recyclerView: RecyclerView,
                                   val externalListener: RVPagerStateListener?,
                                   maxPages: Int)
    : RecyclerView.OnScrollListener() {
    var pageStates: MutableList<VisiblePageState> = ArrayList(maxPages)
    var pageStatesPool = List(maxPages) {
        VisiblePageState(0, recyclerView, 0, 0, 0f)
    }

    init {
        recyclerView.addOnScrollListener(this)
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val layoutManager = recyclerView!!.layoutManager as LinearLayoutManager

        val firstPos = layoutManager.findFirstVisibleItemPosition()
        val lastPos = layoutManager.findLastVisibleItemPosition()

        val screenEndX = recyclerView.context.resources.displayMetrics.widthPixels
        val midScreen = (screenEndX / 2)

        for (position in firstPos..lastPos) {
            val view = layoutManager.findViewByPosition(position)
            val viewWidth = view?.measuredWidth ?: 0
            val viewStartX = view?.x ?: 0f
            val viewEndX = viewStartX + viewWidth
            if (viewEndX >= 0 && viewStartX <= screenEndX) {
                val viewHalfWidth = (view?.measuredWidth ?: 0) / 2f

                val pageState = pageStatesPool[position - firstPos]
                pageState.index = position
                view?.let {
                    pageState.view = it
                }
                pageState.viewCenterX = (viewStartX + viewWidth / 2f).toInt()
                pageState.distanceToSettledPixels = (pageState.viewCenterX - midScreen)
                pageState.distanceToSettled = (pageState.viewCenterX + viewHalfWidth) / (midScreen + viewHalfWidth)
                pageStates.add(pageState)
            }
        }
        externalListener?.onPageScroll(pageStates)

        // Clear this in advance so as to avoid holding refs to views.
        pageStates.clear()
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        externalListener?.onScrollStateChanged(statesArray[newState])
    }

    companion object {
        val statesArray = listOf(RVPageScrollState.Idle(), RVPageScrollState.Dragging(), RVPageScrollState.Settling())
    }
}
