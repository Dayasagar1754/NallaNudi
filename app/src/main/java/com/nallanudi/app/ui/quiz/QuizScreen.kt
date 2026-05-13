package com.nallanudi.app.ui.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nallanudi.app.data.model.Term
import com.nallanudi.app.theme.Saffron
import com.nallanudi.app.theme.TextPrimary
import com.nallanudi.app.theme.TextSecondary
import com.nallanudi.app.viewmodel.DictionaryViewModel

val CorrectGreen = Color(0xFF2E7D32)
val WrongRed    = Color(0xFFC62828)

data class QuizQuestion(
    val term: Term,
    val options: List<String>,
    val correctIndex: Int
)

fun buildQuiz(allTerms: List<Term>, questionCount: Int = 10): List<QuizQuestion> {
    if (allTerms.size < 4) return emptyList()
    val pool = allTerms.shuffled().take(questionCount.coerceAtMost(allTerms.size))
    return pool.map { term ->
        val wrongOptions = allTerms
            .filter { it.id != term.id }
            .shuffled()
            .take(3)
            .map { it.kannadaScript }
        val allOptions = (wrongOptions + term.kannadaScript).shuffled()
        QuizQuestion(
            term = term,
            options = allOptions,
            correctIndex = allOptions.indexOf(term.kannadaScript)
        )
    }
}

sealed class QuizState {
    object Loading : QuizState()
    object NotEnoughTerms : QuizState()
    data class Playing(val questions: List<QuizQuestion>) : QuizState()
    data class Result(val score: Int, val total: Int) : QuizState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: DictionaryViewModel,
    onBack: () -> Unit
) {
    val allTerms by viewModel.searchResults.collectAsState()
    val savedTerms by viewModel.savedTerms.collectAsState()

    val termPool = remember(savedTerms, allTerms) {
        if (savedTerms.size >= 4) savedTerms else allTerms
    }

    var quizState by remember { mutableStateOf<QuizState>(QuizState.Loading) }

    LaunchedEffect(Unit) { viewModel.setSearchQuery("") }

    LaunchedEffect(termPool) {
        if (termPool.size >= 4) {
            val questions = buildQuiz(termPool)
            quizState = if (questions.isEmpty()) QuizState.NotEnoughTerms
            else QuizState.Playing(questions)
        } else if (termPool.isNotEmpty()) {
            quizState = QuizState.NotEnoughTerms
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Mode — ರಸಪ್ರಶ್ನೆ") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
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
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = quizState) {
                QuizState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Saffron)
                            Spacer(Modifier.height(12.dp))
                            Text("ಪ್ರಶ್ನೆಗಳನ್ನು ತಯಾರಿಸುತ್ತಿದ್ದೇವೆ...",
                                color = TextSecondary, fontSize = 14.sp)
                        }
                    }
                }
                QuizState.NotEnoughTerms -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("📚", fontSize = 64.sp)
                        Spacer(Modifier.height(16.dp))
                        Text("ಕನಿಷ್ಠ 4 ಪದಗಳು ಬೇಕು",
                            fontSize = 18.sp, fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center)
                        Spacer(Modifier.height(8.dp))
                        Text("Quiz ಆಡಲು ಕನಿಷ್ಠ 4 ಪದಗಳಿರಬೇಕು.\nSearch ನಲ್ಲಿ ಪದಗಳನ್ನು ಉಳಿಸಿ ನಂತರ ಬನ್ನಿ.",
                            fontSize = 14.sp, color = TextSecondary,
                            textAlign = TextAlign.Center, lineHeight = 20.sp)
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = onBack,
                            colors = ButtonDefaults.buttonColors(containerColor = Saffron)) {
                            Text("← ಹಿಂದೆ ಹೋಗಿ")
                        }
                    }
                }
                is QuizState.Playing -> {
                    QuizPlayScreen(
                        questions = state.questions,
                        onFinish = { score ->
                            quizState = QuizState.Result(score, state.questions.size)
                        },
                        onSpeak = { viewModel.speak(it) }
                    )
                }
                is QuizState.Result -> {
                    QuizResultScreen(
                        score = state.score,
                        total = state.total,
                        onRetry = {
                            val questions = buildQuiz(termPool)
                            quizState = QuizState.Playing(questions)
                        },
                        onBack = onBack
                    )
                }
            }
        }
    }
}

@Composable
fun QuizPlayScreen(
    questions: List<QuizQuestion>,
    onFinish: (Int) -> Unit,
    onSpeak: (String) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var showAnswer by remember { mutableStateOf(false) }

    val question = questions[currentIndex]

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / questions.size },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            color = Saffron
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("ಪ್ರಶ್ನೆ ${currentIndex + 1} / ${questions.size}",
                fontSize = 14.sp, color = TextSecondary)
            Text("ಅಂಕ: $score", fontSize = 14.sp,
                color = Saffron, fontWeight = FontWeight.Bold)
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Saffron),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("ಈ ಪದದ ಕನ್ನಡ ಅರ್ಥ ಯಾವುದು?",
                    fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 12.dp))
                Text(question.term.englishTerm,
                    fontSize = 32.sp, fontWeight = FontWeight.Bold,
                    color = Color.White, textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
                Text(question.term.subject, fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f))
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { onSpeak(question.term.englishTerm) },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White)
                ) {
                    Text("🔊 Pronounce", fontSize = 12.sp, color = Color.White)
                }
            }
        }

        question.options.forEachIndexed { index, option ->
            QuizOptionButton(
                text = option,
                index = index,
                selectedIndex = selectedOption,
                correctIndex = question.correctIndex,
                showAnswer = showAnswer,
                onClick = {
                    if (!showAnswer) {
                        selectedOption = index
                        showAnswer = true
                        if (index == question.correctIndex) score++
                    }
                }
            )
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.weight(1f))

        AnimatedVisibility(visible = showAnswer) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedOption == question.correctIndex)
                        Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (selectedOption == question.correctIndex)
                            "✅ ಸರಿ! Correct!" else "❌ ತಪ್ಪು! Wrong!",
                        fontWeight = FontWeight.Bold,
                        color = if (selectedOption == question.correctIndex)
                            CorrectGreen else WrongRed,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text("ಸರಿ ಉತ್ತರ: ${question.term.kannadaScript}",
                        fontSize = 13.sp, color = TextPrimary)
                    Text(question.term.kannadaExplanation,
                        fontSize = 12.sp, color = TextSecondary,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(top = 4.dp))
                }
            }
        }

        Button(
            onClick = {
                if (currentIndex < questions.size - 1) {
                    currentIndex++
                    selectedOption = null
                    showAnswer = false
                } else {
                    onFinish(score)
                }
            },
            enabled = showAnswer,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Saffron,
                disabledContainerColor = Color.LightGray
            )
        ) {
            Text(
                if (currentIndex < questions.size - 1) "ಮುಂದೆ → Next"
                else "ಫಲಿತಾಂಶ → Result",
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun QuizOptionButton(
    text: String,
    index: Int,
    selectedIndex: Int?,
    correctIndex: Int,
    showAnswer: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        !showAnswer -> Color.White
        index == correctIndex -> Color(0xFFE8F5E9)
        index == selectedIndex -> Color(0xFFFFEBEE)
        else -> Color.White
    }
    val textColor = when {
        !showAnswer -> TextPrimary
        index == correctIndex -> CorrectGreen
        index == selectedIndex -> WrongRed
        else -> TextSecondary
    }
    val letterColor = when {
        showAnswer && index == correctIndex -> CorrectGreen
        showAnswer && index == selectedIndex -> WrongRed
        else -> Saffron
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = listOf("A", "B", "C", "D")[index],
                fontWeight = FontWeight.Bold,
                color = letterColor,
                fontSize = 14.sp,
                modifier = Modifier.width(24.dp)
            )
            Text(text = text, fontSize = 15.sp,
                color = textColor, modifier = Modifier.weight(1f))
            if (showAnswer) {
                when {
                    index == correctIndex ->
                        Icon(Icons.Filled.Check, null,
                            tint = CorrectGreen, modifier = Modifier.size(18.dp))
                    index == selectedIndex ->
                        Icon(Icons.Filled.Close, null,
                            tint = WrongRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun QuizResultScreen(
    score: Int,
    total: Int,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    val percentage = (score.toFloat() / total * 100).toInt()
    val emoji = when {
        percentage >= 90 -> "🏆"
        percentage >= 70 -> "🌟"
        percentage >= 50 -> "👍"
        else -> "📚"
    }
    val message = when {
        percentage >= 90 -> "ಅದ್ಭುತ! Excellent!"
        percentage >= 70 -> "ತುಂಬಾ ಚೆನ್ನಾಗಿದೆ! Very Good!"
        percentage >= 50 -> "ಚೆನ್ನಾಗಿದೆ! Good!"
        else -> "ಇನ್ನಷ್ಟು ಓದಿ! Keep Studying!"
    }
    val cardColor = when {
        percentage >= 70 -> CorrectGreen
        percentage >= 50 -> Saffron
        else -> Color(0xFF1565C0)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(emoji, fontSize = 72.sp)
        Spacer(Modifier.height(16.dp))
        Text(message, fontSize = 22.sp, fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("ನಿಮ್ಮ ಅಂಕ", fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f))
                Text("$score / $total", fontSize = 48.sp,
                    fontWeight = FontWeight.Bold, color = Color.White)
                Text("$percentage%", fontSize = 22.sp,
                    color = Color.White.copy(alpha = 0.9f))
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuizStatCard("✅ ಸರಿ", "$score", CorrectGreen, Modifier.weight(1f))
            QuizStatCard("❌ ತಪ್ಪು", "${total - score}", WrongRed, Modifier.weight(1f))
            QuizStatCard("📊 ಶೇ", "$percentage%", Saffron, Modifier.weight(1f))
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Saffron)
        ) {
            Text("🔄  ಮತ್ತೆ ಆಡಿ / Play Again", fontSize = 15.sp)
        }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("← ಹಿಂದೆ / Go Back", fontSize = 15.sp)
        }
    }
}

@Composable
fun QuizStatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 11.sp, color = TextSecondary,
                textAlign = TextAlign.Center)
        }
    }
}