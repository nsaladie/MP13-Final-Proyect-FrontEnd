package com.example.hospitalfrontend.model

import java.util.Date

data class DietState(
    val id: Int = 0,
    val date: Date? = null,
    val takeData: String? = null,
    val dietTypes: Set<DietTypeState> = emptySet(),
    val dietTypeTexture: DietTextureTypeState? = null,
    val independent: Int? = null,
    val prosthesis: Int? = null
)