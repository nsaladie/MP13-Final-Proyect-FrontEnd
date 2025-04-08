package com.example.hospitalfrontend.utils

/**
 * Converts integers to descriptive text for values
 * @param value Integer value (0 or 1)
 * @param trueText Text to display when value is 1
 * @param falseText Text to display when value is 0
 * @return Descriptive text based on the value
 */
fun Int.toDescriptiveText(trueText: String = "Sí", falseText: String = "No"): String {
    return when (this) {
        0 -> falseText
        1 -> trueText
        else -> "unknown"
    }
}

/**
 * Specific conversion for independent field in Diet
 */
fun Int.toIndependentText(): String {
    return this.toDescriptiveText("Necessita ajuda", "Autònom")
}

/**
 * Specific conversion for prosthesis field in Diet
 */
fun Int.toProsthesisText(): String {
    return this.toDescriptiveText("Portador", "No")
}

/**
 * Specific conversion for walkingAssis field in Mobilization
 */
fun Int.toWalkingAssisText(): String {
    return this.toDescriptiveText("Amb assistència", "Sense assistència")
}