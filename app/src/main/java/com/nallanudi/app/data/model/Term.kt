package com.nallanudi.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "terms")
data class Term(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val englishTerm: String,
    val kannadaScript: String,
    val kannadaTerm: String,
    val kannadaExplanation: String,
    val kannadaExample: String,
    val subject: String,
    val pronunciationHint: String,
    val isFeatured: Boolean = false
)

enum class Subject(val displayName: String, val kannadaName: String) {
    ALL("All", "ಎಲ್ಲಾ"),
    SCIENCE("Science", "ವಿಜ್ಞಾನ"),
    MATH("Math", "ಗಣಿತ"),
    COMMERCE("Commerce", "ವಾಣಿಜ್ಯ"),
    ENGLISH("English", "ಇಂಗ್ಲಿಷ್")
}