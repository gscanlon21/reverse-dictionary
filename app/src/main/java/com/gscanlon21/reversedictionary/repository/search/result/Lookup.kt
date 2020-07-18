package com.gscanlon21.reversedictionary.repository.search.result

import com.android.volley.Response
import com.gscanlon21.reversedictionary.db.search.result.SearchResultDao
import com.gscanlon21.reversedictionary.db.search.result.SearchResultEntity
import com.gscanlon21.reversedictionary.repository.data.ApiType
import com.gscanlon21.reversedictionary.repository.data.NetworkBoundResource
import com.gscanlon21.reversedictionary.service.SearchResultService
import com.gscanlon21.reversedictionary.service.api.DatamuseModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@ExperimentalCoroutinesApi
class Lookup(private val searchResultDao: SearchResultDao, private val searchResultService: SearchResultService, private val phrase: String, private val type: ApiType.Datamuse) :
    NetworkBoundResource<List<SearchResultEntity>, List<DatamuseModel>>() {
    override suspend fun loadFromDb(): Flow<List<SearchResultEntity>> {
        val items = searchResultDao.getList(type, phrase)
        val defs = if (type != ApiType.Datamuse.Definition) {
            searchResultDao.getList(ApiType.Datamuse.Definition, items.map { it.value })
        } else { null }

        return flowOf(
            items.map { ent ->
                SearchResultEntity(ent.key, ent.value, ent.score, ent.type, defs?.filter { it.key == ent.value })
            }
        )
    }
    override suspend fun createCall(): Response<List<DatamuseModel>> = searchResultService.datamuseLookup(phrase, type)
    override suspend fun saveCallResult(item: List<DatamuseModel>): List<SearchResultEntity> {
        val entities = if (type == ApiType.Datamuse.Definition) {
            item.flatMap { ite -> ite.defs?.map { SearchResultEntity(phrase, it, 0, type, null) } ?: emptyList() }
        } else {
            item.map { apiData ->
                SearchResultEntity(phrase, apiData.word, apiData.score, type, apiData.defs?.map {
                    SearchResultEntity(apiData.word, it, 0, ApiType.Datamuse.Definition, null)
                })
            }
        }

        if (type != ApiType.Datamuse.Definition) {
            searchResultDao.insertMany(entities.mapNotNull { it.extra }.flatten())
        }
        searchResultDao.insertMany(entities)
        return entities
    }
}
