package com.nallanudi.app.data.db

import androidx.room.*
import com.nallanudi.app.data.model.Term
import kotlinx.coroutines.flow.Flow

@Dao
interface TermDao {

    @Query("""
        SELECT * FROM terms
        WHERE englishTerm LIKE '%' || :query || '%'
        ORDER BY
          CASE WHEN englishTerm LIKE :query || '%' THEN 0 ELSE 1 END,
          englishTerm ASC
        LIMIT 100
    """)
    fun searchTerms(query: String): Flow<List<Term>>

    @Query("SELECT * FROM terms WHERE subject = :subject ORDER BY englishTerm ASC")
    fun getTermsBySubject(subject: String): Flow<List<Term>>

    @Query("SELECT * FROM terms ORDER BY englishTerm ASC")
    fun getAllTerms(): Flow<List<Term>>

    @Query("SELECT * FROM terms WHERE isFeatured = 1 ORDER BY RANDOM() LIMIT 1")
    suspend fun getWordOfTheDay(): Term?

    @Query("SELECT * FROM terms WHERE id = :id LIMIT 1")
    suspend fun getTermById(id: Int): Term?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(terms: List<Term>)

    @Query("SELECT COUNT(*) FROM terms")
    suspend fun getCount(): Int
}