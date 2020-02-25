package isdigital.veridium.flash.view


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.veridiumid.sdk.BiometricResultsParser
import com.veridiumid.sdk.IBiometricFormats
import com.veridiumid.sdk.IBiometricResultsHandler
import com.veridiumid.sdk.IVeridiumSDK
import com.veridiumid.sdk.fourf.defaultui.activity.DefaultFourFBiometricsActivity
import com.veridiumid.sdk.fourfintegration.ExportConfig
import isdigital.veridium.flash.R
import isdigital.veridium.flash.model.dto.Fingers
import isdigital.veridium.flash.preferences.UserPrefs
import isdigital.veridium.flash.util.*
import isdigital.veridium.flash.viewmodel.ActivationViewModel
import isdigital.veridium.flash.viewmodel.BiometricViewModel
import kotlinx.android.synthetic.main.biometric_fragment.*
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * A simple [Fragment] subclass.
 */
class BiometricFragment : Fragment() {

    private lateinit var fingers: Fingers
    private lateinit var biometricViewModel: BiometricViewModel
    private lateinit var activationViewModel: ActivationViewModel

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

        listeners()

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
                UserPrefs.putHandSelected(
                    context,
                    if (left) HANDS.LEFT.value else HANDS.RIGHT.value
                )

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
            action =
                if (finger == 1) IVeridiumSDK.ACTION_CAPTURE_INDIVIDUALF else IVeridiumSDK.ACTION_CAPTURE
        }, 141)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 141) {
            val handler = object : IBiometricResultsHandler {
                override fun handleSuccess(res: MutableList<ByteArray>?) {
                    validateBiometricPrint(res)
                    Log.d("Biometric sucess", "********************** SCANNER PASSED SUCCESSFULLY")
                }

                override fun handleFailure() {
                    Log.d("Biometric error", "Unknown failure issue")
                }

                override fun handleCancellation() {
                    Log.d("Biometric error", "Cancelled")
                }

                override fun handleError(p0: String?) {
                    Log.d("Biometric error", p0)
                }

                override fun handleLivenessFailure() {
                    Log.d("Biometric error", "Liveness Failure")
                }
            }

            BiometricResultsParser.parse(resultCode, data, handler)
        }
    }

    fun navigateErrorFragment() {
        val action = BiometricFragmentDirections.actionBiometricFragmentToErrorFragment()
        findNavController().navigate(action)
    }

    fun navigateSuccessFragment() {
        val action =
            BiometricFragmentDirections.actionBiometricFragmentToSuccessFragment()
        findNavController().navigate(action)
    }


    @SuppressLint("MissingPermission")
    fun validateBiometricPrint(print: MutableList<ByteArray>?) {

        print?.let {
            showSpinner(activity)
            if (it.size > 0) {
                var result: JSONObject?

                try {
                    val zip: File = File.createTempFile(
                        "tempfile",
                        ".zip",
                        ContextCompat.getCodeCacheDir(context!!)
                    )
                    zip.writeBytes(it[0])
                    val stringBuilder = StringBuilder()
                    val br = BufferedReader(FileReader(zip))
                    var line: String? = br.readLine()

                    while (line != null) {
                        stringBuilder.append(line)
                        stringBuilder.append('\n')
                        line = br.readLine()
                    }

                    br.close()

                    val resultStr = stringBuilder.toString()

                    val tokener = JSONTokener(resultStr)
                    result = JSONObject(tokener)

                    val SCALE085 = result.getJSONObject("SCALE085")
                    val Fingerprints = SCALE085.getJSONArray("Fingerprints")
                    var FingerprintsPositionCode = Fingerprints.getJSONObject(0)
                    var FingerPositionCode = FingerprintsPositionCode.getInt("FingerPositionCode")

                    var posicionBusqueda = 0

                    if (FingerPositionCode == 7) {
                        when (fingers.left) {
                            7 -> posicionBusqueda = 0
                            8 -> posicionBusqueda = 1
                            9 -> posicionBusqueda = 2
                            10 -> posicionBusqueda = 3
                        }

                        FingerprintsPositionCode = Fingerprints.getJSONObject(posicionBusqueda)
                        FingerPositionCode = FingerprintsPositionCode.getInt("FingerPositionCode")

                    } else if (FingerPositionCode == 2) {
                        when (fingers.right) {
                            2 -> posicionBusqueda = 0
                            3 -> posicionBusqueda = 1
                            4 -> posicionBusqueda = 2
                            5 -> posicionBusqueda = 3
                        }

                        FingerprintsPositionCode = Fingerprints.getJSONObject(posicionBusqueda)
                        FingerPositionCode = FingerprintsPositionCode.getInt("FingerPositionCode")
                    } else {
                        // Para el caso de los pulgares, por default coge el pulgar derecho
                        posicionBusqueda = 0
                        FingerprintsPositionCode = Fingerprints.getJSONObject(posicionBusqueda)
                        FingerPositionCode = FingerprintsPositionCode.getInt("FingerPositionCode")
                    }

                    val FingerImpressionImage =
                        FingerprintsPositionCode.getJSONObject("FingerImpressionImage")

                    val jsonInSolutions = HashMap<String, String>()

                    jsonInSolutions["hostName"] = "AAA111BBB222CCC333"
                    jsonInSolutions["dniCliente"] = UserPrefs.getUserDni(context!!)!!
                    jsonInSolutions["rawBase64"] =
                        FingerImpressionImage.getString("BinaryBase64ObjectRAW")
                    jsonInSolutions["rawHeight"] = FingerImpressionImage.getString("Height")
                    jsonInSolutions["rawWidth"] = FingerImpressionImage.getString("Width")
                    this.biometricViewModel.validateVeridiumFingerprints(jsonInSolutions)

                } catch (exception: Exception) {
                    navigateErrorFragment()
                    Toast.makeText(context, "An error was thrown...", Toast.LENGTH_LONG).show()
                    exception.printStackTrace()
                }
            }
        } ?: run {

        }
    }

    fun listeners() {
        activationViewModel = ViewModelProviders.of(this).get(ActivationViewModel::class.java)
        biometricViewModel = ViewModelProviders.of(this).get(BiometricViewModel::class.java)

        this.biometricViewModel.loading.observe(this, Observer { loading ->
            loading?.let {
                if (loading) {

                    if (!biometricViewModel.loadError.value!!) {
                        val form = UserPrefs.getActivation(context)
                        form.iccid = UserPrefs.getIccid(context)

                        activationViewModel.sendFormWithStatus(
                            PartnerData.formPreparation(
                                form, passBarcode = true, passBiometric = true
                            )
                        )
                    } else {
                        UserPrefs.putUserBiometricWrongAttempts(context)

                        val attemps = UserPrefs.getUserBiometricWrongAttempts(context)
                        if (attemps == MAX_BIOMETRIC_SCANNER_TEMPS) {
                            val form = UserPrefs.getActivation(context)
                            form.iccid = UserPrefs.getIccid(context)

                            this.activationViewModel.loading.value = false
                            activationViewModel.sendFormWithStatus(
                                PartnerData.formPreparation(
                                    form, passBarcode = true, passBiometric = false
                                )
                            )
                            return@let
                        }

                        hideSpinner(this.activity)
                        this.view?.csSnackbar(
                            message = "El proceso de validación ha fallado, intentelo nuevamente.",
                            duration = Snackbar.LENGTH_LONG
                        )
                    }
                }
            }
        })

        this.activationViewModel.loading.observe(this, Observer { loading ->
            loading?.let {
                if (!loading) {
                    return@let
                }

                this.activationViewModel.formError.value?.let {
                    hideSpinner(this.activity)
                    if (it) {
                        navigateErrorFragment()
                    } else {
                        val attemps = UserPrefs.getUserBiometricWrongAttempts(context)
                        if (attemps == MAX_BIOMETRIC_SCANNER_TEMPS) {
                            navigateErrorFragment()
                            return@let
                        }
                        navigateSuccessFragment()
                    }
                }
            }
        })
    }
}
