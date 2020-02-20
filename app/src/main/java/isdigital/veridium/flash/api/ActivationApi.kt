package isdigital.veridium.flash.api

import io.reactivex.Single
import isdigital.veridium.flash.model.dto.ConsolidatedDataResponse
import isdigital.veridium.flash.model.dto.VerifyIccidResponse
import isdigital.veridium.flash.util.API_VERSION_V1
import retrofit2.http.*

interface ActivationApi {
    @FormUrlEncoded
    @POST("$API_VERSION_V1/users")
    fun saveActivation(
        @Field("formId") formId: String?,
        @Field("dni") dni: String,
        @Field("formStatus") formStatus: String,
        @Field("iccid") iccid: String,
        @Field("formCreationDate") formCreationDate: String,
        @Field("name") name: String,
        @Field("lastName") lastName: String,
        @Field("birthDate") birthDate: String,
        @Field("email") email: String,
        @Field("sponsorTeamId") sponsorTeamId: String?,
        @Field("wantPortability") wantPortability: Boolean,
        @Field("phoneNumber") phoneNumber: String?,
        @Field("planType") planType: String?,
        @Field("validationBiometric") validationBiometric: Boolean,
        @Field("validationBiometricDate") validationBiometricDate: String?
    ): Single<ConsolidatedDataResponse>

    @GET( "$API_VERSION_V1/users")
    fun validateICCID(@Query("iccid") iccid: String): Single<VerifyIccidResponse>
}