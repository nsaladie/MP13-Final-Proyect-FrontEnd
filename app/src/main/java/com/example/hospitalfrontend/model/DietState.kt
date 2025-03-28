package com.example.hospitalfrontend.model

import java.util.Date

data class DietState(
    val id:Int,
    val dietDate: Date,
    val dietTakeData: Date,
    val dietType:Set<DietTypeState>,
    val dietTextureType: DietTextureTypeState,
    val dietIndependent: Int,
    val dietProsthesis:Int
    )

