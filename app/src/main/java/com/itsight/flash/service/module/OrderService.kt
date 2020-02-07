package com.itsight.flash.service.module

import com.itsight.flash.api.OrderApi
import com.itsight.flash.di.DaggerApiComponent
import com.itsight.flash.model.dto.ResponseVerifyDNI
import dagger.Module
import dagger.Provides
import io.reactivex.Single
import javax.inject.Inject

@Module
class OrderService {

    @Inject
    lateinit var api: OrderApi

    init {
        DaggerApiComponent.create().injectOrderService(this)
    }

    @Provides
    fun get() = this

    fun findAllByDNI(dni: String): Single<ResponseVerifyDNI> = api.findAllByDNI(dni)
}