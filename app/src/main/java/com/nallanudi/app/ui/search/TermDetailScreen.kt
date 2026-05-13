package com.nallanudi.app.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nallanudi.app.theme.*
import com.nallanudi.app.viewmodel.DictionaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermDetailScreen(
    termId: Int,
    viewModel: DictionaryViewModel,
    onBack: () -> Unit
) {
    val term by viewModel.currentTerm.collectAsState()
    val savedIds by viewModel.savedTermIds.collectAsState()

    LaunchedEffect(termId) { viewModel.loadTerm(termId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(term?.englishTerm ?: "", maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Saffron,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        term?.let { t ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Saffron)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        AssistChip(
                            onClick = {},
                            label = { Text(t.subject, color = Saffron) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color.White),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(t.englishTerm, fontSize = 32.sp,
                            fontWeight = FontWeight.Bold, color = Color.White)
                        Text(t.pronunciationHint, fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.speak(t.englishTerm) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White, contentColor = Saffron)
                            ) {
                                Icon(Icons.Filled.VolumeUp, null,
                                    modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Pronounce")
                            }
                            Button(
                                onClick = { viewModel.toggleSave(t) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White, contentColor = Saffron)
                            ) {
                                Icon(
                                    if (t.id in savedIds) Icons.Filled.Bookmark
                                    else Icons.Filled.BookmarkBorder,
                                    null, modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(if (t.id in savedIds) "Saved" else "Save")
                            }
                        }
                    }
                }

                Card(shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("ಕನ್ನಡ ಪದ  /  Kannada Term",
                            fontSize = 12.sp, color = TextSecondary)
                        Spacer(Modifier.height(6.dp))
                        Text(t.kannadaScript, fontSize = 26.sp,
                            fontWeight = FontWeight.Bold, color = Saffron)
                        Text(t.kannadaTerm, fontSize = 14.sp, color = TextSecondary)
                    }
                }

                Card(shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("ಸರಳ ವಿವರಣೆ  /  Simple Explanation",
                            fontSize = 12.sp, color = TextSecondary)
                        Spacer(Modifier.height(8.dp))
                        Text(t.kannadaExplanation, fontSize = 15.sp,
                            lineHeight = 22.sp, color = TextPrimary)
                    }
                }

                Card(shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF8E1))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("ಉದಾಹರಣೆ  /  Example",
                            fontSize = 12.sp, color = TextSecondary)
                        Spacer(Modifier.height(8.dp))
                        Text("📌  ${t.kannadaExample}", fontSize = 14.sp,
                            lineHeight = 20.sp, color = TextPrimary,
                            fontStyle = FontStyle.Italic)
                    }
                }
            }
        } ?: Box(Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Saffron)
        }
    }
}