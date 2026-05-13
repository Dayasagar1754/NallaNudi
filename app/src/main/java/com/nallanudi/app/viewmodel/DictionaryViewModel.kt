package com.nallanudi.app.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nallanudi.app.data.db.AppDatabase
import com.nallanudi.app.data.model.Subject
import com.nallanudi.app.data.model.Term
import com.nallanudi.app.data.repository.DictionaryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class DictionaryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = DictionaryRepository(db.termDao(), db.savedTermDao())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedSubject = MutableStateFlow(Subject.ALL)
    val selectedSubject: StateFlow<Subject> = _selectedSubject.asStateFlow()

    val searchResults: StateFlow<List<Term>> = combine(
        _searchQuery, _selectedSubject
    ) { query, subject -> Pair(query, subject) }
        .flatMapLatest { (query, subject) ->
            when {
                subject != Subject.ALL && query.isBlank() ->
                    repository.getTermsBySubject(subject.name)
                subject != Subject.ALL && query.isNotBlank() ->
                    repository.searchTerms(query).map { list ->
                        list.filter { it.subject == subject.name }
                    }
                else -> repository.searchTerms(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _wordOfTheDay = MutableStateFlow<Term?>(null)
    val wordOfTheDay: StateFlow<Term?> = _wordOfTheDay.asStateFlow()

    init {
        viewModelScope.launch {
            _wordOfTheDay.value = repository.getWordOfTheDay()
        }
    }

    val savedTerms: StateFlow<List<Term>> = repository.getSavedTerms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedCount: StateFlow<Int> = repository.getSavedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _savedTermIds = MutableStateFlow<Set<Int>>(emptySet())
    val savedTermIds: StateFlow<Set<Int>> = _savedTermIds.asStateFlow()

    init {
        viewModelScope.launch {
            savedTerms.collect { terms ->
                _savedTermIds.value = terms.map { it.id }.toSet()
            }
        }
    }

    fun toggleSave(term: Term) {
        viewModelScope.launch { repository.toggleSave(term) }
    }

    fun setSearchQuery(q: String) { _searchQuery.value = q }
    fun setSubjectFilter(s: Subject) { _selectedSubject.value = s }

    private val _currentTerm = MutableStateFlow<Term?>(null)
    val currentTerm: StateFlow<Term?> = _currentTerm.asStateFlow()

    fun loadTerm(id: Int) {
        viewModelScope.launch {
            _currentTerm.value = repository.getTermById(id)
        }
    }

    private var tts: TextToSpeech? = null
    private var ttsReady = false

    fun initTts() {
        tts = TextToSpeech(getApplication()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                ttsReady = true
            }
        }
    }

    fun speak(text: String) {
        if (ttsReady) tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_id")
    }

    override fun onCleared() {
        tts?.stop()
        tts?.shutdown()
        super.onCleared()
    }
}