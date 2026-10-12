package com.example.medcards.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.medcards.ui.theme.CardWhite
import com.example.medcards.ui.theme.CobaltBlue
import com.example.medcards.ui.theme.CobaltBlueContainer
import com.example.medcards.ui.theme.DifficultyMastered
import com.example.medcards.ui.theme.ErrorRed
import com.example.medcards.ui.theme.LightColdGrey
import com.example.medcards.ui.theme.MintGreen
import com.example.medcards.ui.theme.TextDark
import com.example.medcards.ui.theme.TextMuted
import com.example.medcards.ui.theme.WarningAmber

enum class IllustrationType {
    Learning,
    Anatomy,
    Pharmacology,
    Histology,
    Biology,
    Chemistry,
    Physics,
    PreMedicine,
    Empty,
    Analytics,
    Calendar,
    Premium
}

@Composable
fun MedicalIllustration(
    type: IllustrationType,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val scaleFactor = minOf(this.size.width, this.size.height) / 100f
        val offsetX = (this.size.width - 100f * scaleFactor) / 2f
        val offsetY = (this.size.height - 100f * scaleFactor) / 2f
        translate(left = offsetX, top = offsetY) {
            scale(scaleFactor, scaleFactor, pivot = Offset.Zero) {
                drawCircle(CobaltBlueContainer, radius = 46f, center = Offset(50f, 50f))
                when (type) {
                    IllustrationType.Learning -> drawLearningIllustration()
                    IllustrationType.Anatomy -> drawAnatomyIllustration()
                    IllustrationType.Pharmacology -> drawPharmacologyIllustration()
                    IllustrationType.Histology -> drawHistologyIllustration()
                    IllustrationType.Biology -> drawBiologyIllustration()
                    IllustrationType.Chemistry -> drawChemistryIllustration()
                    IllustrationType.Physics -> drawPhysicsIllustration()
                    IllustrationType.PreMedicine -> drawPreMedicineIllustration()
                    IllustrationType.Empty -> drawEmptyIllustration()
                    IllustrationType.Analytics -> drawAnalyticsIllustration()
                    IllustrationType.Calendar -> drawCalendarIllustration()
                    IllustrationType.Premium -> drawPremiumIllustration()
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLearningIllustration() {
    drawRoundRect(
        color = CardWhite,
        topLeft = Offset(18f, 20f),
        size = Size(64f, 62f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f)
    )
    drawRoundRect(CobaltBlue, Offset(27f, 29f), Size(22f, 4f), androidx.compose.ui.geometry.CornerRadius(2f))
    drawRoundRect(TextMuted.copy(alpha = 0.25f), Offset(27f, 39f), Size(32f, 3f), androidx.compose.ui.geometry.CornerRadius(2f))
    drawRoundRect(TextMuted.copy(alpha = 0.25f), Offset(27f, 47f), Size(25f, 3f), androidx.compose.ui.geometry.CornerRadius(2f))
    drawRoundRect(MintGreen, Offset(27f, 61f), Size(36f, 10f), androidx.compose.ui.geometry.CornerRadius(5f))
    drawCircle(WarningAmber, 13f, Offset(70f, 31f))
    drawCircle(CardWhite, 8f, Offset(70f, 31f))
    drawLine(CobaltBlue, Offset(70f, 25f), Offset(70f, 37f), 2.5f, cap = StrokeCap.Round)
    drawLine(CobaltBlue, Offset(64f, 31f), Offset(76f, 31f), 2.5f, cap = StrokeCap.Round)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAnatomyIllustration() {
    drawLine(CobaltBlue, Offset(30f, 25f), Offset(70f, 75f), 12f, cap = StrokeCap.Round)
    drawCircle(CobaltBlue, 13f, Offset(27f, 23f))
    drawCircle(MintGreen, 12f, Offset(73f, 77f))
    drawCircle(CardWhite, 6f, Offset(27f, 23f))
    drawCircle(CardWhite, 5f, Offset(73f, 77f))
    drawLine(MintGreen.copy(alpha = 0.75f), Offset(39f, 37f), Offset(61f, 63f), 4f, cap = StrokeCap.Round)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPharmacologyIllustration() {
    val capsule = Path().apply {
        moveTo(31f, 70f)
        cubicTo(22f, 61f, 22f, 47f, 31f, 38f)
        lineTo(43f, 26f)
        cubicTo(52f, 17f, 66f, 17f, 75f, 26f)
        cubicTo(84f, 35f, 84f, 49f, 75f, 58f)
        lineTo(63f, 70f)
        cubicTo(54f, 79f, 40f, 79f, 31f, 70f)
        close()
    }
    drawPath(capsule, CardWhite)
    drawPath(capsule, CobaltBlue, style = Stroke(width = 3f))
    val greenHalf = Path().apply {
        moveTo(43f, 26f)
        cubicTo(52f, 17f, 66f, 17f, 75f, 26f)
        cubicTo(84f, 35f, 84f, 49f, 75f, 58f)
        lineTo(53f, 36f)
        close()
    }
    drawPath(greenHalf, MintGreen)
    drawLine(CobaltBlue, Offset(40f, 39f), Offset(62f, 61f), 3f, cap = StrokeCap.Round)
    drawCircle(WarningAmber, 5f, Offset(74f, 23f))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHistologyIllustration() {
    drawCircle(CardWhite, 23f, Offset(42f, 45f))
    drawCircle(MintGreen.copy(alpha = 0.82f), 16f, Offset(42f, 45f))
    drawCircle(CobaltBlue, 7f, Offset(42f, 45f))
    drawCircle(CardWhite, 19f, Offset(68f, 61f))
    drawCircle(WarningAmber.copy(alpha = 0.82f), 13f, Offset(68f, 61f))
    drawCircle(CobaltBlue, 5f, Offset(68f, 61f))
    drawCircle(CardWhite, 12f, Offset(28f, 70f))
    drawCircle(DifficultyMastered, 8f, Offset(28f, 70f))
    drawCircle(ErrorRed, 4f, Offset(76f, 29f))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBiologyIllustration() {
    drawCircle(CardWhite, 31f, Offset(50f, 50f))
    drawCircle(MintGreen.copy(alpha = 0.72f), 26f, Offset(50f, 50f))
    drawCircle(CobaltBlue, 10f, Offset(54f, 47f))
    drawCircle(WarningAmber, 4f, Offset(35f, 40f))
    drawCircle(WarningAmber, 4f, Offset(68f, 61f))
    drawRoundRect(CobaltBlue.copy(alpha = 0.7f), Offset(31f, 57f), Size(10f, 4f), androidx.compose.ui.geometry.CornerRadius(2f))
    drawRoundRect(CobaltBlue.copy(alpha = 0.7f), Offset(61f, 34f), Size(10f, 4f), androidx.compose.ui.geometry.CornerRadius(2f))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawChemistryIllustration() {
    val flask = Path().apply {
        moveTo(42f, 20f)
        lineTo(58f, 20f)
        lineTo(56f, 43f)
        lineTo(75f, 72f)
        cubicTo(79f, 79f, 73f, 83f, 67f, 83f)
        lineTo(33f, 83f)
        cubicTo(27f, 83f, 21f, 79f, 25f, 72f)
        lineTo(44f, 43f)
        close()
    }
    drawPath(flask, CardWhite)
    drawPath(flask, CobaltBlue, style = Stroke(width = 3f, join = StrokeJoin.Round))
    drawLine(CobaltBlue, Offset(40f, 20f), Offset(60f, 20f), 4f, cap = StrokeCap.Round)
    drawPath(
        Path().apply {
            moveTo(33f, 66f)
            lineTo(67f, 66f)
            lineTo(75f, 77f)
            lineTo(25f, 77f)
            close()
        },
        MintGreen
    )
    drawCircle(WarningAmber, 3f, Offset(45f, 60f))
    drawCircle(WarningAmber, 2.5f, Offset(54f, 55f))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPhysicsIllustration() {
    drawLine(TextMuted.copy(alpha = 0.45f), Offset(23f, 72f), Offset(78f, 72f), 2f)
    drawLine(TextMuted.copy(alpha = 0.45f), Offset(29f, 78f), Offset(29f, 24f), 2f)
    val wave = Path().apply {
        moveTo(30f, 51f)
        cubicTo(37f, 25f, 43f, 25f, 50f, 51f)
        cubicTo(57f, 77f, 63f, 77f, 70f, 51f)
        cubicTo(73f, 39f, 76f, 34f, 80f, 34f)
    }
    drawPath(wave, CobaltBlue, style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(MintGreen, 5f, Offset(50f, 51f))
    drawCircle(WarningAmber, 4f, Offset(70f, 51f))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPreMedicineIllustration() {
    val cap = Path().apply {
        moveTo(16f, 39f)
        lineTo(50f, 22f)
        lineTo(84f, 39f)
        lineTo(50f, 56f)
        close()
    }
    drawPath(cap, CobaltBlue)
    drawPath(cap, CardWhite, style = Stroke(width = 2.5f, join = StrokeJoin.Round))
    drawRoundRect(MintGreen, Offset(33f, 49f), Size(34f, 17f), androidx.compose.ui.geometry.CornerRadius(4f))
    drawLine(WarningAmber, Offset(82f, 39f), Offset(82f, 68f), 3f, cap = StrokeCap.Round)
    drawCircle(WarningAmber, 4f, Offset(82f, 72f))
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawEmptyIllustration() {
    drawRoundRect(CardWhite, Offset(20f, 30f), Size(52f, 48f), androidx.compose.ui.geometry.CornerRadius(7f))
    drawRoundRect(TextMuted.copy(alpha = 0.18f), Offset(27f, 38f), Size(37f, 4f), androidx.compose.ui.geometry.CornerRadius(2f))
    drawRoundRect(TextMuted.copy(alpha = 0.18f), Offset(27f, 47f), Size(29f, 4f), androidx.compose.ui.geometry.CornerRadius(2f))
    drawRoundRect(MintGreen, Offset(27f, 59f), Size(32f, 10f), androidx.compose.ui.geometry.CornerRadius(5f))
    drawCircle(WarningAmber, 13f, Offset(72f, 29f))
    drawLine(CardWhite, Offset(72f, 23f), Offset(72f, 35f), 3f, cap = StrokeCap.Round)
    drawLine(CardWhite, Offset(66f, 29f), Offset(78f, 29f), 3f, cap = StrokeCap.Round)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAnalyticsIllustration() {
    drawRoundRect(CardWhite, Offset(19f, 20f), Size(62f, 62f), androidx.compose.ui.geometry.CornerRadius(10f))
    drawLine(TextMuted.copy(alpha = 0.3f), Offset(29f, 69f), Offset(72f, 69f), 2f)
    drawRoundRect(MintGreen, Offset(31f, 53f), Size(8f, 16f), androidx.compose.ui.geometry.CornerRadius(3f))
    drawRoundRect(CobaltBlue, Offset(46f, 43f), Size(8f, 26f), androidx.compose.ui.geometry.CornerRadius(3f))
    drawRoundRect(WarningAmber, Offset(61f, 32f), Size(8f, 37f), androidx.compose.ui.geometry.CornerRadius(3f))
    val trend = Path().apply {
        moveTo(31f, 47f)
        lineTo(46f, 37f)
        lineTo(58f, 39f)
        lineTo(73f, 26f)
    }
    drawPath(
        trend,
        CobaltBlue,
        style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCalendarIllustration() {
    drawRoundRect(CardWhite, Offset(20f, 23f), Size(60f, 56f), androidx.compose.ui.geometry.CornerRadius(9f))
    drawRoundRect(CobaltBlue, Offset(20f, 23f), Size(60f, 16f), androidx.compose.ui.geometry.CornerRadius(8f))
    drawRect(CobaltBlue, Offset(20f, 31f), Size(60f, 8f))
    drawLine(CardWhite, Offset(35f, 19f), Offset(35f, 29f), 3f, cap = StrokeCap.Round)
    drawLine(CardWhite, Offset(65f, 19f), Offset(65f, 29f), 3f, cap = StrokeCap.Round)
    val dates = listOf(
        Offset(34f, 50f), Offset(50f, 50f), Offset(66f, 50f),
        Offset(34f, 66f), Offset(50f, 66f), Offset(66f, 66f)
    )
    dates.forEachIndexed { index, point ->
        drawCircle(if (index == 4) MintGreen else LightColdGrey, 4.5f, point)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPremiumIllustration() {
    val star = Path().apply {
        moveTo(50f, 16f)
        lineTo(59f, 38f)
        lineTo(83f, 40f)
        lineTo(65f, 55f)
        lineTo(70f, 79f)
        lineTo(50f, 66f)
        lineTo(30f, 79f)
        lineTo(35f, 55f)
        lineTo(17f, 40f)
        lineTo(41f, 38f)
        close()
    }
    drawPath(star, WarningAmber)
    drawCircle(CardWhite, 5f, Offset(50f, 48f))
    drawCircle(MintGreen, 4f, Offset(76f, 23f))
    drawCircle(CobaltBlue, 3f, Offset(24f, 72f))
}
