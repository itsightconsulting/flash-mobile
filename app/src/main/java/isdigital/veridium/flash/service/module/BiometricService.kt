package isdigital.veridium.flash.service.module

import dagger.Module
import dagger.Provides
import isdigital.veridium.flash.api.BiometricApi
import isdigital.veridium.flash.di.DaggerApiComponent
import javax.inject.Inject


@Module
class BiometricService {

    @Inject
    lateinit var api: BiometricApi

    init {
        DaggerApiComponent.create().injectBiometricService(this)
    }

    @Provides
    fun get() = this

    fun getBestFingerprintsByDni(body:  HashMap<String, String>) = api.getBestFingerprintsByDni(body)

    fun validateFingerprints(body:  HashMap<String, String>) = api.validateFingerprints(body)

}