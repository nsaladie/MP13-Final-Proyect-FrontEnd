package com.example.hospitalfrontend.data.api

import com.example.hospitalfrontend.domain.model.user.AuxiliaryState
import com.example.hospitalfrontend.domain.model.medical.DiagnosisRequest
import com.example.hospitalfrontend.domain.model.medical.DiagnosisState
import com.example.hospitalfrontend.domain.model.diet.DietTextureTypeState
import com.example.hospitalfrontend.domain.model.diet.DietTypeState
import com.example.hospitalfrontend.domain.model.auth.LoginAuxiliary
import com.example.hospitalfrontend.domain.model.auth.LoginRequest
import com.example.hospitalfrontend.domain.model.user.NurseState
import com.example.hospitalfrontend.domain.model.patient.PatientState
import com.example.hospitalfrontend.domain.model.auth.RegisterState
import com.example.hospitalfrontend.domain.model.facility.RoomWithObservation
import com.example.hospitalfrontend.domain.model.medical.VitalSignState
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

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
    suspend fun getAllRooms(): List<RoomWithObservation>

    //Auxiliary
    @POST("auxiliary/login")
    suspend fun loginAuxiliary(@Body loginAuxiliary: LoginAuxiliary): AuxiliaryState

    //Patient
    @GET("patient/{id}")
    suspend fun getPatientById(@Path("id") id: Int): PatientState

    //Patient Update
    @PUT("patient/{id}")
    suspend fun updatePatient(@Path("id") id: Int, @Body update: PatientState): PatientState

    //Cures
    @GET("register/vitalSign/{id}")
    suspend fun getAllCures(@Path("id") id: Int): List<VitalSignState>

    // Diagnosis Details
    @GET("register/diagnosis/{id}")
    suspend fun getDiagnosis(@Path("id") id: Int): DiagnosisState

    // Create new Cure
    @POST("register")
    suspend fun createCure(@Body registerState: RegisterState): Boolean

    // Cure Detail
    @GET("register/{id}")
    suspend fun getCureDetail(@Path("id") id: Int): RegisterState

    // Create Diagnosis
    @POST("register/diagnosis")
    suspend fun createDiagnosis(@Body request: DiagnosisRequest): Boolean

    // List of DietType
    @GET("diet/type")
    suspend fun getAllDietType(): List<DietTypeState>

    // List of DietTexture
    @GET("diet/texture")
    suspend fun getAllDietTexture(): List<DietTextureTypeState>
}