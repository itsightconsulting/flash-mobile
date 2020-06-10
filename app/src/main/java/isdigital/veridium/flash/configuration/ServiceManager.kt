package isdigital.veridium.flash.configuration

import android.util.Log
import androidx.preference.PreferenceManager
import isdigital.veridium.flash.util.API_BASE_URL
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import isdigital.veridium.flash.FlashApplication
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

    fun <T> createServiceSimple(clazz: Class<T>): T {
        return createServiceSimple(clazz, HttpUrl.parse(API_BASE_URL)!!)
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
    private fun <T> createService(clazz: Class<T>, httpUrl: HttpUrl): T {
        val retrofit = getRetrofit(httpUrl)
        return retrofit.create(clazz)
    }

    private fun <T> createServiceSimple(clazz: Class<T>, httpUrl: HttpUrl): T {
        val retrofit = getRetrofitSimple(httpUrl)
        return retrofit.create(clazz)
    }

    fun <T> createService(clazz: Class<T>, retrofit: Retrofit): T {
        return retrofit.create(clazz)
    }

    private fun getRetrofitSimple(httpUrl: HttpUrl): Retrofit {
        return Retrofit.Builder()
            .baseUrl(httpUrl)
            .client(createSimpleClient())
            .addConverterFactory(getConverter())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
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


    private fun createSimpleClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.MINUTES).build()
    }


    private fun createClient(): OkHttpClient {

        return OkHttpClient.Builder()
            .readTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.MINUTES).addInterceptor { chain ->
                val token = PreferenceManager.getDefaultSharedPreferences(FlashApplication.appContext)
                    .getString("API_TOKEN", "")
                Log.d("TOKENREQ", token)
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .addHeader("token", token?:"")
                    .build()
                chain.proceed(newRequest)
            }.build()
    }
}