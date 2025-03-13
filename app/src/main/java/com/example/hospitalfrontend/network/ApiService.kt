package com.example.hospitalfrontend.network

import com.example.hospitalfrontend.model.LoginAuxiliary
import com.example.hospitalfrontend.model.LoginRequest
import com.example.hospitalfrontend.model.NurseState
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
    @POST("auxiliary/login")
    suspend fun loginAuxiliary(@Body loginAuxiliary: LoginAuxiliary): Boolean

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
        @Path("id") id: Int,
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>
}