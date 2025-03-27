package com.josh25.vorsprungone.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


interface MockApi {
    @GET("rovermission/init")
    suspend fun getRoverMissionData(): RoverMissionResponse
    @GET("newrovermission/init")
    suspend fun getNewRoverMissionData(): RoverMissionResponse
}

fun createMockApi(interceptor: MockNetworkInterceptor): MockApi {
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(MockApi::class.java)
}