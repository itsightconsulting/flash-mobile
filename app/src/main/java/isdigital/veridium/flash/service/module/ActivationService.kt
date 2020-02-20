package isdigital.veridium.flash.service.module

import dagger.Module
import dagger.Provides
import io.reactivex.Single
import isdigital.veridium.flash.api.ActivationApi
import isdigital.veridium.flash.di.DaggerApiComponent
import isdigital.veridium.flash.model.dto.ConsolidatedDataResponse
import isdigital.veridium.flash.model.dto.VerifyIccidResponse
import javax.inject.Inject

@Module
class ActivationService {

    @Inject
    lateinit var api: ActivationApi

    init {
        DaggerApiComponent.create().injectActivationService(this)
    }

    @Provides
    fun get() = this

    fun saveActivationForm(
        formId: String?,
        dni: String,
        formStatus: String,
        iccid: String,
        formCreationDate: String,
        name: String,
        lastName: String,
        birthDate: String,
        email: String,
        sponsorTeamId: String?,
        wantPortability: Boolean,
        phoneNumber: String?,
        planType: String?,
        validationBiometric: Boolean,
        validationBiometricDate: String?
    ): Single<ConsolidatedDataResponse> = api.saveActivation(
        formId,
        dni,
        formStatus,
        iccid,
        formCreationDate,
        name,
        lastName,
        birthDate,
        email,
        sponsorTeamId,
        wantPortability,
        phoneNumber,
        planType,
        validationBiometric,
        validationBiometricDate
    )

    fun validateICCID(iccid: String): Single<VerifyIccidResponse> = api.validateICCID(iccid)
}