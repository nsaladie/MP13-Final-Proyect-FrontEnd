package com.example.hospitalfrontend.domain.model.user

data class SearchState(
    val nurseName : String = "",
    val searchResults : List<NurseState> = emptyList(),
    val resultMessage : String = "",
)