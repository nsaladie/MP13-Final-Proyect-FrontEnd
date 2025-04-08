package com.example.hospitalfrontend.model

import java.util.Date

data class DietState(
    val id: Int,
    val date: Date,
    val takeData: String,
    val dietTypes: Set<DietTypeState>? = null,
    val dietTypeTexture: DietTextureTypeState? = null,
    val independent: Int,
    val prosthesis: Int
)