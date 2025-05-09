package com.example.solutionchallenge.api

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

fun File.toMultipartBodyPart(partName: String): MultipartBody.Part {
    val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), this)
    return MultipartBody.Part.createFormData(partName, name, requestFile)
}
