package com.itsight.flash

import android.app.Application
import com.veridiumid.sdk.VeridiumSDK
import com.veridiumid.sdk.fourf.VeridiumFourFInitializer

class FlashApplication: Application() {override fun onCreate() {
    super.onCreate()
    appContext = this

    VeridiumSDK.init(applicationContext, VeridiumFourFInitializer())
}

    companion object {
        lateinit var appContext: FlashApplication
            private set
    }
}