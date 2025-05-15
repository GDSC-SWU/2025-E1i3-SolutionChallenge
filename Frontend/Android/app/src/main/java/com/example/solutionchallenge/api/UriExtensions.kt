package com.example.solutionchallenge.api

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

fun Uri.toMultipartBodyPart(fieldName: String, context: Context): MultipartBody.Part {
    val inputStream = context.contentResolver.openInputStream(this)
        ?: throw IllegalArgumentException("파일을 열 수 없습니다: $this")

    val file = File(context.cacheDir, "$fieldName.jpg")
    FileOutputStream(file).use { output ->
        inputStream.copyTo(output)
    }

    val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(fieldName, file.name, requestBody)
}
