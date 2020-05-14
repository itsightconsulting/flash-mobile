package isdigital.veridium.flash

import androidx.multidex.MultiDexApplication
import com.veridiumid.sdk.VeridiumSDK
import com.veridiumid.sdk.activities.DefaultVeridiumSDKModelFactory
import com.veridiumid.sdk.defaultdata.VeridiumSDKDataInitializer
import com.veridiumid.sdk.fourf.VeridiumSDKFourFInitializer

class FlashApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        appContext = this

        // TODO add here the 4 FingersID Licence
        val fourfLicence =
            "kfWS50ag30kQT1W392M+XSDSfskjYE5KAHuCRyNo8HPRE2RiX989l3yn3dS3OONp7aVpXeKzvBNWaJG+hZG1BHsiZGV2aWNlRmluZ2VycHJpbnQiOiIvZ1hnSzZiNzNHK2R6VTJvbFVRT0pnR24wa2VZNGhLYUd5UmxiU2tQZURrPSIsImxpY2Vuc2UiOiJ3Q0p2NjJkL1kwNmVtRGNPeUJrVDJONndvLzdOWVhQNVpZZHZTQjRuQmxtb3o0alc2K1RHck9MZWVjNjcwckpPQ2tBMlovTzM2bjM4aXhVWkJ1NUxESHNpZEhsd1pTSTZJa0pKVDB4SlFsTWlMQ0p1WVcxbElqb2lORVlpTENKc1lYTjBUVzlrYVdacFpXUWlPakUxT0RneU5UazVNelF3TWpFc0ltTnZiWEJoYm5sT1lXMWxJam9pU1c1emIyeDFkR2x2Ym5NZ1ptOXlJRVpzWVhOb0lpd2lZMjl1ZEdGamRFbHVabThpT2lKUVVrOUVJQzBnUm14aGMyZ2dUVzlpYVd4bElDMGdhWE5rYVdkcGRHRnNMblpsY21sa2FYVnRMbVpzWVhOb0lDMGdRVzVrY205cFpDSXNJbU52Ym5SaFkzUkZiV0ZwYkNJNkltMXBaM1ZsYkM1b1pYSnVZVzVrWlhwQWFXNXpiMngxZEdsdmJuTXVjR1VpTENKemRXSk1hV05sYm5OcGJtZFFkV0pzYVdOTFpYa2lPaUpETUVaRloxTk5VRW93TDFoUFJ6WjJNSGhoTWtFMWJGQTVkRFV3U2sxYWMxZE9WVlp4UW1sM1RXMVpQU0lzSW5OMFlYSjBSR0YwWlNJNk1UVTROVFl5TnpJd01EQXdNQ3dpWlhod2FYSmhkR2x2YmtSaGRHVWlPakUyTVRjeE5qTXlNREF3TURBc0ltZHlZV05sUlc1a1JHRjBaU0k2TVRZeE56azBNRGd3TURBd01Dd2lkWE5wYm1kVFFVMU1WRzlyWlc0aU9tWmhiSE5sTENKMWMybHVaMFp5WldWU1FVUkpWVk1pT21aaGJITmxMQ0oxYzJsdVowRmpkR2wyWlVScGNtVmpkRzl5ZVNJNlptRnNjMlVzSW1KcGIyeHBZa1poWTJWRmVIQnZjblJGYm1GaWJHVmtJanBtWVd4elpTd2ljblZ1ZEdsdFpVVnVkbWx5YjI1dFpXNTBJanA3SW5ObGNuWmxjaUk2Wm1Gc2MyVXNJbVJsZG1salpWUnBaV1FpT21aaGJITmxmU3dpWm1WaGRIVnlaWE1pT25zaVltRnpaU0k2ZEhKMVpTd2ljM1JsY21WdlRHbDJaVzVsYzNNaU9uUnlkV1VzSW1WNGNHOXlkQ0k2ZEhKMVpYMHNJbVZ1Wm05eVkyVmtVSEpsWm1WeVpXNWpaWE1pT25zaWJXRnVaR0YwYjNKNVRHbDJaVzVsYzNNaU9tWmhiSE5sZlN3aWRtVnljMmx2YmlJNklqVXVLaUo5In0="
        VeridiumSDK.init(
            this,
            DefaultVeridiumSDKModelFactory(applicationContext),
            VeridiumSDKFourFInitializer(fourfLicence),
            VeridiumSDKDataInitializer()
        )
    }

    companion object {
        lateinit var appContext: FlashApplication
            private set
    }
}