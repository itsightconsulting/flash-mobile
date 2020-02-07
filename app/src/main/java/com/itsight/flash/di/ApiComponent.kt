package com.itsight.flash.di

import com.itsight.flash.di.module.ApiModule
import com.itsight.flash.service.module.OrderService
import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {
    
    fun injectOrderService(OrderService: OrderService)

}