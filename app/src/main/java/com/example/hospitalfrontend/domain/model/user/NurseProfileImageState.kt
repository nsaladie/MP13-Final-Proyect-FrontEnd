package com.example.hospitalfrontend.domain.model.user

import android.graphics.Bitmap

data class NurseProfileImageState(
    val nurseId: Int,
    val image: Bitmap?
)
