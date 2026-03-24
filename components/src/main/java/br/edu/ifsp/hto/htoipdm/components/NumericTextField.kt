package br.edu.ifsp.hto.htoipdm.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import java.text.DecimalFormatSymbols

@Composable
fun NumericTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
) {
    val decimalSeparator = remember {
        DecimalFormatSymbols.getInstance().decimalSeparator
    }

    TextField(
        value = value,
        onValueChange = { newValue ->
            val filtered = newValue.filter { it.isDigit() || it == decimalSeparator }

            val result = buildString {
                var hasSeparator = false

                for (c in filtered) {
                    if (c.isDigit()) {
                        append(c)
                    } else if (c == decimalSeparator && !hasSeparator) {
                        append(c)
                        hasSeparator = true
                    }
                }
            }

            val normalized = result.replace(decimalSeparator, '.')
            3
            if (
                result.isEmpty() ||
                result.last() == decimalSeparator ||
                normalized.toDoubleOrNull() != null
            ) {
                onValueChange(result)
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal
        ),
        modifier = modifier
    )
}