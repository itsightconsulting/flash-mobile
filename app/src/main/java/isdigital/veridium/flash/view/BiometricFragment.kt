package isdigital.veridium.flash.view


import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import isdigital.veridium.flash.R

/**
 * A simple [Fragment] subclass.
 */
class BiometricFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.biometric_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /*imgLeftHand.setOnClickListener {

            val fingerSelected = 1
            MyVeridiumPreferencesManager.saveFinger(context!!, "$fingerSelected")

            launchVeridium(
                ExportConfig.CaptureHand.LEFT_ENFORCED,
                MyVeridiumPreferencesManager.getFinger(context!!).toInt() % 5
            )
        }

        imgRightHand.setOnClickListener {

            val fingerSelected = 6
            MyVeridiumPreferencesManager.saveFinger(context!!, "$fingerSelected")
            launchVeridium(
                ExportConfig.CaptureHand.RIGHT_ENFORCED,
                MyVeridiumPreferencesManager.getFinger(context!!).toInt() % 5
            )
        }*/
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
//        imgLeftHand.setColorFilter(
//            ContextCompat.getColor(context!!, R.color.black),
//            PorterDuff.Mode.SRC_IN
//        )
    }


    /*fun launchVeridium(hand: ExportConfig.CaptureHand, finger: Int) {
        ExportConfig.setCaptureHand(hand)
        ExportConfig.setIndividualthumb(finger == 1)
        ExportConfig.setIndividualindex(finger == 2)
        ExportConfig.setIndividualmiddle(finger == 3)
        ExportConfig.setIndividualring(finger == 4)
        ExportConfig.setIndividuallittle(finger == 5)

        ExportConfig.setFormat(IBiometricFormats.TemplateFormat.FORMAT_JSON)
        ExportConfig.setCalculate_NFIQ(true)
        ExportConfig.setBackground_remove(true)
        ExportConfig.setPackDebugInfo(false)
        ExportConfig.setPackExtraScale(true)
        ExportConfig.setPackAuditImage(true)
        ExportConfig.setLaxLiveness(false)

        ExportConfig.setPack_raw_scaled(true)
        ExportConfig.getPack_raw_scaled()

        ExportConfig.getPack_wsq_scaled()
        ExportConfig.setPack_wsq_scaled(true)

        ExportConfig.setUseNistType4(false)
        ExportConfig.setWSQCompressionBitrate(5f)

        startActivityForResult(Intent(context, DefaultFourFBiometricsActivity::class.java).apply {
            putExtra("uid", "4F")
            putExtra("optional", "true")
            putExtra("validator", "com.veridiumid.sdk.fourf.FourFValidator")
            action = IVeridiumSDK.ACTION_CAPTURE_INDIVIDUALF
        }, 141)
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 141) {
            if (data != null) {
                val action = BiometricFragmentDirections.actionBiometricFragmentToSuccessFragment()
                findNavController().navigate(action)
            } else {
                val action = BiometricFragmentDirections.actionBiometricFragmentToErrorFragment()
                findNavController().navigate(action)
            }
        }
    }
}
