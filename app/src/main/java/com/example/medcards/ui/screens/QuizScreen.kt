package com.example.medcards.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medcards.data.model.QuizQuestion
import com.example.medcards.ui.components.IllustrationType
import com.example.medcards.ui.components.MedicalIllustration
import com.example.medcards.ui.theme.CardWhite
import com.example.medcards.ui.theme.CobaltBlue
import com.example.medcards.ui.theme.CobaltBlueContainer
import com.example.medcards.ui.theme.ErrorRed
import com.example.medcards.ui.theme.LightColdGrey
import com.example.medcards.ui.theme.MintGreen
import com.example.medcards.ui.theme.TextDark
import com.example.medcards.ui.theme.TextMuted

@Composable
fun QuizScreen(
	sessionId: Int,
	questions: List<QuizQuestion>,
	onAnswer: (QuizQuestion, Boolean) -> Unit,
	onFinish: () -> Unit
) {
	val sessionKey = remember(questions) { questions.joinToString("|") { it.id } }
	var questionIndex by rememberSaveable(sessionId, sessionKey) { mutableIntStateOf(0) }
	var selectedOptionIndex by rememberSaveable(sessionId, sessionKey) { mutableIntStateOf(-1) }
	var correctCount by rememberSaveable(sessionId, sessionKey) { mutableIntStateOf(0) }
	val question = questions.getOrNull(questionIndex)

	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(LightColdGrey)
			.verticalScroll(rememberScrollState())
			.padding(20.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		Text("Test de recuperación activa", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextDark)
		if (question == null) {
			MedicalIllustration(IllustrationType.Analytics, size = 92.dp)
			Text(
				if (questions.isEmpty()) "No hay preguntas disponibles todavía." else "Sesión completada",
				style = MaterialTheme.typography.titleLarge,
				fontWeight = FontWeight.Bold,
				color = TextDark
			)
			if (questions.isNotEmpty()) {
				Text("Resultado: $correctCount de ${questions.size} respuestas correctas.", color = TextMuted)
				LinearProgressIndicator(
					progress = { correctCount.toFloat() / questions.size },
					modifier = Modifier.fillMaxWidth(),
					color = MintGreen,
					trackColor = CobaltBlueContainer
				)
			} else {
				Text("Las nuevas preguntas aparecen aquí cuando estén disponibles en el banco de estudio.", color = TextMuted)
			}
			Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) {
				Text("Volver al inicio")
			}
		} else {
			Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
				Text("${questionIndex + 1} de ${questions.size}", color = CobaltBlue, fontWeight = FontWeight.Bold)
				Text(question.subject, color = TextMuted)
			}
			LinearProgressIndicator(
				progress = { (questionIndex + 1).toFloat() / questions.size },
				modifier = Modifier.fillMaxWidth(),
				color = CobaltBlue,
				trackColor = CobaltBlueContainer
			)
			Card(
				modifier = Modifier.fillMaxWidth(),
				shape = RoundedCornerShape(18.dp),
				colors = CardDefaults.cardColors(containerColor = CardWhite)
			) {
				Column(
					modifier = Modifier.padding(20.dp),
					verticalArrangement = Arrangement.spacedBy(14.dp)
				) {
					Text(question.topic.uppercase(), style = MaterialTheme.typography.labelMedium, color = CobaltBlue, fontWeight = FontWeight.Bold)
					Text(question.question, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = TextDark)
					question.options.forEachIndexed { optionIndex, option ->
						val isSelected = selectedOptionIndex == optionIndex
						val isCorrectOption = question.correctOptionIndex == optionIndex
						val optionColor = when {
							selectedOptionIndex >= 0 && isCorrectOption -> MintGreen.copy(alpha = 0.15f)
							isSelected -> ErrorRed.copy(alpha = 0.13f)
							else -> LightColdGrey
						}
						Card(
							onClick = {
								if (selectedOptionIndex == -1) {
									selectedOptionIndex = optionIndex
									val wasCorrect = optionIndex == question.correctOptionIndex
									if (wasCorrect) correctCount++
									onAnswer(question, wasCorrect)
								}
							},
							modifier = Modifier.fillMaxWidth(),
							shape = RoundedCornerShape(12.dp),
							colors = CardDefaults.cardColors(containerColor = optionColor)
						) {
							Text(
								text = "${'A' + optionIndex}.  $option",
								modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
								color = TextDark,
								style = MaterialTheme.typography.bodyLarge
							)
						}
					}
					if (selectedOptionIndex >= 0) {
						val wasCorrect = selectedOptionIndex == question.correctOptionIndex
						Surface(
							shape = RoundedCornerShape(12.dp),
							color = if (wasCorrect) MintGreen.copy(alpha = 0.12f) else ErrorRed.copy(alpha = 0.1f)
						) {
							Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
								Text(if (wasCorrect) "Correcto" else "Repasa este concepto", fontWeight = FontWeight.Bold, color = if (wasCorrect) MintGreen else ErrorRed)
								Text(question.explanation, color = TextDark, style = MaterialTheme.typography.bodyMedium)
							}
						}
					}
				}
			}
			Spacer(Modifier.height(2.dp))
			Button(
				onClick = {
					questionIndex++
					selectedOptionIndex = -1
				},
				enabled = selectedOptionIndex >= 0,
				modifier = Modifier.fillMaxWidth()
			) {
				Text(if (questionIndex == questions.lastIndex) "Ver resultado" else "Siguiente pregunta")
			}
		}
	}
}
