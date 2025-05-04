package com.example.hospitalfrontend.domain.model.medical

data class MobilizationState(
    val id: Int = 0,
    val sedestation: Int? = null,
    val walkingAssis: Int? = null,
    val assisDesc: String? = null,
    val changes: String = "",
    val decubitus: String = ""
)
