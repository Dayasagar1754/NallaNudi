package com.nallanudi.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_terms")
data class SavedTerm(
    @PrimaryKey
    val termId: Int,
    val savedAt: Long = System.currentTimeMillis()
)