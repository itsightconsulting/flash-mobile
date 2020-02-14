package pe.mobile.cuy.di.module

import pe.mobile.cuy.api.OrderApi
import pe.mobile.cuy.configuration.ServiceManager
import dagger.Module
import dagger.Provides
import pe.mobile.cuy.api.ActivationApi


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


}