package com.example.hospitalfrontend.model

data class MobilizationState (
    val id:Int =0,
    val sedestation:Int=0,
    val walkingAssis:Int=0,
    val assisDesc:String="",
    val changes:String="",
    val decubitus:String=""
)
