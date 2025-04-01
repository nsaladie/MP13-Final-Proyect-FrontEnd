package com.example.hospitalfrontend.model

data class MobilizationState (
    val id:Int,
    val sedestation:Int,
    val walkingAssis:Int,
    val assisDesc:String,
    val changes:String,
    val decubitus:String
)
