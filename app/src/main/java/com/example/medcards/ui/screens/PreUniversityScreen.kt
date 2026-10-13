package com.example.medcards.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medcards.data.model.QuizQuestion
import com.example.medcards.data.repository.SubjectStats
import com.example.medcards.ui.components.IllustrationType
import com.example.medcards.ui.components.MedicalIllustration
import com.example.medcards.ui.components.illustrationTypeForSubject

@Composable
fun PreUniversityScreen(
    subjects: List<SubjectStats>,
    quizQuestions: List<QuizQuestion>,
    onStudyCards: (String) -> Unit,
    onStartQuiz: (String) -> Unit
) {
    StudyPage(
        title = "Preuniversitario de medicina",
        subtitle = "Bases científicas para prepararte: estudia tarjetas o practica con preguntas.",
        illustration = IllustrationType.PreMedicine
    ) {
        if (subjects.isEmpty()) {
            Text(
                text = "Todavía no hay materias preuniversitarias en la base de estudio.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            subjects.forEach { subject ->
                val questionCount = quizQuestions.count { it.subject == subject.subject }
                PreUniversitySubjectCard(
                    subject = subject.subject,
                    cards = subject.total,
                    quizQuestions = questionCount,
                    onStudyCards = { onStudyCards(subject.subject) },
                    onStartQuiz = { onStartQuiz(subject.subject) }
                )
            }
        }
    }
}

@Composable
private fun PreUniversitySubjectCard(
    subject: String,
    cards: Int,
    quizQuestions: Int,
    onStudyCards: () -> Unit,
    onStartQuiz: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(
                        text = subject,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$cards tarjetas · $quizQuestions preguntas de práctica",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                MedicalIllustration(type = illustrationTypeForSubject(subject), size = 62.dp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onStudyCards,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Estudiar tarjetas")
                }
                OutlinedButton(
                    onClick = onStartQuiz,
                    modifier = Modifier.weight(1f),
                    enabled = quizQuestions > 0
                ) {
                    Text("Practicar test")
                }
            }
        }
    }
}
