package com.example.solutionchallenge.api

import com.example.solutionchallenge.data.UserInfoResponse
import retrofit2.http.Body
import retrofit2.http.POST

data class TokenRequest(val token: String)

interface AuthApiService {
    @POST("/api/auth/login")
    suspend fun login(@Body request: TokenRequest): UserInfoResponse
}
