package isdigital.veridium.flash

import android.app.Application
import com.veridiumid.sdk.VeridiumSDK
import com.veridiumid.sdk.activities.DefaultVeridiumSDKModelFactory
import com.veridiumid.sdk.defaultdata.VeridiumSDKDataInitializer
import com.veridiumid.sdk.fourf.ExportConfig
import com.veridiumid.sdk.fourf.VeridiumSDKFourFInitializer
import com.veridiumid.sdk.model.biometrics.packaging.IBiometricFormats

class FlashApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this

        // TODO add here the 4 FingersID Licence
        val fourfLicence = "mQiLZsQ9zmwb5RL/+PG7tQpTYCSYmJpAJ2c4C18DJeHalrKHx0UdSjFZeyHiN+5LfLtB9kZNwnA+tXgAZmhzDXsiZGV2aWNlRmluZ2VycHJpbnQiOiIvZ1hnSzZiNzNHK2R6VTJvbFVRT0pnR24wa2VZNGhLYUd5UmxiU2tQZURrPSIsImxpY2Vuc2UiOiJFaU9HeWNlN1A1cmRoMEJFRzNFb1dRaldGOGtOTFg0RjZJS2FwYjR2aDVKVzB2ZXpoV0V3RzR5SzJzVWZwaThPWk5EM25acVVyVEwyMGlkd3hRZ29CM3NpZEhsd1pTSTZJa0pKVDB4SlFsTWlMQ0p1WVcxbElqb2lORVlpTENKc1lYTjBUVzlrYVdacFpXUWlPakUxT0RVMU9EQXhOekEwT0RBc0ltTnZiWEJoYm5sT1lXMWxJam9pU1c1emIyeDFkR2x2Ym5NZ1ptOXlJRVpzWVhOb0lpd2lZMjl1ZEdGamRFbHVabThpT2lKRVJWWWdMU0JHYkdGemFDQk5iMkpwYkdVZ0xTQnBjMlJwWjJsMFlXd3VkbVZ5YVdScGRXMHVabXhoYzJnaUxDSmpiMjUwWVdOMFJXMWhhV3dpT2lKdGFXZDFaV3d1YUdWeWJtRnVaR1Y2UUdsdWMyOXNkWFJwYjI1ekxuQmxJaXdpYzNWaVRHbGpaVzV6YVc1blVIVmliR2xqUzJWNUlqb2lRV1F5TkZKaWNFRlZjVEpoYTBaVGVreHBZM0JoYkZveFZ6UnVaVzV2YzBkSVFsTlpVMFp5TlZVdk1EMGlMQ0p6ZEdGeWRFUmhkR1VpT2pFMU9ESXdNREl3TURBd01EQXNJbVY0Y0dseVlYUnBiMjVFWVhSbElqb3hOVGc0TWpFNU1qQXdNREF3TENKbmNtRmpaVVZ1WkVSaGRHVWlPakUxT0Rnek1EVTJNREF3TURBc0luVnphVzVuVTBGTlRGUnZhMlZ1SWpwbVlXeHpaU3dpZFhOcGJtZEdjbVZsVWtGRVNWVlRJanBtWVd4elpTd2lkWE5wYm1kQlkzUnBkbVZFYVhKbFkzUnZjbmtpT21aaGJITmxMQ0ppYVc5c2FXSkdZV05sUlhod2IzSjBSVzVoWW14bFpDSTZabUZzYzJVc0luSjFiblJwYldWRmJuWnBjbTl1YldWdWRDSTZleUp6WlhKMlpYSWlPbVpoYkhObExDSmtaWFpwWTJWVWFXVmtJanBtWVd4elpYMHNJbVpsWVhSMWNtVnpJanA3SW1KaGMyVWlPblJ5ZFdVc0luTjBaWEpsYjB4cGRtVnVaWE56SWpwMGNuVmxMQ0psZUhCdmNuUWlPblJ5ZFdWOUxDSmxibVp2Y21ObFpGQnlaV1psY21WdVkyVnpJanA3SW0xaGJtUmhkRzl5ZVV4cGRtVnVaWE56SWpwbVlXeHpaWDBzSW5abGNuTnBiMjRpT2lJMUxpb2lmUT09In0="
        VeridiumSDK.init(this,
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