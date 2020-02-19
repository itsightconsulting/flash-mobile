package isdigital.veridium.flash.service.module

import isdigital.veridium.flash.api.OrderApi
import isdigital.veridium.flash.di.DaggerApiComponent
import isdigital.veridium.flash.model.dto.ResponseVerifyDNI
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