package com.example.hospitalfrontend.domain.model.user

data class NurseState(
    val id: Int,
    val age: String,
    val name: String,
    val email: String,
    val surname: String,
    val password: String,
    val speciality: String
)
