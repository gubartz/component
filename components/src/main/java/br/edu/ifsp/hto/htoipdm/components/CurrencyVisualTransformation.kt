package br.edu.ifsp.hto.htoipdm.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.NumberFormat
import java.util.Locale

class CurrencyVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Se o campo estiver vazio, não formata nada para exibir a label/placeholder
//        if (text.text.isEmpty()) {
//            return TransformedText(text, OffsetMapping.Identity)
//        }

        // Garante que só temos números
        val digitsOnly = text.text.filter { it.isDigit() }
        val value = digitsOnly.toLongOrNull() ?: 0L

        // Divide por 100 para considerar os dois últimos dígitos como centavos
        val doubleValue = value / 100.0

        // Pega a formatação de moeda com base no idioma/região do dispositivo
        val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
        val formattedText = formatter.format(doubleValue)

        // Mapeamento de cursor simples (mantém o cursor sempre no final)
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return formattedText.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                return text.length
            }
        }

        return TransformedText(
            text = AnnotatedString(formattedText),
            offsetMapping = offsetMapping
        )
    }
}