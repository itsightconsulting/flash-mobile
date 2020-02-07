package com.itsight.flash.service.component

import com.itsight.flash.service.module.OrderService
import com.itsight.flash.viewmodel.OrderViewModel
import dagger.Component

@Component(modules = [OrderService::class])
interface OrderComponent {

    fun inject(orderViewModel: OrderViewModel)
}