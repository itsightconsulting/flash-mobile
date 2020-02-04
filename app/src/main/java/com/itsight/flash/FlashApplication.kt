package com.itsight.flash

import android.app.Application

class FlashApplication: Application() {override fun onCreate() {
    super.onCreate()
    appContext = this
}

    companion object {
        lateinit var appContext: FlashApplication
            private set
    }
}