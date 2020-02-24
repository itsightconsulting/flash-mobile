package isdigital.veridium.flash.api

import io.reactivex.Single
import isdigital.veridium.flash.model.dto.BestFingers
import isdigital.veridium.flash.model.dto.ReniecUser
import isdigital.veridium.flash.model.generic.ApiResponse
import isdigital.veridium.flash.util.API_VERSION_V1
import retrofit2.http.Body
import retrofit2.http.POST

interface BiometricApi {

    @POST("${API_VERSION_V1}/biometrics/best")
    fun getBestFingerprintsByDni(@Body body: HashMap<String, String>): Single<ApiResponse<BestFingers>>

    @POST("${API_VERSION_V1}/biometrics/validation")
    fun validateFingerprints(@Body body: HashMap<String, String>): Single<ApiResponse<ReniecUser>>
}