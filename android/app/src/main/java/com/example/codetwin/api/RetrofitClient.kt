package com.example.codetwin.api

import android.content.Context
import com.example.codetwin.utils.SessionManager
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // 10.0.2.2 is the special IP to access your computer's localhost from the Emulator
    private const val BASE_URL = "http://10.0.2.2:8080/"

    fun getClient(context: Context): ApiService {
        val sessionManager = SessionManager(context)

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                
                // Automatically add the Bearer token to every request if the user is logged in
                sessionManager.getAccessToken()?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }
                
                chain.proceed(requestBuilder.build())
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
