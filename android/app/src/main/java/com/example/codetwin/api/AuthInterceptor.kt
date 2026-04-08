package com.example.codetwin.api

import android.content.Context
import com.example.codetwin.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val sessionManager = SessionManager(context)

        var request = chain.request()

        // Attach access token
        sessionManager.getAccessToken()?.let {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $it")
                .build()
        }

        var response = chain.proceed(request)

        // 🔥 If token expired → 401
        if (response.code() == 401) {

            response.close() // important

            val refreshToken = sessionManager.getRefreshToken()

            if (refreshToken != null) {

                val newAccessToken = refreshAccessToken(refreshToken)

                if (newAccessToken != null) {

                    // Save new token
                    sessionManager.saveTokens(newAccessToken, refreshToken, sessionManager.getUserId())

                    // Retry original request
                    val newRequest = request.newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", "Bearer $newAccessToken")
                        .build()

                    return chain.proceed(newRequest)
                }
            }
        }

        return response
    }

    // 🔧 Call refresh API manually
    private fun refreshAccessToken(refreshToken: String): String? {

        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(ApiService::class.java)

            val response = api.refreshToken(mapOf("refreshToken" to refreshToken)).execute()

            if (response.isSuccessful && response.body() != null) {
                val sessionManager = SessionManager(context)
                val loginResponse = response.body()!!.data
                sessionManager.saveTokens(loginResponse.accessToken, loginResponse.refreshToken, loginResponse.userId)
                return loginResponse.accessToken
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}