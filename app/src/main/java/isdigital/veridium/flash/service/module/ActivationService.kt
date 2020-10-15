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
        body: HashMap<String, String>
    ): Single<ConsolidatedDataResponse> = api.saveActivation(
        body
    )

    fun validateICCID(iccid: String, body: HashMap<String, String>): Single<VerifyIccidResponse> =
        api.validateICCID(iccid, body)
}