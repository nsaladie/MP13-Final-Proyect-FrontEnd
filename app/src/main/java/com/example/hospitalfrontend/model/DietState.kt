package com.example.hospitalfrontend.model

import java.util.Date

data class DietState(
    val id: Int = 0,
    val date: Date? = null,
    val takeData: String,
    val dietType: Set<DietTypeState>? = null,
    val dietTypeTexture: DietTextureTypeState? = null,
    val independent: Int = 0,
    val prosthesis: Int = 0
)

