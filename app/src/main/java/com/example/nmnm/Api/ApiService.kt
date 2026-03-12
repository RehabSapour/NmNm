package com.example.nmnm.Api


import com.example.nmnm.Models.LoginRequest
import com.example.nmnm.Models.RegisterResponse
import com.example.nmnm.Models.ResetPasswordRequest
import com.example.nmnm.Models.UserResponse
import com.example.nmnm.Models.VerifyCodeRequest
import com.example.nmnm.VM.MessageDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthApiService {
    @Multipart
    @POST("api/Authentication/register")
    suspend fun registerUser(
        @Part("FullName") fullName: RequestBody,
        @Part("Email") email: RequestBody,
        @Part("Password") password: RequestBody,
        @Part profileImage: MultipartBody.Part?
    ): Response<RegisterResponse>

    @POST("api/Authentication/login")
    suspend fun loginUser(
        @Body request: LoginRequest
    ): Response<RegisterResponse>

//    @POST("/api/Authentication/forgetPassword")
//    suspend fun forgetPassword(
//        @Body email: String // إرسال الإيميل كـ String مباشر
//    ): Response<Unit> // أو حسب نوع الـ Response المتوقع من الـ API

    @POST("/api/Authentication/forgetPassword")
    suspend fun forgetPassword(
        @Body email: String
    ): Response<Unit>

    @POST("/api/Authentication/verifyResetCode")
    suspend fun verifyResetCode(
        @Body request: VerifyCodeRequest
    ): Response<okhttp3.ResponseBody>

    @POST("/api/Authentication/resetPassword")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<RegisterResponse>

    @GET("api/User/AllUsers")
    suspend fun getAllUsers(): List<UserResponse>

    @GET("api/Authentication/currentUser")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): Response<UserResponse>

    @Multipart
    @PUT("api/User/UpdateUser")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Part("Name") name: RequestBody,
        @Part profileImage: MultipartBody.Part?
    ): Response<UserResponse>

    @GET("api/Chat/conversation/{receiverId}")
    suspend fun getConversation(
        @Header("Authorization") token: String,
        @Path("receiverId") receiverId: String
    ): Response<List<MessageDto>>

}