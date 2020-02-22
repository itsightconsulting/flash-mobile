package isdigital.veridium.flash.view


import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.veridiumid.sdk.IBiometricFormats
import com.veridiumid.sdk.IVeridiumSDK
import com.veridiumid.sdk.fourf.defaultui.activity.DefaultFourFBiometricsActivity
import com.veridiumid.sdk.fourfintegration.ExportConfig
import isdigital.veridium.flash.R
import isdigital.veridium.flash.model.dto.Fingers
import isdigital.veridium.flash.preferences.UserPrefs
import isdigital.veridium.flash.util.csSnackbar
import kotlinx.android.synthetic.main.biometric_fragment.*

/**
 * A simple [Fragment] subclass.
 */
class BiometricFragment : Fragment() {

    private lateinit var fingers: Fingers

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

        this.fingers = UserPrefs.getBestFingerPrints(context!!)
        imgLeftHand.setImageResource(getImageResourceId(this.fingers.left))
        imgRightHand.setImageResource(getImageResourceId(this.fingers.right))
        leftFingerDesc.text = this.fingers.descriptionLeft
        rightFingerDesc.text = this.fingers.descriptionRight

        imgLeftHand.setOnClickListener {
            if (it.alpha == 1.0f) {
                return@setOnClickListener
            }
            imgRightHand.alpha = it.alpha
            it.alpha = 1.0f

        }

        imgRightHand.setOnClickListener {
            if (it.alpha == 1.0f) {
                return@setOnClickListener
            }
            imgLeftHand.alpha = it.alpha
            it.alpha = 1.0f
        }

        btnVeridiumInit.setOnClickListener {
            val left = imgLeftHand.alpha == 1.0f
            val right = imgRightHand.alpha == 1.0f
            if (left || right) {
                launchVeridium(
                    if (left) ExportConfig.CaptureHand.LEFT_ENFORCED else ExportConfig.CaptureHand.RIGHT_ENFORCED,
                    (if (left) this.fingers.left else this.fingers.right) % 5
                )
            } else {
                this.view?.csSnackbar(
                    "Debe seleccionar una huella antes de continuar",
                    Snackbar.LENGTH_SHORT
                )
            }
        }
    }

    private fun getImageResourceId(fingerId: Int): Int {
        when (fingerId) {
            1 -> {
                return R.drawable.right_thumb
            }
            2 -> {
                return R.drawable.right_index
            }
            3 -> {
                return R.drawable.right_middle
            }
            4 -> {
                return R.drawable.right_ring
            }
            5 -> {
                return R.drawable.right_little
            }
            6 -> {
                return R.drawable.left_thumb
            }
            7 -> {
                return R.drawable.left_index
            }
            8 -> {
                return R.drawable.left_middle
            }
            9 -> {
                return R.drawable.left_ring
            }
            else -> {
                return R.drawable.left_little
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
//        imgLeftHand.setColorFilter(
//            ContextCompat.getColor(context!!, R.color.black),
//            PorterDuff.Mode.SRC_IN
//        )
    }


    fun launchVeridium(hand: ExportConfig.CaptureHand, finger: Int) {
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
    }

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
