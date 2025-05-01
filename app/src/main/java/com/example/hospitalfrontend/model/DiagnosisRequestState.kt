package com.example.hospitalfrontend.model

data class DiagnosisRequest(
    val register: RegisterState,
    val diagnosis: DiagnosisRequestData
)

data class DiagnosisRequestData(
    val detailDiagnosisSet: List<DetailDiagnosisRequestData>
)

data class DetailDiagnosisRequestData(
    val dependencyLevel: String,
    val oxygenLevel: Int,
    val oxygenLevelDescription: String,
    val diapers: Boolean,
    val totalChangesDiapers: Int,
    val detailDescription: String,
    val urinaryCatheter: String,
    val rectalCatheter: String,
    val nasogastricTube: String
)