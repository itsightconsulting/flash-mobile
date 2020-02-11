package pe.mobile.cuy.di

import pe.mobile.cuy.di.module.ApiModule
import pe.mobile.cuy.service.module.OrderService
import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {
    
    fun injectOrderService(OrderService: OrderService)

}