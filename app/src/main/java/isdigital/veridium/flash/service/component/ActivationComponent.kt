package isdigital.veridium.flash.service.component

import dagger.Component
import isdigital.veridium.flash.service.module.ActivationService
import isdigital.veridium.flash.viewmodel.ActivationViewModel

@Component(modules = [ActivationService::class])
interface ActivationComponent {
    fun inject(activationViewModel: ActivationViewModel)
}