package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface USAspendingApi {

    @POST("search/spending_by_transaction/")
    suspend fun searchSpendingByTransaction(
        @Body request: USAspendingRequest
    ): USAspendingResponse

    companion object {
        private const val BASE_URL = "https://api.usaspending.gov/api/v2/"

        fun create(): USAspendingApi {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
            }

            val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            return retrofit.create(USAspendingApi::class.java)
        }
    }
}

data class USAspendingRequest(
    @Json(name = "filters") val filters: Map<String, Any> = emptyMap(),
    @Json(name = "fields") val fields: List<String>,
    @Json(name = "limit") val limit: Int = 15,
    @Json(name = "page") val page: Int = 1,
    @Json(name = "sort") val sort: String = "Award Amount",
    @Json(name = "order") val order: String = "desc"
)

data class USAspendingResponse(
    @Json(name = "results") val results: List<USAspendingTransaction>?
)

data class USAspendingTransaction(
    @Json(name = "Award ID") val awardId: String?,
    @Json(name = "Recipient Name") val recipientName: String?,
    @Json(name = "Start Date") val startDate: String?,
    @Json(name = "End Date") val endDate: String?,
    @Json(name = "Award Amount") val awardAmount: Double?,
    @Json(name = "Awarding Agency") val awardingAgency: String?,
    @Json(name = "Awarding Sub Agency") val awardingSubAgency: String?,
    @Json(name = "Award Type") val awardType: String?,
    @Json(name = "Description") val description: String?
)
