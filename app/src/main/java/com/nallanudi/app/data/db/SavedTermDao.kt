package com.nallanudi.app.data.db

import androidx.room.*
import com.nallanudi.app.data.model.SavedTerm
import com.nallanudi.app.data.model.Term
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedTermDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(savedTerm: SavedTerm)

    @Delete
    suspend fun remove(savedTerm: SavedTerm)

    @Query("SELECT * FROM saved_terms WHERE termId = :termId LIMIT 1")
    suspend fun isSaved(termId: Int): SavedTerm?

    @Query("""
        SELECT t.* FROM terms t
        INNER JOIN saved_terms s ON t.id = s.termId
        ORDER BY s.savedAt DESC
    """)
    fun getSavedTerms(): Flow<List<Term>>

    @Query("SELECT COUNT(*) FROM saved_terms")
    fun getSavedCount(): Flow<Int>
}