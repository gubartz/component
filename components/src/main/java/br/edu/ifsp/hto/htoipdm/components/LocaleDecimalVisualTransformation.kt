package br.edu.ifsp.hto.htoipdm.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow

class LocaleDecimalVisualTransformation(
    private val decimalDigits: Int = 2
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {

        val cleanText = text.text.filter { it.isDigit() }

        if (cleanText.isEmpty()) {
            return TransformedText(AnnotatedString(""), OffsetMapping.Identity)
        }

        val locale = Locale.getDefault()

        val number = cleanText.toDouble() / 10.0.pow(decimalDigits)

        val formatter = NumberFormat.getNumberInstance(locale) as DecimalFormat
        formatter.minimumFractionDigits = decimalDigits
        formatter.maximumFractionDigits = decimalDigits
        formatter.isGroupingUsed = true

        val formatted = formatter.format(number)

        val offsetMapping = object : OffsetMapping {

            override fun originalToTransformed(offset: Int): Int {

                val digitsBefore = cleanText.take(offset).length
                var count = 0

                formatted.forEachIndexed { index, c ->
                    if (c.isDigit()) count++
                    if (count >= digitsBefore) return index + 1
                }

                return formatted.length
            }

            override fun transformedToOriginal(offset: Int): Int {

                var digitsCount = 0

                formatted.take(offset).forEach {
                    if (it.isDigit()) digitsCount++
                }

                return digitsCount.coerceAtMost(cleanText.length)
            }
        }

        return TransformedText(
            AnnotatedString(formatted),
            offsetMapping
        )
    }
}