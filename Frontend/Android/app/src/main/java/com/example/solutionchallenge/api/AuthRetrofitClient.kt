package com.example.solutionchallenge.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

object AuthRetrofitClient {
    private const val BASE_URL = "http://34.22.103.49:8000"  // 로그인 서버 주소

    private val okHttpClient = OkHttpClient.Builder().build()

    val apiService: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
}
