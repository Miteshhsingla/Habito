package com.timeit.habito.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {
    //hostel ip
    //private const val BASE_URL = "http://10.22.13.75:8000/"

    //mobile hotspot ip
    //private const val BASE_URL = "http://192.168.47.46:8000/"

    //lakshay ip
//      private const val BASE_URL = "http://192.168.95.46:8000/"
    //goji ip
      private const val BASE_URL = "http://10.42.0.96:8000/"

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
