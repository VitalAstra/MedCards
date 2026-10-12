package com.example.medcards.data.seed

import com.example.medcards.data.model.Flashcard
import com.example.medcards.data.model.QuizQuestion

internal object StudyContent {
    val additionalFlashcards = listOf(
        Flashcard("biology-1", "Biología", "¿Qué orgánulo produce la mayor parte del ATP en la respiración aeróbica?", "La mitocondria"),
        Flashcard("biology-2", "Biología", "¿Qué bases se aparean en el ADN?", "Adenina con timina y citosina con guanina"),
        Flashcard("biology-3", "Biología", "¿Cuál es la función principal de los ribosomas?", "Sintetizar proteínas"),
        Flashcard("biology-4", "Biología", "¿Qué proceso celular produce dos células hijas genéticamente similares?", "La mitosis"),
        Flashcard("biology-5", "Biología", "¿Cómo se llama el movimiento de agua a través de una membrana semipermeable?", "Ósmosis"),
        Flashcard("biology-6", "Biología", "¿Qué molécula almacena la información hereditaria?", "El ADN"),
        Flashcard("biology-7", "Biología", "¿Qué organismos producen materia orgánica a partir de energía luminosa?", "Los organismos fotosintéticos, como plantas y algas"),
        Flashcard("biology-8", "Biología", "¿Qué estructura regula el intercambio entre la célula y su entorno?", "La membrana plasmática"),
        Flashcard("chemistry-1", "Química", "¿Qué indica un pH menor que 7 en una disolución acuosa?", "Que es ácida"),
        Flashcard("chemistry-2", "Química", "¿Qué representa un mol de una sustancia?", "6.022 × 10²³ entidades elementales"),
        Flashcard("chemistry-3", "Química", "¿Qué tipo de enlace se forma al transferirse electrones?", "Un enlace iónico"),
        Flashcard("chemistry-4", "Química", "¿Qué partícula subatómica tiene carga negativa?", "El electrón"),
        Flashcard("chemistry-5", "Química", "¿Qué indica el número atómico de un elemento?", "El número de protones de su núcleo"),
        Flashcard("chemistry-6", "Química", "¿Qué ocurre con los electrones durante la oxidación?", "La especie pierde electrones"),
        Flashcard("physics-1", "Física", "¿Cuál es la unidad SI de fuerza?", "El newton (N)"),
        Flashcard("physics-2", "Física", "¿Qué relación expresa la ley de Ohm?", "V = I × R"),
        Flashcard("physics-3", "Física", "¿Cuál es la unidad SI de aceleración?", "Metro por segundo cuadrado (m/s²)"),
        Flashcard("physics-4", "Física", "¿Cómo se calcula la presión?", "Fuerza dividida entre área"),
        Flashcard("physics-5", "Física", "¿Qué magnitud permanece constante en una onda al cambiar de medio?", "La frecuencia"),
        Flashcard("premed-1", "Pre-Medicina", "¿Qué significa homeostasis?", "La regulación que mantiene relativamente estables las condiciones internas"),
        Flashcard("premed-2", "Pre-Medicina", "¿Cuál es la diferencia entre correlación y causalidad?", "La correlación describe asociación; no demuestra que una variable cause la otra"),
        Flashcard("premed-3", "Pre-Medicina", "¿Qué grupo sanguíneo se considera donante universal de glóbulos rojos en una emergencia?", "O negativo, considerando que siempre se debe confirmar compatibilidad cuando sea posible"),
        Flashcard("premed-4", "Pre-Medicina", "¿Qué sistema corporal coordina respuestas rápidas mediante señales eléctricas?", "El sistema nervioso"),
        Flashcard("premed-5", "Pre-Medicina", "¿Qué representa una hipótesis científica?", "Una explicación provisional, clara y comprobable mediante observaciones o experimentos")
    )

    val quizQuestions = listOf(
        QuizQuestion("bio-quiz-1", "Biología", "Biología celular", "¿En qué orgánulo ocurre la mayor parte de la producción aeróbica de ATP?", listOf("Aparato de Golgi", "Mitocondria", "Lisosoma", "Nucléolo"), 1, "La cadena respiratoria y la fosforilación oxidativa ocurren principalmente en la membrana interna mitocondrial."),
        QuizQuestion("bio-quiz-2", "Biología", "Genética", "¿Qué base nitrogenada se aparea con la adenina en el ADN?", listOf("Citosina", "Guanina", "Timina", "Uracilo"), 2, "En el ADN, la adenina se aparea con timina; en el ARN, se aparea con uracilo."),
        QuizQuestion("bio-quiz-3", "Biología", "Biología celular", "¿Cuál es el resultado típico de la mitosis de una célula somática?", listOf("Cuatro células haploides", "Dos células hijas similares", "Una célula sin núcleo", "Dos gametos"), 1, "La mitosis conserva el número de cromosomas y suele producir dos células hijas genéticamente similares."),
        QuizQuestion("chem-quiz-1", "Química", "Ácidos y bases", "Una disolución acuosa tiene pH 3. ¿Cómo se clasifica?", listOf("Ácida", "Neutra", "Básica", "No se puede clasificar"), 0, "A 25 °C, los valores de pH menores que 7 se consideran ácidos."),
        QuizQuestion("chem-quiz-2", "Química", "Enlaces", "¿Qué sucede principalmente al formarse un enlace iónico?", listOf("Se comparten electrones por igual", "Se transfieren electrones y se atraen iones", "Se comparten protones", "Se fusionan los núcleos"), 1, "La transferencia de electrones genera iones de cargas opuestas que se atraen electrostáticamente."),
        QuizQuestion("chem-quiz-3", "Química", "Estequiometría", "¿Cuántas entidades contiene aproximadamente un mol?", listOf("6.022 × 10²³", "3.00 × 10⁸", "9.81 × 10²", "1.60 × 10⁻¹⁹"), 0, "El número de Avogadro es aproximadamente 6.022 × 10²³ entidades por mol."),
        QuizQuestion("phys-quiz-1", "Física", "Mecánica", "Según la segunda ley de Newton, la fuerza neta es igual a:", listOf("Masa dividida entre aceleración", "Masa por aceleración", "Velocidad por tiempo", "Trabajo por distancia"), 1, "La segunda ley se expresa como F = m × a."),
        QuizQuestion("phys-quiz-2", "Física", "Electricidad", "Un resistor de 4 Ω conduce una corriente de 2 A. ¿Qué voltaje tiene?", listOf("2 V", "6 V", "8 V", "12 V"), 2, "Por la ley de Ohm, V = I × R = 2 A × 4 Ω = 8 V."),
        QuizQuestion("phys-quiz-3", "Física", "Magnitudes", "¿Cuál es la unidad SI de presión?", listOf("Joule", "Pascal", "Watt", "Coulomb"), 1, "La presión se mide en pascales (Pa), equivalentes a newtons por metro cuadrado."),
        QuizQuestion("premed-quiz-1", "Pre-Medicina", "Método científico", "Un estudio encuentra asociación entre dos variables. ¿Qué conclusión es válida por sí sola?", listOf("Una variable causa la otra", "Existe una asociación observada", "Se ha probado el mecanismo", "El resultado siempre es universal"), 1, "La correlación no demuestra causalidad; se necesita diseño y evidencia adicionales."),
        QuizQuestion("anatomy-quiz-1", "Anatomía", "Sistema cardiovascular", "¿Qué vaso lleva sangre desde el ventrículo izquierdo hacia la circulación sistémica?", listOf("Vena cava", "Arteria pulmonar", "Aorta", "Vena pulmonar"), 2, "La aorta sale del ventrículo izquierdo y distribuye sangre oxigenada a la circulación sistémica."),
        QuizQuestion("pharm-quiz-1", "Farmacología", "Seguridad farmacológica", "¿Cuál es la acción principal de la naloxona?", listOf("Antagonizar receptores opioides", "Estimular receptores beta-2", "Inhibir la síntesis de pared bacteriana", "Bloquear receptores H1"), 0, "La naloxona es un antagonista opioide utilizado para revertir temporalmente sus efectos; requiere atención médica y seguimiento.")
    )
}