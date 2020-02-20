package isdigital.veridium.flash.service.component

import isdigital.veridium.flash.service.module.OrderService
import isdigital.veridium.flash.viewmodel.OrderViewModel
import dagger.Component

@Component(modules = [OrderService::class])
interface OrderComponent {

    fun inject(orderViewModel: OrderViewModel)
}