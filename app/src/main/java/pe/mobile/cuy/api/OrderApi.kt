package pe.mobile.cuy.api

import pe.mobile.cuy.model.dto.ResponseVerifyDNI
import pe.mobile.cuy.util.API_VERSION_V1
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface OrderApi {

    @GET("$API_VERSION_V1/users/{dni}")
    fun findAllByDNI(@Path("dni") dni: String): Single<ResponseVerifyDNI>

}