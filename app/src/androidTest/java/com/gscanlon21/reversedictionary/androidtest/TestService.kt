package com.gscanlon21.reversedictionary.androidtest

import com.gscanlon21.reversedictionary.repository.data.ApiType
import com.gscanlon21.reversedictionary.service.SearchResultService
import com.gscanlon21.reversedictionary.service.SearchService
import com.gscanlon21.reversedictionary.service.WebService
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockkConstructor
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before

@ExperimentalCoroutinesApi
interface TestService {
    val searchService: WebService.SearchService get() = object : DefaultService { }
    val searchResultService: WebService.SearchResultService get() = object : DefaultService { }

    @Before
    fun api_before() {
        setupApiMocks()
    }

    fun setupApiMocks() = runBlocking {
        mockkConstructor(SearchService::class)
        mockkConstructor(SearchResultService::class)

        coEvery {
            anyConstructed<SearchService>().requestWordOfTheDay()
        } returns searchService.requestWordOfTheDay()

        coEvery {
            anyConstructed<SearchService>().requestRandomWord()
        } returns searchService.requestRandomWord()

        val termSlot = slot<String>()
        val typeSlot = slot<ApiType.Datamuse>()
        coEvery {
            anyConstructed<SearchResultService>().datamuseLookup(capture(termSlot), capture(typeSlot))
        } coAnswers { searchResultService.datamuseLookup(termSlot.captured, typeSlot.captured) }

        coEvery {
            anyConstructed<SearchResultService>().getAnagrams(capture(termSlot))
        } coAnswers { searchResultService.getAnagrams(termSlot.captured) }

        coEvery {
            anyConstructed<SearchResultService>().getAudioUris(capture(termSlot))
        } coAnswers { searchResultService.getAudioUris(termSlot.captured) }
    }

    @After
    fun api_after() {
        clearAllMocks()
    }
}
