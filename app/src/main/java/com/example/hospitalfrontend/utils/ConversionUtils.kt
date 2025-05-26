package com.example.hospitalfrontend.utils

import android.content.Context
import androidx.annotation.StringRes
import com.example.hospitalfrontend.R

/**
 * Converts integers to descriptive text for values using string resources
 * @param context Android context to access string resources
 * @param trueTextRes String resource ID for true value
 * @param falseTextRes String resource ID for false value
 * @return Descriptive text based on the value
 */
fun Int.toDescriptiveText(
    context: Context,
    @StringRes trueTextRes: Int,
    @StringRes falseTextRes: Int
): String {
    return when (this) {
        0 -> context.getString(falseTextRes)
        1 -> context.getString(trueTextRes)
        else -> context.getString(R.string.unknown) // Asegúrate de tener este string en tus recursos
    }
}

/**
 * Converts booleans to descriptive text for values using string resources
 * @param context Android context to access string resources
 * @param trueTextRes String resource ID for true value
 * @param falseTextRes String resource ID for false value
 * @return Descriptive text based on the value
 */
fun Boolean.toDescriptiveText(
    context: Context,
    @StringRes trueTextRes: Int,
    @StringRes falseTextRes: Int
): String {
    return when (this) {
        false -> context.getString(falseTextRes)
        true -> context.getString(trueTextRes)
    }
}

/**
 * Specific conversion for independent field in Diet
 */
fun Int.toIndependentText(context: Context): String {
    return this.toDescriptiveText(
        context,
        R.string.needs_help,      // "Necessita ajuda"
        R.string.autonomous       // "Autònom"
    )
}

/**
 * Specific conversion for prosthesis field in Diet
 */
fun Int.toProsthesisText(context: Context): String {
    return this.toDescriptiveText(
        context,
        R.string.yes,            // "Sí"
        R.string.no              // "No"
    )
}

/**
 * Specific conversion for walkingAssis field in Mobilization
 */
fun Int.toWalkingAssisText(context: Context): String {
    return this.toDescriptiveText(
        context,
        R.string.with_assistance,    // "Amb assistència"
        R.string.without_assistance  // "Sense assistència"
    )
}

/**
 * Specific conversion for Diapers field in DiagnosisDetails
 */
fun Boolean.toDiapersText(context: Context): String {
    return this.toDescriptiveText(
        context,
        R.string.wears,          // "Porta"
        R.string.does_not_wear   // "No porta"
    )
}

/**
 * Specific conversion for OxygenLevel field in DiagnosisDetails
 */
fun Int.toOxygenLevelText(context: Context): String {
    return this.toDescriptiveText(
        context,
        R.string.requires,       // "Requereix"
        R.string.does_not_require // "No requereix"
    )
}