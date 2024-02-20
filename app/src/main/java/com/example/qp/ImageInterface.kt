package com.example.qp

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.Part
import retrofit2.http.Multipart
import retrofit2.http.POST

interface ImageInterface {
    @Multipart
    @POST("/images")
    fun uploadImage(
        @Part image: MultipartBody.Part
    ): Call<ImageResponse>

    @HTTP(method = "DELETE", path = "/images", hasBody = true)
    fun deleteImage(
        @Body url: Url
    ) : Call<DeleteResponse>
}