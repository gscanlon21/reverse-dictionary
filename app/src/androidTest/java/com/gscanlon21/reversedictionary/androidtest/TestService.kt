package com.gscanlon21.reversedictionary.androidtest

import com.gscanlon21.reversedictionary.core.repository.ApiType
import com.gscanlon21.reversedictionary.service.SearchService
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockkConstructor
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before

interface TestService {
    val searchService: SearchService get() = object : DefaultService { }

    @Before
    fun api_before() {
        setupApiMocks()
    }

    fun setupApiMocks() = runBlocking {
        mockkConstructor(SearchService::class)

        val termSlot = slot<String>()
        val typeSlot = slot<ApiType.Datamuse>()
        coEvery {
            anyConstructed<SearchService>().datamuseLookup(capture(termSlot), capture(typeSlot))
        } coAnswers { searchService.datamuseLookup(termSlot.captured, typeSlot.captured) }
    }

    @After
    fun api_after() {
        clearAllMocks()
    }
}
