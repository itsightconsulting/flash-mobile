package pe.mobile.cuy.service.component

import dagger.Component
import pe.mobile.cuy.service.module.ActivationService
import pe.mobile.cuy.viewmodel.ActivationViewModel

@Component(modules = [ActivationService::class])
interface ActivationComponent {
    fun inject(activationViewModel: ActivationViewModel)
}