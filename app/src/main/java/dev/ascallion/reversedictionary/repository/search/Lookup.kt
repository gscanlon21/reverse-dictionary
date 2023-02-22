package dev.ascallion.reversedictionary.repository.search

import com.android.volley.Response
import dev.ascallion.reversedictionary.core.repository.ApiType
import dev.ascallion.reversedictionary.core.repository.NetworkBoundResource
import dev.ascallion.reversedictionary.core.service.DatamuseModel
import dev.ascallion.reversedictionary.db.search.SearchDao
import dev.ascallion.reversedictionary.db.search.SearchResultEntity
import dev.ascallion.reversedictionary.service.SearchService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@ExperimentalCoroutinesApi
class Lookup(
    private val searchDao: SearchDao,
    private val searchService: SearchService,
    private val phrase: String,
    private val type: ApiType.Datamuse
) : NetworkBoundResource<List<SearchResultEntity>, List<DatamuseModel>> {

    override suspend fun loadFromDb(): Flow<List<SearchResultEntity>> {
        val items = searchDao.getList(type, phrase)
        val defs = if (type != ApiType.Datamuse.Definition) {
            searchDao.getList(ApiType.Datamuse.Definition, items.map { it.value })
        } else { null }

        return flowOf(
            items.map { ent ->
                SearchResultEntity(ent.key, ent.value, ent.score, ent.type, defs?.filter { it.key == ent.value })
            }
        )
    }

    override suspend fun createCall(): Response<List<DatamuseModel>> = searchService.datamuseLookup(phrase, type)

    override suspend fun saveCallResult(item: List<DatamuseModel>): List<SearchResultEntity> {
        val entities = if (type == ApiType.Datamuse.Definition) {
            item.flatMap { ite -> ite.defs?.map { SearchResultEntity(phrase, it, 0, type, null) } ?: emptyList() }
        } else {
            item.map { apiData ->
                SearchResultEntity(
                    phrase, apiData.word, apiData.score, type,
                    apiData.defs?.map {
                        SearchResultEntity(apiData.word, it, 0, ApiType.Datamuse.Definition, null)
                    }
                )
            }
        }

        if (type != ApiType.Datamuse.Definition) {
            searchDao.insertMany(entities.mapNotNull { it.extra }.flatten())
        }
        searchDao.insertMany(entities)
        return entities
    }
}
