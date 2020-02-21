package isdigital.veridium.flash.service.component

import dagger.Component
import isdigital.veridium.flash.service.module.BiometricService
import isdigital.veridium.flash.viewmodel.BiometricViewModel

@Component(modules = [BiometricService::class])
interface BiometricComponent {

    fun inject(biometricViewModel: BiometricViewModel)
}