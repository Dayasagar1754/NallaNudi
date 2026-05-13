package com.nallanudi.app.data.db

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nallanudi.app.data.model.SavedTerm
import com.nallanudi.app.data.model.Term
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Term::class, SavedTerm::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun termDao(): TermDao
    abstract fun savedTermDao(): SavedTermDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nallanudi.db"
                )
                    .addCallback(SeedCallback(context))
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }

    private class SeedCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                val database = getInstance(context)
                if (database.termDao().getCount() == 0) {
                    try {
                        val json = context.assets.open("glossary.json")
                            .bufferedReader().use { it.readText() }
                        val type = object : TypeToken<List<Term>>() {}.type
                        val terms: List<Term> = Gson().fromJson(json, type)
                        database.termDao().insertAll(terms)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}