package com.itsight.flash.di

import com.itsight.flash.di.module.ApiModule
import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {
    /*fun injectUserService(userService: UserService)*/

}