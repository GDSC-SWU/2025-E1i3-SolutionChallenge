package com.example.solutionchallenge.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("analyze-day")
    suspend fun analyzeDay(
        @Part morning: MultipartBody.Part,
        @Part lunch: MultipartBody.Part,
        @Part dinner: MultipartBody.Part,
        @Part snack: MultipartBody.Part
    ): Response<AnalyzeResponse>
}
