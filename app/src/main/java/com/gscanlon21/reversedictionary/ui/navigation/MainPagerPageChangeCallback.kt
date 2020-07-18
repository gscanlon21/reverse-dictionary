package com.gscanlon21.reversedictionary.ui.navigation

import androidx.viewpager2.widget.ViewPager2
import com.gscanlon21.reversedictionary.vm.MainViewModel

class MainPagerPageChangeCallback(private val mainViewModel: MainViewModel) : ViewPager2.OnPageChangeCallback() {
    override fun onPageScrollStateChanged(state: Int) {
        when (state) {
            ViewPager2.SCROLL_STATE_DRAGGING -> mainViewModel.setViewPagerScrolling(true) // User begins dragging or a fake drag is started
            ViewPager2.SCROLL_STATE_IDLE -> mainViewModel.setViewPagerScrolling(false) // Pager is fully stopped/idle
            ViewPager2.SCROLL_STATE_SETTLING -> Unit // Pager is automatically settling to the current page
        }
    }
}
