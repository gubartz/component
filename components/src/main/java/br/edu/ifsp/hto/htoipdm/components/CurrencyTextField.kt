package br.edu.ifsp.hto.htoipdm.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow

/**
 * A text field specialized for currency input using a numeric (Long) backing value.
 *
 * This composable allows the user to input only digits, while displaying the value
 * formatted as currency through a [VisualTransformation]. Internally, the value is
 * represented as a [Long], typically corresponding to the smallest currency unit
 * (e.g., cents).
 *
 * Key behaviors:
 * - Accepts digits only (`0-9`).
 * - Automatically filters out any non-digit characters.
 * - Maintains an internal text state synchronized with the external [value].
 * - Uses [CurrencyVisualTransformation] to format the displayed value (e.g., "1234" → "12,34").
 * - Calls [onValueChange] with the parsed numeric value as [Long].
 *
 * State handling:
 * - The displayed text is controlled internally to allow smooth typing and formatting.
 * - A [LaunchedEffect] keeps the internal state in sync when [value] changes externally.
 *
 * @param value The current numeric value, typically representing the smallest unit
 * (e.g., cents). For example, `1234` represents "12.34".
 * @param onValueChange Callback invoked whenever the numeric value changes.
 * Receives the updated value as [Long].
 * @param modifier Optional [Modifier] for layout and styling.
 * @param label Optional composable displayed as the label inside the [OutlinedTextField].
 *
 * @sample
 * ```
 * var amount by remember { mutableStateOf(0L) }
 *
 * CurrencyTextField(
 *     value = amount,
 *     onValueChange = { amount = it },
 *     label = { Text("Valor") }
 * )
 * ```
 *
 * @note This composable assumes a fixed decimal scale (usually 2 decimal places).
 * The formatting behavior depends on the implementation of [CurrencyVisualTransformation].
 *
 * @note This component separates concerns:
 * - Input filtering and state → handled internally
 * - Visual formatting → handled by [VisualTransformation]
 *
 * @see OutlinedTextField
 * @see VisualTransformation
 * @see CurrencyVisualTransformation
 */
@Composable
fun CurrencyTextField(
    value: Long,
    onValueChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null
) {

    OutlinedTextField(
        value = if (value == 0L) "" else value.toString(),
        onValueChange = { newValue ->
            val digitsOnly = newValue.filter { it.isDigit() }
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

/**
 * Converts a [Long] value representing a scaled integer amount into a [Double]
 * by applying a decimal shift based on the number of digits.
 *
 * This is commonly used for currency representations where values are stored
 * as integers (e.g., cents) to avoid floating-point precision issues.
 *
 * For example:
 * - `12345.toDoubleCurrency(2)` results in `123.45`
 * - `500.toDoubleCurrency(2)` results in `5.0`
 *
 * @param digits The number of decimal places to shift to the left.
 *               Typically 2 for standard currency (e.g., cents to reais/dollars).
 *
 * @return The resulting [Double] value after applying the decimal scaling.
 *
 * @throws IllegalArgumentException if [digits] is negative.
 */
fun Long.toDoubleCurrency(digits: Int): Double {
    require(digits >= 0) { "digits must be non-negative" }
    return this / 10.0.pow(digits)
}

/**
 * Formats a [Long] value representing a scaled integer amount into a localized
 * currency [String].
 *
 * This function first converts the value using [toDoubleCurrency], applying a
 * decimal shift based on [digits], and then formats it using the default
 * device locale via [NumberFormat.getCurrencyInstance].
 *
 * Typical use case is formatting values stored as integers (e.g., cents) into
 * human-readable currency strings.
 *
 * Examples:
 * - `12345.formatCurrency(2)` → "R$ 123,45" (in pt-BR locale)
 * - `12345.formatCurrency(2)` → "$123.45" (in en-US locale)
 *
 * @param digits The number of decimal places to shift to the left before formatting.
 *               Typically 2 for standard currencies.
 *
 * @return A localized currency string representation of the value.
 *
 * @throws IllegalArgumentException if [digits] is negative (propagated from [toDoubleCurrency]).
 *
 * @see toDoubleCurrency
 * @see NumberFormat.getCurrencyInstance
 */
fun Long.formatCurrency(digits: Int): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    return formatter.format(this.toDoubleCurrency(digits))
}