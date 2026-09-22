package com.formup.app.data

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import java.util.concurrent.TimeUnit

data class Coach(
    val uid: String, val email: String, val displayName: String,
    val profilePhotoUrl: String?, val preferredLanguage: String,
    val teamName: String?, val ageGroup: Int?
)
data class UpdateCoachRequest(
    val displayName: String? = null,
    val profilePhotoUrl: String? = null,
    val preferredLanguage: String? = null
)
data class TeamResponse(val teamName: String?, val ageGroup: Int?, val hasTeam: Boolean)

interface FormUpApi {
    @GET("api/coach") suspend fun getCoach(): Coach
    @PATCH("api/coach") suspend fun updateCoach(@Body body: UpdateCoachRequest): Coach
    @GET("api/team") suspend fun getTeam(): TeamResponse
}

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runCatching {
            FirebaseAuth.getInstance().currentUser?.let { Tasks.await(it.getIdToken(false)).token }
        }.getOrNull()
        val request = chain.request().newBuilder().apply {
            if (token != null) header("Authorization", "Bearer $token")
        }.build()
        return chain.proceed(request)
    }
}

object ApiClient {
    private const val BASE_URL = "http://prog7314.runasp.net/"

    private val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
            redactHeader("Authorization")
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: FormUpApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FormUpApi::class.java)
}