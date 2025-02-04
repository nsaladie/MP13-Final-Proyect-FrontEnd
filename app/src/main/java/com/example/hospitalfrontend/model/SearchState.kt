package com.example.hospitalfrontend.model

data class SearchState(
    val nurseName : String = "",
    val searchResults : List<NurseState> = emptyList(),
    val resultMessage : String = "",
)
