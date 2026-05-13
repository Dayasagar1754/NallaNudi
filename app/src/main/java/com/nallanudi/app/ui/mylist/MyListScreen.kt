package com.nallanudi.app.ui.mylist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nallanudi.app.theme.*
import com.nallanudi.app.ui.search.TermCard
import com.nallanudi.app.viewmodel.DictionaryViewModel

@Composable
fun MyListScreen(
    viewModel: DictionaryViewModel,
    onTermClick: (Int) -> Unit,
    onFlashcardsClick: () -> Unit
) {
    val savedTerms by viewModel.savedTerms.collectAsState()
    val savedIds by viewModel.savedTermIds.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("📚 ನನ್ನ ಪಟ್ಟಿ", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("${savedTerms.size} words saved",
                    fontSize = 13.sp, color = TextSecondary)
            }
            if (savedTerms.isNotEmpty()) {
                Button(
                    onClick = onFlashcardsClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Saffron)
                ) {
                    Text("▶ Flashcards")
                }
            }
        }

        if (savedTerms.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)) {
                    Text("📖", fontSize = 56.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("ಇನ್ನೂ ಯಾವ ಪದವೂ ಉಳಿಸಿಲ್ಲ",
                        fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("Search a word and tap Save\nto add it here for revision.",
                        fontSize = 14.sp, color = TextSecondary)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(savedTerms, key = { it.id }) { term ->
                    TermCard(
                        term = term,
                        isSaved = term.id in savedIds,
                        onClick = { onTermClick(term.id) },
                        onSpeak = { viewModel.speak(term.englishTerm) },
                        onSave = { viewModel.toggleSave(term) }
                    )
                }
            }
        }
    }
}