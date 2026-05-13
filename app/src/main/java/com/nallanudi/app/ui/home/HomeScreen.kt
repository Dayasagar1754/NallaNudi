package com.nallanudi.app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nallanudi.app.theme.*
import com.nallanudi.app.viewmodel.DictionaryViewModel

@Composable
fun HomeScreen(
    viewModel: DictionaryViewModel,
    onTermClick: (Int) -> Unit,
    onSearchClick: () -> Unit
) {
    val wordOfTheDay by viewModel.wordOfTheDay.collectAsState()
    val savedCount by viewModel.savedCount.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "ನಲ್ಲ ನುಡಿ",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Saffron
        )
        Text(
            text = "Nalla Nudi — Bridge Dictionary",
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        wordOfTheDay?.let { term ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable { onTermClick(term.id) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Saffron)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "📖 ಇಂದಿನ ಪದ  /  Word of the Day",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = term.englishTerm,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = term.kannadaScript,
                        fontSize = 20.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = term.kannadaExplanation,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SuggestionChip(
                            onClick = { viewModel.speak(term.englishTerm) },
                            label = { Text("🔊 Pronounce", color = Saffron) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = Color.White
                            )
                        )
                        SuggestionChip(
                            onClick = { viewModel.toggleSave(term) },
                            label = { Text("+ Save", color = Saffron) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clickable { onSearchClick() },
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Search, contentDescription = null, tint = TextSecondary)
                Spacer(Modifier.width(12.dp))
                Text("Search any English term...", color = TextSecondary, fontSize = 15.sp)
            }
        }

        Text(
            text = "ವಿಷಯ ಆರಿಸಿ  /  Browse by Subject",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SubjectButton("🔬 ವಿಜ್ಞಾನ", ScienceGreen, Modifier.weight(1f)) { onSearchClick() }
            SubjectButton("📐 ಗಣಿತ", MathBlue, Modifier.weight(1f)) { onSearchClick() }
            SubjectButton("📊 ವಾಣಿಜ್ಯ", CommerceViolet, Modifier.weight(1f)) { onSearchClick() }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📚 ನನ್ನ ಪಟ್ಟಿ  /  My List",
                    fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (savedCount > 0) "$savedCount words saved for revision"
                    else "No saved words yet — tap + Save on any term",
                    fontSize = 14.sp, color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun SubjectButton(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)
    ) {
        Text(label, fontSize = 12.sp, color = Color.White)
    }
}