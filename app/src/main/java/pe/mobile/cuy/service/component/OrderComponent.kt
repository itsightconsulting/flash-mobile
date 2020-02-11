package pe.mobile.cuy.service.component

import pe.mobile.cuy.service.module.OrderService
import pe.mobile.cuy.viewmodel.OrderViewModel
import dagger.Component

@Component(modules = [OrderService::class])
interface OrderComponent {

    fun inject(orderViewModel: OrderViewModel)
}