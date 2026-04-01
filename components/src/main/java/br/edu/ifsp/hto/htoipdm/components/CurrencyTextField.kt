package br.edu.ifsp.hto.htoipdm.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow

@Composable
fun CurrencyTextField(
    value: Long,
    onValueChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null
) {

    var text by remember { mutableStateOf(value.toString()) }

    LaunchedEffect(value) {
        text = value.toString()
    }

    OutlinedTextField(
        value = text,
        onValueChange = { newValue ->
            val digitsOnly = newValue.filter { it.isDigit() }
            text = digitsOnly
            val longValue = digitsOnly.toLongOrNull() ?: 0L
            onValueChange(longValue)
        },
        label = label,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = CurrencyVisualTransformation(),
        singleLine = true,
        modifier = modifier
    )
}

fun Long.toDoubleCurrency(digits: Int): Double = this / 10.0.pow(digits)

fun Long.formatCurrency(digits: Int): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    return formatter.format(this.toDoubleCurrency(digits))
}