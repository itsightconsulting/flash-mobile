package isdigital.veridium.flash.api

import io.reactivex.Single
import isdigital.veridium.flash.model.dto.ConsolidatedDataResponse
import isdigital.veridium.flash.model.dto.VerifyIccidResponse
import isdigital.veridium.flash.util.API_VERSION_V1
import retrofit2.http.*

interface ActivationApi {

    @POST("$API_VERSION_V1/users")
    fun saveActivation(
    @Body body: HashMap<String, String>
    ): Single<ConsolidatedDataResponse>

    @GET( "$API_VERSION_V1/simcards/{iccid}")
    fun validateICCID(@Path("iccid") iccid: String): Single<VerifyIccidResponse>

}