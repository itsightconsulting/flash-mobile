package pe.mobile.cuy.service.module

import pe.mobile.cuy.api.OrderApi
import pe.mobile.cuy.di.DaggerApiComponent
import pe.mobile.cuy.model.dto.ResponseVerifyDNI
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