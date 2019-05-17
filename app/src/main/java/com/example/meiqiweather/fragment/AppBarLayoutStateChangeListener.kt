package com.example.meiqiweather.fragment

import android.support.design.widget.AppBarLayout

abstract class AppBarLayoutStateChangeListener : AppBarLayout.OnOffsetChangedListener {
    private var mCurrentState = State.INTERMEDIATE

    enum class State {
        EXPANDED, //展开
        COLLAPSED, //折叠
        INTERMEDIATE//中间状态
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        when {
            verticalOffset == 0 -> {
                if (mCurrentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED)
                }
                mCurrentState = State.EXPANDED
            }
            Math.abs(verticalOffset) >= appBarLayout.totalScrollRange -> {
                if (mCurrentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED)
                }
                mCurrentState = State.COLLAPSED
            }
            else -> {
                if (mCurrentState != State.INTERMEDIATE) {
                    onStateChanged(appBarLayout, State.INTERMEDIATE)
                }
                mCurrentState = State.INTERMEDIATE
            }
        }
    }

    abstract fun onStateChanged(appBarLayout: AppBarLayout, state: State)
}