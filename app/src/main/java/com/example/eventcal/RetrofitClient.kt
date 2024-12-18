package com.example.eventcal
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.125.218.220:8000/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object ApiClient {
    val apiService: APIInterface by lazy {
        RetrofitClient.retrofit.create(APIInterface::class.java)
    }
}