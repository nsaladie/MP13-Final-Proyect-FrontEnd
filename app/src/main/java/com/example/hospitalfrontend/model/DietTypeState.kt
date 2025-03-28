package com.example.hospitalfrontend.model

data class DietTypeState(
    val id: Int,
    val dietTypeDesc: String,
    val diet:Set<DietTypeState>
)
