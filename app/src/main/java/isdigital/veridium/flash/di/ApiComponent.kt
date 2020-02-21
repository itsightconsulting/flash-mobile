package isdigital.veridium.flash.di

import dagger.Component
import isdigital.veridium.flash.di.module.ApiModule
import isdigital.veridium.flash.service.module.ActivationService
import isdigital.veridium.flash.service.module.BiometricService
import isdigital.veridium.flash.service.module.OrderService

@Component(modules = [ApiModule::class])
interface ApiComponent {

    fun injectOrderService(OrderService: OrderService)
    fun injectActivationService(ActivationService: ActivationService)
    fun injectBiometricService(BiometricService: BiometricService)
}