package com.aniruddha.flickrdemo.paging.ui.custom

/***
 * With help from: https://github.com/d4vidi/VP2RV
 *
 * These custom classes help with snapping the recyclerView items correctly
 * */
sealed class RVPageScrollState {
    class Idle: RVPageScrollState()
    class Dragging: RVPageScrollState()
    class Settling: RVPageScrollState()
}