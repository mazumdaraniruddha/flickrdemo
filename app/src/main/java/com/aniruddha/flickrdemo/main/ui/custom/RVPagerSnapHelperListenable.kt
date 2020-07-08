package com.aniruddha.flickrdemo.main.ui.custom

import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/***
 * With help from: https://github.com/d4vidi/VP2RV
 *
 * These custom classes help with snapping the recyclerView items correctly
 * */
data class VisiblePageState(
        var index: Int,
        var view: View,
        @Px var viewCenterX: Int,
        @Px var distanceToSettledPixels: Int,
        var distanceToSettled: Float)

interface RVPagerStateListener {
    fun onPageScroll(pagesState: List<VisiblePageState>) {}
    fun onScrollStateChanged(state: RVPageScrollState) {}
    fun onPageSelected(index: Int) {}
}

open class RVPagerSnapHelperListenable(private val maxPages: Int = 3) {
    fun attachToRecyclerView(recyclerView: RecyclerView, listener: RVPagerStateListener?) {
        assertRecyclerViewSetup(recyclerView)
        setUpSnapHelper(recyclerView, listener)
        setUpScrollListener(recyclerView, listener)
    }

    protected fun setUpScrollListener(recyclerView: RecyclerView, listener: RVPagerStateListener?) =
        PagerSnapScrollListener(recyclerView, listener, maxPages)

    protected fun setUpSnapHelper(recyclerView: RecyclerView, listener: RVPagerStateListener?) =
        PagerSnapHelperVerbose(recyclerView, listener).attachToRecyclerView(recyclerView)

    protected fun assertRecyclerViewSetup(recyclerView: RecyclerView) {
        if (recyclerView.layoutManager !is LinearLayoutManager) {
            throw IllegalArgumentException("RVPagerSnapHelperListenable can only work with a linear layout manager")
        }

        if ((recyclerView.layoutManager as LinearLayoutManager).orientation != LinearLayoutManager.HORIZONTAL) {
            throw IllegalArgumentException("RVPagerSnapHelperListenable can only work with a horizontal orientation")
        }
    }
}
