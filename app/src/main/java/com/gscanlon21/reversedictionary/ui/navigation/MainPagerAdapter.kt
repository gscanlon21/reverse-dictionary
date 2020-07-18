package com.gscanlon21.reversedictionary.ui.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gscanlon21.reversedictionary.ui.main.history.HistoryFragment
import com.gscanlon21.reversedictionary.ui.main.search.SearchFragment
import com.gscanlon21.reversedictionary.ui.main.search.result.SearchResultFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * A [FragmentStateAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages
 */
class MainPagerAdapter(private val fragmentActivity: FragmentActivity, private val viewPagerItems: ArrayList<UiView>) :
    FragmentStateAdapter(fragmentActivity), IPagerAdapter {

    @ExperimentalCoroutinesApi
    override fun createFragment(position: Int): Fragment {
        return when (viewPagerItems[position]) {
            is UiView.Main.Search -> SearchFragment()
            UiView.Main.History -> HistoryFragment()
            else -> SearchResultFragment.newInstance(viewPagerItems[position])
        }
    }

    override fun containsItem(itemId: Long) = viewPagerItems.any { it.hashCode().toLong() == itemId }
    override fun getItemId(position: Int): Long = viewPagerItems[position].hashCode().toLong()
    override fun getItemViewType(position: Int): Int = position
    override fun getItemCount(): Int = viewPagerItems.count()

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentActivity.resources.getString(viewPagerItems[position].resId)
    }
}
