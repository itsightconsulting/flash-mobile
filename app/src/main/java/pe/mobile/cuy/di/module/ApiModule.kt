package pe.mobile.cuy.di.module

import pe.mobile.cuy.api.OrderApi
import pe.mobile.cuy.configuration.ServiceManager
import dagger.Module
import dagger.Provides


@Module
class ApiModule {

    init {

    }

    @Provides
    fun restApiOrder(): OrderApi {
        return ServiceManager().createService(OrderApi::class.java)
    }
}