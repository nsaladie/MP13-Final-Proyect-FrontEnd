package com.example.hospitalfrontend.utils

import androidx.compose.ui.graphics.Color

fun getBloodPressureColor(systolic: Double, diastolic: Double): Color {
    return if ((systolic > SYSTOLIC_HIGH || systolic < SYSTOLIC_LOW) ||
        (diastolic >= DIASTOLIC_HIGH || diastolic < DIASTOLIC_LOW)
    ) Color.Red else Color.Black
}


fun getRespiratoryRateColor(rate: Double): Color {
    return if (rate > RESPIRATORY_RATE_HIGH || rate < RESPIRATORY_RATE_LOW) Color.Red else Color.Black
}

fun getPulseColor(pulse: Double): Color {
    return if (pulse > PULSE_HIGH || pulse < PULSE_LOW) Color.Red else Color.Black
}

fun getTemperatureColor(temperature: Double): Color {
    return if (temperature > TEMPERATURE_HIGH || temperature < TEMPERATURE_LOW) Color.Red else Color.Black
}

fun getOxygenSaturationColor(saturation: Double): Color {
    return if (saturation < OXYGEN_SATURATION_LOW) Color.Red else Color.Black
}