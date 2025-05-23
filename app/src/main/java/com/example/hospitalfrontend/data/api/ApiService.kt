package com.example.hospitalfrontend.data.api

import com.example.hospitalfrontend.domain.model.diet.*
import com.example.hospitalfrontend.domain.model.auth.*
import com.example.hospitalfrontend.domain.model.user.*
import com.example.hospitalfrontend.domain.model.medical.*
import com.example.hospitalfrontend.domain.model.patient.PatientState
import com.example.hospitalfrontend.domain.model.medication.MedicationState
import com.example.hospitalfrontend.domain.model.facility.*
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
    suspend fun getAllRooms(): List<RoomDTO>

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

    // List of Diagnosis
    @GET("register/diagnosisList/{id}")
    suspend fun getAllDiagnosis(@Path("id") id: Int): List<DiagnosisState>

    // List of Medication
    @GET("/medication")
    suspend fun getAllMedication(): List<MedicationState>

    // Medication
    @GET("/medication/{id}")
    suspend fun getMedication(@Path("id") id: Int): MedicationState

    // Medication
    @PUT("/medication/{id}")
    suspend fun updateMedication(@Path("id") id: Int, @Body request: MedicationState): Boolean

    // Create Medication
    @POST("/medication")
    suspend fun addMedicine(@Body medicationState: MedicationState): Boolean

    // Discharge a patient
    @PUT("room/discharge")
    suspend fun updatePatientDischarge(@Body patientState: PatientState): Boolean

    // Add new Diet Type
    @POST("/diet/type")
    suspend fun createNewDietType(@Body type: List<DietTypeState>): Boolean

    // Add new Diet Texture
    @POST("/diet/texture")
    suspend fun createNewDietTexture(@Body texture: List<DietTextureTypeState>): Boolean
}