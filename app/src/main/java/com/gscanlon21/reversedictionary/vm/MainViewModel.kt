package com.gscanlon21.reversedictionary.vm

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gscanlon21.reversedictionary.repository.data.ViewResource
import com.gscanlon21.reversedictionary.ui.navigation.UiView
import com.gscanlon21.reversedictionary.utility.IdlingResource
import kotlinx.coroutines.CompletableDeferred

class MainViewModel : ViewModel() {
    private var viewPagerIdling: IdlingResource? = null
    @VisibleForTesting
    fun getIdlingResource(): IdlingResource {
        if (viewPagerIdling == null) {
            viewPagerIdling = IdlingResource()
        }
        return viewPagerIdling as IdlingResource
    }

    val viewPagerItems: ArrayList<UiView> = arrayListOf()
    val loadingJobs = mutableMapOf<UiView.SearchResult, CompletableDeferred<ViewResource<Any?>>>()

    private val _viewPagerScrolling = MutableLiveData<Boolean>()
    val viewPagerScrolling: LiveData<Boolean> get() = _viewPagerScrolling
    fun setViewPagerScrolling(scrolling: Boolean) {
        viewPagerIdling?.setIdleState(!scrolling)
        _viewPagerScrolling.value = scrolling
    }
}
