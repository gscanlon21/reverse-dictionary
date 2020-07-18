package com.gscanlon21.reversedictionary.utility

import androidx.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicBoolean

class IdlingResource : IdlingResource {
    @Volatile
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    private val isIdle: AtomicBoolean = AtomicBoolean(true)

    override fun getName() = IdlingResource::class.java.name
    override fun isIdleNow() = isIdle.get()
    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        resourceCallback = callback
    }

    fun setIdleState(state: Boolean) {
        isIdle.set(state)
        if (isIdleNow && resourceCallback != null) {
            resourceCallback!!.onTransitionToIdle()
        }
    }
}
