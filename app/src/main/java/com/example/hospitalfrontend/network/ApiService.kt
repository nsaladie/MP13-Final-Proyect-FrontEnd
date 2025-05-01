package com.example.hospitalfrontend.network

import com.example.hospitalfrontend.model.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("nurse/{id}")
    suspend fun getNurseById(@Path("id") id: Int): NurseState

    @GET("nurse")
    suspend fun getAll(): List<NurseState>

    @POST("nurse/login")
    suspend fun loginNurse(@Body loginRequest: LoginRequest): NurseState

    @POST("nurse")
    suspend fun createNurse(@Body nurse: NurseState): NurseState

    @DELETE("nurse/{id}")
    suspend fun deleteNurse(@Path("id") id: Int): Boolean

    @GET("nurse/name/{name}")
    suspend fun findByName(@Path("name") nurseName: String): NurseState

    @PUT("nurse/{id}")
    suspend fun updateNurse(@Path("id") id: Int, @Body updateNurse: NurseState): NurseState

    @GET("nurse/photo/{id}")
    suspend fun getPhotoById(@Path("id") id: Int): Response<ResponseBody>

    @Multipart
    @POST("nurse/photo/{id}")
    suspend fun uploadPhotoById(
        @Path("id") id: Int, @Part file: MultipartBody.Part
    ): Response<ResponseBody>

    //Rooms
    @GET("room")
    suspend fun getAllRooms(): List<RoomState>

    //Auxiliary
    @POST("auxiliary/login")
    suspend fun loginAuxiliary(@Body loginAuxiliary: LoginAuxiliary): AuxiliaryState

    //Patient
    @GET("patient/{id}")
    suspend fun getPatientById(@Path("id") id: Int): PatientState

    //Cures
    @GET("register/vitalSign/{id}")
    suspend fun getAllCures(@Path("id") id: Int): List<VitalSignState>

    // Diagnosis Details
    @GET("register/diagnosis/{id}")
    suspend fun getDiagnosis(@Path("id") id: Int): DiagnosisState


    @POST("register")
    suspend fun createCure(@Body registerState: RegisterState): Boolean

    // Cure Detail
    @GET("register/{id}")
    suspend fun getCureDetail(@Path("id") id: Int): RegisterState

    // Create Diagnosis
    @POST("register/diagnosis")
    suspend fun createDiagnosis(@Body request: DiagnosisRequest): Boolean


}