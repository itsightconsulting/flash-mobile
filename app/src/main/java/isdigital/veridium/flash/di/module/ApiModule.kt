package isdigital.veridium.flash.di.module

import isdigital.veridium.flash.api.OrderApi
import isdigital.veridium.flash.configuration.ServiceManager
import dagger.Module
import dagger.Provides
import isdigital.veridium.flash.api.ActivationApi
import isdigital.veridium.flash.api.BiometricApi


@Module
class ApiModule {

    init {

    }

    @Provides
    fun restApiOrder(): OrderApi {
        return ServiceManager().createService(OrderApi::class.java)
    }


    @Provides
    fun restApiActivation(): ActivationApi {
        return ServiceManager().createService(ActivationApi::class.java)
    }

    @Provides
    fun restApiBiometric(): BiometricApi {
        return ServiceManager().createService(BiometricApi::class.java)
    }


}