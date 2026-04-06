package br.edu.ifsp.hto.htoipdm.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

class CurrencyVisualTransformation(
    private val locale: Locale = Locale.getDefault(),
    private val scale: Int = 2
) : VisualTransformation {

    private val formatter = NumberFormat.getCurrencyInstance(locale)

    override fun filter(text: AnnotatedString): TransformedText {

        val raw = text.text.ifEmpty { "0" }

        val number = try {
            BigDecimal(raw).movePointLeft(scale)
        } catch (e: Exception) {
            BigDecimal.ZERO
        }

        val formatted = formatter.format(number)

        val digitIndexes = formatted.mapIndexedNotNull { i, c ->
            if (c.isDigit()) i else null
        }

        val offsetMapping = object : OffsetMapping {

            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                if (offset > digitIndexes.size) return formatted.length
                return digitIndexes[offset - 1] + 1
            }

            override fun transformedToOriginal(offset: Int): Int {
                val digitsBefore = digitIndexes.count { it < offset }
                return digitsBefore.coerceAtMost(raw.length)
            }
        }

        return TransformedText(
            AnnotatedString(formatted),
            offsetMapping
        )
    }
}