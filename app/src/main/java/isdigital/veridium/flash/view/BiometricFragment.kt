package isdigital.veridium.flash.view


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.veridiumid.sdk.VeridiumSDK
import com.veridiumid.sdk.fourf.ExportConfig
import com.veridiumid.sdk.fourf.FourFInterface
import com.veridiumid.sdk.model.biometrics.packaging.IBiometricFormats
import com.veridiumid.sdk.model.biometrics.results.BiometricResultsParser
import com.veridiumid.sdk.model.biometrics.results.handling.IBiometricResultsHandler
import isdigital.veridium.flash.R
import isdigital.veridium.flash.model.dto.Fingers
import isdigital.veridium.flash.preferences.UserPrefs
import isdigital.veridium.flash.util.*
import isdigital.veridium.flash.viewmodel.ActivationViewModel
import isdigital.veridium.flash.viewmodel.BiometricViewModel
import kotlinx.android.synthetic.main.biometric_fragment.*
import org.json.JSONObject
import org.json.JSONTokener
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest


/**
 * A simple [Fragment] subclass.
 */
class BiometricFragment : Fragment(),
    EasyPermissions.PermissionCallbacks {

    private val REQUEST_CAMERA_CAPTURE = 1
    private lateinit var fingers: Fingers
    private lateinit var biometricViewModel: BiometricViewModel
    private lateinit var activationViewModel: ActivationViewModel
    private val REQUEST_EXPORT = 314

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

                EasyPermissions.requestPermissions(
                    PermissionRequest.Builder(
                        this,
                        REQUEST_CAMERA_CAPTURE,
                        Manifest.permission.CAMERA
                    )
                        .setRationale("¿Podría concedernos el permiso para acceder a su cámara?")
                        .setPositiveButtonText("ACEPTAR")
                        .setNegativeButtonText("CANCELAR")
                        .build()
                )
            } else {
                this.view?.csSnackbar(
                    "Debe seleccionar una huella antes de continuar",
                    Snackbar.LENGTH_SHORT
                )
            }
        }
    }

    private fun initVeridium() {
        val left = imgLeftHand.alpha == 1.0f

        launchVeridium(
            if (left) ExportConfig.ExportMode.FOUR_F_LEFT_ENFORCED else ExportConfig.ExportMode.FOUR_F_RIGHT_ENFORCED,
            (if (left) this.fingers.left else this.fingers.right) % 5
        )
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
    }


    private fun launchVeridium(hand: ExportConfig.ExportMode, finger: Int) {
        val finalFinger =
            if (hand == ExportConfig.ExportMode.FOUR_F_LEFT_ENFORCED) this.fingers.left else this.fingers.right

        when (finalFinger) {
            1 -> {
                ExportConfig.setFingersToCapture(listOf(ExportConfig.FingerID.THUMB_RIGHT))
            }
            2 -> {
                ExportConfig.setFingersToCapture(ExportConfig.ExportMode.FOUR_F_RIGHT_ENFORCED)
            }
            3 -> {
                ExportConfig.setFingersToCapture(ExportConfig.ExportMode.FOUR_F_RIGHT_ENFORCED)
            }
            4 -> {
                ExportConfig.setFingersToCapture(ExportConfig.ExportMode.FOUR_F_RIGHT_ENFORCED)
            }
            5 -> {
                ExportConfig.setFingersToCapture(ExportConfig.ExportMode.FOUR_F_RIGHT_ENFORCED)
            }
            6 -> {
                ExportConfig.setFingersToCapture(listOf(ExportConfig.FingerID.THUMB_LEFT))
            }
            7 -> {
                ExportConfig.setFingersToCapture(ExportConfig.ExportMode.FOUR_F_LEFT_ENFORCED)
            }
            8 -> {
                ExportConfig.setFingersToCapture(ExportConfig.ExportMode.FOUR_F_LEFT_ENFORCED)
            }
            9 -> {
                ExportConfig.setFingersToCapture(ExportConfig.ExportMode.FOUR_F_LEFT_ENFORCED)
            }
            else -> {
                ExportConfig.setFingersToCapture(ExportConfig.ExportMode.FOUR_F_LEFT_ENFORCED)
            }
        }

        ExportConfig.setFormat(IBiometricFormats.TemplateFormat.FORMAT_JSON)
        ExportConfig.setCalculate_NFIQ(true)
        ExportConfig.setPackDebugInfo(false)
        ExportConfig.setPackExtraScale(true)
        ExportConfig.setPackAuditImage(true)
        ExportConfig.setUseLiveness(false)

        ExportConfig.setPack_raw(true)
        ExportConfig.getPack_raw()

        ExportConfig.getPack_wsq()
        ExportConfig.setPack_wsq(true)

        ExportConfig.setUseNistType4(false)
        ExportConfig.setWSQCompressionBitrate(5f)

        val exportIntent: Intent = VeridiumSDK.getSingleton()!!.export(FourFInterface.UID)
        startActivityForResult(exportIntent, REQUEST_EXPORT)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EXPORT) {
            val handler = object : IBiometricResultsHandler {
                override fun handleSuccess(res: MutableMap<String, Array<ByteArray>>?) {
                    validateBiometricPrint(res)
                }

                /*override fun handleSuccess(res: MutableList<ByteArray>?) {
                    validateBiometricPrint(res)
                    Log.d("Biometric sucess", "********************** SCANNER PASSED SUCCESSFULLY")
                }*/

                override fun handleFailure() {
                    Log.d("Biometric error", "Unknown failure issue")
                }

                override fun handleCancellation() {
                    Log.d("Biometric error", "Cancelled")
                }

                override fun handleError(p0: String?) {
                    Log.d("Biometric error", p0)
                }
            }

            BiometricResultsParser.parse(resultCode, data, handler)
        }
    }

    private fun navigateErrorFragment() {
        val action = BiometricFragmentDirections.actionBiometricFragmentToErrorFragment()
        findNavController().navigate(action)
    }

    private fun navigateSuccessFragment() {
        val action =
            BiometricFragmentDirections.actionBiometricFragmentToSuccessFragment()
        findNavController().navigate(action)
    }


    @SuppressLint("MissingPermission")
    fun validateBiometricPrint(print: MutableMap<String, Array<ByteArray>>?) {
        print?.let {
            showSpinner(activity)
            if (it.isNotEmpty()) {
                val result: JSONObject?

                try {
                    val arrayVeridiumResponse = it[FourFInterface.UID]!![1]

                    val tokener = JSONTokener(String(arrayVeridiumResponse))
                    result = JSONObject(tokener)

                    val scale085 = result.getJSONObject("SCALE085")
                    val fingerprints = scale085.getJSONArray("Fingerprints")
                    var fingerprintsPositionCode = fingerprints.getJSONObject(0)
                    var fingerPositionCode = fingerprintsPositionCode.getInt("FingerPositionCode")

                    var posicionBusqueda = 0

                    when (fingerPositionCode) {
                        7 -> {
                            when (fingers.left) {
                                7 -> posicionBusqueda = 0
                                8 -> posicionBusqueda = 1
                                9 -> posicionBusqueda = 2
                                10 -> posicionBusqueda = 3
                            }

                            fingerprintsPositionCode = fingerprints.getJSONObject(posicionBusqueda)
                            fingerPositionCode =
                                fingerprintsPositionCode.getInt("FingerPositionCode")

                        }
                        2 -> {
                            when (fingers.right) {
                                2 -> posicionBusqueda = 0
                                3 -> posicionBusqueda = 1
                                4 -> posicionBusqueda = 2
                                5 -> posicionBusqueda = 3
                            }

                            fingerprintsPositionCode = fingerprints.getJSONObject(posicionBusqueda)
                            fingerPositionCode =
                                fingerprintsPositionCode.getInt("FingerPositionCode")
                        }
                        else -> {
                            // Para el caso de los pulgares, por default coge el pulgar derecho
                            posicionBusqueda = 0
                            fingerprintsPositionCode = fingerprints.getJSONObject(posicionBusqueda)
                            fingerPositionCode =
                                fingerprintsPositionCode.getInt("FingerPositionCode")
                        }
                    }

                    val fingerImpressionImage =
                        fingerprintsPositionCode.getJSONObject("FingerImpressionImage")

                    val jsonInSolutions = HashMap<String, String>()

                    jsonInSolutions["dniCliente"] = UserPrefs.getUserDni(context!!)!!
                    jsonInSolutions["rawBase64"] =
                        fingerImpressionImage.getString("BinaryBase64ObjectRAW")
                    jsonInSolutions["rawHeight"] = fingerImpressionImage.getString("Height")
                    jsonInSolutions["rawWidth"] = fingerImpressionImage.getString("Width")
                    this.biometricViewModel.validateVeridiumFingerprints(jsonInSolutions)

                } catch (exception: Exception) {
                    exception.printStackTrace()
                    navigateErrorFragment()
                    Toast.makeText(context, "An error was thrown...", Toast.LENGTH_LONG).show()
                }
            }
        } ?: run {

        }
    }

    private fun listeners() {
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
                            message = "⚠️ El proceso de validación ha fallado, inténtelo nuevamente.",
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults[0] == -1) {
            if (EasyPermissions.somePermissionPermanentlyDenied(
                    activity!!,
                    permissions.toMutableList()
                )
            ) {
                AppSettingsDialog.Builder(this).build().show()
            }
        } else {
            initVeridium()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        initVeridium()
    }
}
