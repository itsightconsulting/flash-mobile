package com.itsight.flash.di.module

import com.itsight.flash.api.OrderApi
import com.itsight.flash.configuration.ServiceManager
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