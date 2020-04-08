package isdigital.veridium.flash.veridium

import androidx.fragment.app.Fragment
import com.veridiumid.sdk.defaultdata.DataStorage
import com.veridiumid.sdk.fourf.FourFBiometricsActivity
import com.veridiumid.sdk.fourf.FourFUIInterface
import com.veridiumid.sdk.fourf.ui.authenticator.AuthenticatorFourFFragment
import com.veridiumid.sdk.model.data.persistence.IKVStore

class CustomFourFActivity : FourFBiometricsActivity() {
    override fun <FourFFragmentInterfaceUnion> fragmentToShow(): FourFFragmentInterfaceUnion where FourFFragmentInterfaceUnion : Fragment?, FourFFragmentInterfaceUnion : FourFUIInterface? {
        val authenticatorFourFFragment = AuthenticatorFourFFragment()
        return authenticatorFourFFragment as (FourFFragmentInterfaceUnion)
    }

    override fun openStorage(): IKVStore {
        return DataStorage.getDefaultStorage()
    }
}