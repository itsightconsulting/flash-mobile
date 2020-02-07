package com.itsight.flash.configuration

import androidx.preference.PreferenceManager
import com.itsight.flash.FlashApplication
import com.itsight.flash.util.API_BASE_URL
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class ServiceManager {

    /**
     * Gets the instance of the web services implementation.
     *
     * @return the singleton instance.
     */
    companion object {
        private var sServiceManager: ServiceManager? = null

        fun get(): ServiceManager {
            if (sServiceManager == null) {
                sServiceManager = ServiceManager()
            }
            return sServiceManager as ServiceManager
        }
    }


    /**
     * Creates the services for a given HTTP Url, useful when testing
     * through multiple endpoints and unit testing
     *
     * @param clazz the service class.
     * @param <T>   type of the service.
     * @return the created services implementation.
    </T> */
    fun <T> createService(clazz: Class<T>): T {
        return createService(clazz, HttpUrl.parse(API_BASE_URL)!!)
    }

    /**
     * Creates the services for a given HTTP Url, useful when testing
     * through multiple endpoints and unit testing
     *
     * @param clazz   the service class.
     * @param httpUrl the endpoint
     * @param <T>     type of the service.
     * @return the created services implementation.
    </T> */
    fun <T> createService(clazz: Class<T>, httpUrl: HttpUrl): T {
        val retrofit = getRetrofit(httpUrl)
        return retrofit.create(clazz)
    }

    fun <T> createService(clazz: Class<T>, retrofit: Retrofit): T {
        return retrofit.create(clazz)
    }

    private fun getRetrofit(httpUrl: HttpUrl): Retrofit {
        return Retrofit.Builder()
            .baseUrl(httpUrl)
            .client(createClient())
            .addConverterFactory(getConverter())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    fun getPlainRetrofit(httpUrl: HttpUrl): Retrofit {
        return Retrofit.Builder()
            .baseUrl(httpUrl)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(getConverter())
            .build()
    }

    private fun getConverter(): Converter.Factory {
        return GsonConverterFactory.create()
    }


    private fun createClient(): OkHttpClient {
        val token =
            PreferenceManager.getDefaultSharedPreferences(FlashApplication.appContext).getString("API_TOKEN", "")

        return OkHttpClient.Builder()
            .readTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.MINUTES).addInterceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(newRequest)
        }.build()
        //return OkHttpClient.Builder().addInterceptor(CustomRequestInterceptor()).build()
    }
}