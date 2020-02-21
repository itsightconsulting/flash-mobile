package isdigital.veridium.flash.api

import io.reactivex.Single
import isdigital.veridium.flash.model.dto.Token
import isdigital.veridium.flash.model.generic.ApiResponse
import isdigital.veridium.flash.util.API_VERSION_V1
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenApi {

    @POST("$API_VERSION_V1/auth")
    fun getToken(@Body body: HashMap<String, String>): Call<ApiResponse<Token>>
}