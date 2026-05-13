package com.nallanudi.app.data.repository

import com.nallanudi.app.data.db.SavedTermDao
import com.nallanudi.app.data.db.TermDao
import com.nallanudi.app.data.model.SavedTerm
import com.nallanudi.app.data.model.Term
import kotlinx.coroutines.flow.Flow

class DictionaryRepository(
    private val termDao: TermDao,
    private val savedTermDao: SavedTermDao
) {
    fun searchTerms(query: String): Flow<List<Term>> =
        if (query.isBlank()) termDao.getAllTerms() else termDao.searchTerms(query.trim())

    fun getTermsBySubject(subject: String): Flow<List<Term>> =
        termDao.getTermsBySubject(subject)

    fun getAllTerms(): Flow<List<Term>> = termDao.getAllTerms()

    suspend fun getWordOfTheDay(): Term? = termDao.getWordOfTheDay()

    suspend fun getTermById(id: Int): Term? = termDao.getTermById(id)

    fun getSavedTerms(): Flow<List<Term>> = savedTermDao.getSavedTerms()

    fun getSavedCount(): Flow<Int> = savedTermDao.getSavedCount()

    suspend fun toggleSave(term: Term) {
        val existing = savedTermDao.isSaved(term.id)
        if (existing != null) savedTermDao.remove(existing)
        else savedTermDao.save(SavedTerm(termId = term.id))
    }

    suspend fun isSaved(termId: Int): Boolean = savedTermDao.isSaved(termId) != null
}