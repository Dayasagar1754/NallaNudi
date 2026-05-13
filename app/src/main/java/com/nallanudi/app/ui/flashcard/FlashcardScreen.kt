package com.nallanudi.app.ui.flashcard

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nallanudi.app.theme.*
import com.nallanudi.app.viewmodel.DictionaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    viewModel: DictionaryViewModel,
    onBack: () -> Unit
) {
    val allSaved by viewModel.savedTerms.collectAsState()
    var terms by remember { mutableStateOf(allSaved.shuffled()) }
    var index by remember { mutableIntStateOf(0) }
    var showFront by remember { mutableStateOf(true) }

    LaunchedEffect(allSaved) {
        if (terms.isEmpty()) terms = allSaved.shuffled()
    }

    val rotation by animateFloatAsState(
        targetValue = if (showFront) 0f else 180f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "flip"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flashcard Revision") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        terms = terms.shuffled()
                        index = 0
                        showFront = true
                    }) { Icon(Icons.Filled.Shuffle, "Shuffle") }
                    IconButton(onClick = {
                        if (terms.isNotEmpty()) viewModel.speak(terms[index].englishTerm)
                    }) { Icon(Icons.Filled.VolumeUp, "Speak") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Saffron,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (terms.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center) {
                Text("Save some words first!", color = TextSecondary)
            }
            return@Scaffold
        }

        val term = terms[index]

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("${index + 1} / ${terms.size}",
                fontSize = 14.sp, color = TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp))

            LinearProgressIndicator(
                progress = { (index + 1).toFloat() / terms.size },
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                color = Saffron
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 12f * density
                    }
                    .clickable { showFront = !showFront },
                contentAlignment = Alignment.Center
            ) {
                if (rotation <= 90f) {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Saffron),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(32.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("English Term", fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.padding(bottom = 16.dp))
                            Text(term.englishTerm, fontSize = 36.sp,
                                fontWeight = FontWeight.Bold, color = Color.White,
                                textAlign = TextAlign.Center)
                            Spacer(Modifier.height(12.dp))
                            AssistChip(
                                onClick = {},
                                label = { Text(term.subject, color = Saffron) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = Color.White)
                            )
                            Spacer(Modifier.height(24.dp))
                            Text("ಕನ್ನಡದಲ್ಲಿ ಅರ್ಥ ಏನು?\nTap to reveal",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { rotationY = 180f },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("ಕನ್ನಡ ಅರ್ಥ", fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(bottom = 8.dp))
                            Text(term.kannadaScript, fontSize = 28.sp,
                                fontWeight = FontWeight.Bold, color = Saffron,
                                textAlign = TextAlign.Center)
                            HorizontalDivider(modifier = Modifier
                                .padding(vertical = 12.dp).width(48.dp))
                            Text(term.kannadaExplanation, fontSize = 14.sp,
                                lineHeight = 20.sp, textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 8.dp))
                            Text(term.kannadaExample, fontSize = 13.sp,
                                color = TextSecondary, textAlign = TextAlign.Center,
                                fontStyle = FontStyle.Italic)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { if (index > 0) { index--; showFront = true } },
                    enabled = index > 0,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) { Text("← ಹಿಂದೆ") }

                Button(
                    onClick = {
                        if (index < terms.size - 1) { index++; showFront = true }
                    },
                    enabled = index < terms.size - 1,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Saffron)
                ) { Text("ಮುಂದೆ →") }
            }
        }
    }
}