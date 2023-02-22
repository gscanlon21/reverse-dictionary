package dev.ascallion.reversedictionary.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.ascallion.reversedictionary.core.ui.UiView
import dev.ascallion.reversedictionary.ui.history.HistoryFragment
import dev.ascallion.reversedictionary.ui.search.SearchFragment
import dev.ascallion.reversedictionary.ui.search.SearchResultFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

interface MainPagerAdapter {
    fun getPageTitle(position: Int): CharSequence?
}

@ExperimentalCoroutinesApi
class MainPagerAdapterImpl(private val fragmentActivity: FragmentActivity, private val viewPagerItems: ArrayList<UiView>) :
    FragmentStateAdapter(fragmentActivity),
    MainPagerAdapter {

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
