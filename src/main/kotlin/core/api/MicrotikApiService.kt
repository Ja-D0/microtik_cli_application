package com.microtik.core.api

import com.google.gson.Gson
import com.microtik.Microtik
import com.microtik.core.api.endpoints.AddressApi
import com.microtik.core.api.endpoints.AddressListsApi
import com.microtik.core.api.endpoints.FirewallFilterApi
import com.microtik.core.api.exceptions.FailedRequestException
import com.microtik.core.api.responseModels.ErrorResponse
import com.microtik.core.base.HttpFileLogger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.util.*
import java.util.concurrent.TimeUnit

class MicrotikApiService private constructor() {
    companion object {
        private var instance: MicrotikApiService? = null
        private lateinit var retrofit: Retrofit

        fun getInstance(): MicrotikApiService {
            if (instance == null) {
                instance = MicrotikApiService()
            }

            return instance!!
        }

        /**
         *
         */
        fun <T> runRequest(callable: () -> Response<T>): T {
            val response: Response<T>

            try {
                response = callable()
            } catch (connectException: ConnectException) {
                throw FailedRequestException(500, "", connectException.message!!)
            }

            if (response.isSuccessful) {
                return response.body()!!

            } else {
                if (response.errorBody() != null) {
                    val errorBody = Gson().fromJson(response.errorBody()!!.string(), ErrorResponse::class.java)

                    throw FailedRequestException(errorBody.error, errorBody.detail, errorBody.detail)
                }

                throw FailedRequestException(response.code(), response.body().toString(), response.message())
            }
        }
    }

    init {
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .connectTimeout(120, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder().header("Authorization", getBasicCredentials()).build())
            }

        if (Microtik.app.getConfig().logsConfig.httpLogsConfig.path.isNotBlank()) {
            builder.addInterceptor(HttpLoggingInterceptor(HttpFileLogger()).apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }

        val client = builder.build()

        retrofit = Retrofit.Builder()
            .baseUrl("http://" + Microtik.app.getConfig().microtikApiConfig.microtikServerConfig.host + "/rest/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    private fun getBasicCredentials(): String {
        val login = Microtik.app.getConfig().microtikApiConfig.microtikServerConfig.login
        val password = Microtik.app.getConfig().microtikApiConfig.microtikServerConfig.password

        return "Basic " + Base64.getEncoder().encodeToString("$login:$password".encodeToByteArray())
    }

    fun getAddressApi(): AddressApi = retrofit.create(AddressApi::class.java)

    fun getAddressListsApi(): AddressListsApi = retrofit.create(AddressListsApi::class.java)

    fun getFirewallFilterApi(): FirewallFilterApi = retrofit.create(FirewallFilterApi::class.java)
}
