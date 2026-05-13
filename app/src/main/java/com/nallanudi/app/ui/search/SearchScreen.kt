package com.nallanudi.app.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nallanudi.app.data.model.Subject
import com.nallanudi.app.data.model.Term
import com.nallanudi.app.theme.*
import com.nallanudi.app.viewmodel.DictionaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: DictionaryViewModel,
    onTermClick: (Int) -> Unit
) {
    val query by viewModel.searchQuery.collectAsState()
    val results by viewModel.searchResults.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val savedIds by viewModel.savedTermIds.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            placeholder = { Text("Search English term... (e.g. Gravity)") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            shape = RoundedCornerShape(28.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Saffron,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            items(Subject.values()) { subject ->
                FilterChip(
                    selected = selectedSubject == subject,
                    onClick = { viewModel.setSubjectFilter(subject) },
                    label = { Text("${subject.kannadaName} / ${subject.displayName}") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Saffron,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Text(
            text = "${results.size} results",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
        )

        if (results.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔎", fontSize = 48.sp)
                    Text("ಯಾವ ಪದವೂ ಸಿಗಲಿಲ್ಲ\nNo terms found",
                        color = TextSecondary, fontSize = 16.sp)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(results, key = { it.id }) { term ->
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

@Composable
fun TermCard(
    term: Term,
    isSaved: Boolean,
    onClick: () -> Unit,
    onSpeak: () -> Unit,
    onSave: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = term.englishTerm,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                AssistChip(
                    onClick = {},
                    label = { Text(term.subject, fontSize = 10.sp) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (term.subject) {
                            "SCIENCE"  -> ScienceGreen.copy(alpha = 0.1f)
                            "MATH"     -> MathBlue.copy(alpha = 0.1f)
                            "COMMERCE" -> CommerceViolet.copy(alpha = 0.1f)
                            else       -> Saffron.copy(alpha = 0.1f)
                        }
                    )
                )
            }
            Text(
                text = term.kannadaScript,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Saffron,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            Text(
                text = term.pronunciationHint,
                fontSize = 12.sp,
                color = TextSecondary,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = term.kannadaExplanation,
                fontSize = 13.sp,
                color = TextPrimary,
                lineHeight = 18.sp,
                maxLines = 2,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onSpeak,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Filled.VolumeUp, null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Speak", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = onSave,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        null,
                        modifier = Modifier.size(14.dp),
                        tint = if (isSaved) Saffron else TextSecondary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(if (isSaved) "Saved" else "Save", fontSize = 12.sp)
                }
            }
        }
    }
}