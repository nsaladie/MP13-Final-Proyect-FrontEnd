package com.example.hospitalfrontend.domain.model.medical

data class DiagnosisState(
    val id: Int,
    val oxygenLevel: Int,
    val dependencyLevel: String,
    val oxygenLevelDescription: String,
    val diapers: Boolean,
    val totalChangesDiapers: Int,
    val detailDescription: String,
    val urinaryCatheter: String,
    val rectalCatheter: String,
    val nasogastricTube: String
)
