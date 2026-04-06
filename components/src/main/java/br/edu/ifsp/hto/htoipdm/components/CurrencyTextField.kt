package br.edu.ifsp.hto.htoipdm.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CurrencyTextField(
    value: BigDecimal,
    onValueChange: (BigDecimal) -> Unit,
    modifier: Modifier = Modifier,
    locale: Locale = Locale.getDefault(),
    scale: Int = 2,
    label: @Composable (() -> Unit)? = null
) {
    var internalDigits by remember {
        mutableStateOf(value.toDigits(scale))
    }

    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(internalDigits))
    }

    LaunchedEffect(value) {
        val newDigits = value.toDigits(scale)
        if (newDigits != internalDigits) {
            internalDigits = newDigits
            textFieldValue = TextFieldValue(
                text = newDigits,
                selection = TextRange(newDigits.length)
            )
        }
    }

    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { newValue ->

            val digitsOnly = newValue.text.filter { it.isDigit() }
            val safeDigits = digitsOnly.ifEmpty { "0" }

            val normalized = safeDigits.trimStart('0').ifEmpty { "0" }

            val newCursor = calculateNewCursorPosition(
                old = textFieldValue,
                new = newValue,
                filtered = normalized
            )

            internalDigits = normalized

            textFieldValue = TextFieldValue(
                text = normalized,
                selection = TextRange(newCursor.coerceAtLeast(1))
            )

            onValueChange(normalized.toBigDecimalCurrency(scale))
        },
        visualTransformation = CurrencyVisualTransformation(locale, scale),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        label = label,
        modifier = modifier
    )
}

fun calculateNewCursorPosition(
    old: TextFieldValue,
    new: TextFieldValue,
    filtered: String
): Int {
    val oldDigitsBeforeCursor = old.text
        .take(old.selection.start)
        .count { it.isDigit() }

    val newDigitsBeforeCursor = new.text
        .take(new.selection.start)
        .count { it.isDigit() }

    val diff = newDigitsBeforeCursor - oldDigitsBeforeCursor

    return (old.selection.start + diff)
        .coerceIn(0, filtered.length)
}

fun String.toBigDecimalCurrency(scale: Int = 2): BigDecimal {
    val digits = this.ifEmpty { "0" }
    return BigDecimal(digits).movePointLeft(scale)
}

fun BigDecimal.toDigits(scale: Int = 2): String {
    return this
        .movePointRight(scale)
        .setScale(0, RoundingMode.DOWN)
        .toPlainString()
}

fun BigDecimal.formatCurrency(
    locale: Locale = Locale.getDefault()
): String {
    val formatter = NumberFormat.getCurrencyInstance(locale)
    return formatter.format(this)
}