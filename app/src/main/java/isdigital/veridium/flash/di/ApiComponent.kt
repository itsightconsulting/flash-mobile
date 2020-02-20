package isdigital.veridium.flash.di

import isdigital.veridium.flash.di.module.ApiModule
import isdigital.veridium.flash.service.module.OrderService
import isdigital.veridium.flash.service.module.ActivationService
import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {

    fun injectOrderService(OrderService: OrderService)
    fun injectActivationService(ActivationService: ActivationService)
}