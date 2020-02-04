package com.itsight.flash.di.module

import dagger.Module
import dagger.Provides

@Module
class UserModule(val token: String) {

    @Provides
    fun providesToken() = token
}