package com.gscanlon21.reversedictionary.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.gscanlon21.reversedictionary.R
import com.gscanlon21.reversedictionary.adapter.MainPagerAdapterImpl
import com.gscanlon21.reversedictionary.core.repository.ViewResource
import com.gscanlon21.reversedictionary.core.ui.UiView
import com.gscanlon21.reversedictionary.extension.defaultSharedPreferences
import com.gscanlon21.reversedictionary.extension.emptyResultsHidden
import com.gscanlon21.reversedictionary.extension.pagesToShow
import com.gscanlon21.reversedictionary.ui.preference.PreferenceActivity
import com.gscanlon21.reversedictionary.utility.InjectorUtil
import com.gscanlon21.reversedictionary.vm.MainViewModel
import com.gscanlon21.reversedictionary.vm.search.SearchTermViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {
    @VisibleForTesting
    val mainViewModel: MainViewModel by viewModels()
    private val searchTermViewModel: SearchTermViewModel by viewModels {
        InjectorUtil.provideSearchTermViewModelFactory(applicationContext)
    }

    private val pagerAdapter: MainPagerAdapterImpl by lazy {
        MainPagerAdapterImpl(this, mainViewModel.viewPagerItems)
    }

    companion object {
        const val EXTRA_SEARCH_TERM = "EXTRA_SEARCH_TERM"
        const val INSTANCE_STATE_TITLE = "INSTANCE_STATE_TITLE"
        const val VIEWPAGER_OFFSCREEN_PAGE_LIMIT_NO_RESULTS = 99
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loadingView = findViewById<View>(R.id.loading)
        if (savedInstanceState == null) { initActivity(loadingView) } else { restoreActivityFromDisk(savedInstanceState) }

        if (!searchTermViewModel.searchWord.value.isNullOrBlank()) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = searchTermViewModel.searchWord.value!!
        }

        val viewPager = findViewById<ViewPager2>(R.id.view_pager).apply {
            adapter = pagerAdapter
            offscreenPageLimit = if (this@MainActivity.defaultSharedPreferences().emptyResultsHidden(this@MainActivity)) {
                VIEWPAGER_OFFSCREEN_PAGE_LIMIT_NO_RESULTS
            } else { ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT }
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    when (state) {
                        ViewPager2.SCROLL_STATE_DRAGGING -> mainViewModel.setViewPagerScrolling(true) // User begins dragging or a fake drag is started
                        ViewPager2.SCROLL_STATE_IDLE -> mainViewModel.setViewPagerScrolling(false) // Pager is fully stopped/idle
                        ViewPager2.SCROLL_STATE_SETTLING -> Unit // Pager is automatically settling to the current page
                    }
                }
            })
        }

        findViewById<TabLayout>(R.id.tabs).apply {
            tabMode = TabLayout.MODE_SCROLLABLE
            TabLayoutMediator(this, viewPager) { tab, position ->
                tab.text = pagerAdapter.getPageTitle(position)
            }.attach()
        }
    }

    private fun initActivity(loadingView: View) {
        val searchTerm = this.intent.getStringExtra(EXTRA_SEARCH_TERM)
        searchTermViewModel.setSearchTerm(searchTerm)
        if (searchTerm.isNullOrBlank() || searchTerm.contains(SearchTermViewModel.SEARCH_WORD_PHRASE_DIFFERENTIATOR)) {
            mainViewModel.viewPagerItems.add(UiView.Main.Search)
            mainViewModel.viewPagerItems.add(UiView.Main.History)
        } else {
            val pagesToShow = defaultSharedPreferences().pagesToShow(this)
            mainViewModel.viewPagerItems.addAll(pagesToShow)

            if (defaultSharedPreferences().emptyResultsHidden(this)) {
                mainViewModel.loadingJobs.putAll(pagesToShow.map {
                    Pair(it, CompletableDeferred<ViewResource<Any?>>())
                })

                loadingView.visibility = View.VISIBLE

                lifecycleScope.launch {
                    mainViewModel.loadingJobs.forEach {
                        val value = it.value.await()
                        if (!value.isWithData()) { mainViewModel.viewPagerItems.remove(it.key) }
                    }
                    pagerAdapter.notifyDataSetChanged()
                    loadingView.visibility = View.GONE
                }
            }
        }
    }

    private fun restoreActivityFromDisk(savedInstanceState: Bundle) {
        supportActionBar?.title = savedInstanceState.getCharSequence(INSTANCE_STATE_TITLE)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putCharSequence(INSTANCE_STATE_TITLE, supportActionBar?.title)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                val i = Intent(this, PreferenceActivity::class.java)
                startActivity(i)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
