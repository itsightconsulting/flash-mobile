package pe.mobile.cuy.api

import io.reactivex.Single
import pe.mobile.cuy.model.dto.ConsolidatedDataResponse
import pe.mobile.cuy.util.API_VERSION_V1
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

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
        @Field("validationBiometricDate") validationBiometricDate: String
    ): Single<ConsolidatedDataResponse>
}