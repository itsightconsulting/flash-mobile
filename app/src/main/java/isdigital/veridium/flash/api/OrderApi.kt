package isdigital.veridium.flash.api

import isdigital.veridium.flash.model.dto.ResponseVerifyDNI
import isdigital.veridium.flash.util.API_VERSION_V1
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface OrderApi {

    @GET("$API_VERSION_V1/users/{dni}")
    fun findAllByDNI(@Path("dni") dni: String): Single<ResponseVerifyDNI>

}