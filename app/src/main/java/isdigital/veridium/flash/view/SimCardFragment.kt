package isdigital.veridium.flash.view


import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.zxing.Result
import isdigital.veridium.flash.R
import isdigital.veridium.flash.preferences.UserPrefs
import isdigital.veridium.flash.util.*
import isdigital.veridium.flash.viewmodel.ActivationViewModel
import isdigital.veridium.flash.viewmodel.BiometricViewModel
import kotlinx.android.synthetic.main.sim_card_fragment.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class SimCardFragment : Fragment(), ZXingScannerView.ResultHandler,
    EasyPermissions.PermissionCallbacks {

    private val REQUEST_CAMERA_CAPTURE = 1
    private lateinit var mScannerView: ZXingScannerView
    private lateinit var activationViewModel: ActivationViewModel
    private lateinit var biometricViewModel: BiometricViewModel
    private var dialog: Dialog? = null
    private var iccid: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.sim_card_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelInjections()
        eventListeners()
    }

    private fun viewModelInjections() {
        this.activationViewModel = ViewModelProviders.of(this).get(ActivationViewModel::class.java)
        this.biometricViewModel = ViewModelProviders.of(this).get(BiometricViewModel::class.java)
    }

    private fun eventListeners() {
        imageButton.setOnClickListener {

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
        }

        activationViewModel.loading.observe(this, Observer { loading ->
            loading?.let {
                if (it) {
                    var error = activationViewModel.loadError.value ?: false
                    var formError = activationViewModel.formError.value ?: false

                    if (error) {
                        UserPrefs.putUserBarscanAttempts(context)

                        val attemps = UserPrefs.getUserBarscanAttempts(context)
                        if (attemps == MAX_SCANNER_TEMPS) {
                            val form = UserPrefs.getActivation(context)
                            form.formStatus = FORMSTATUS.REJECTICCD.value
                            form.formCreationDate = SimpleDateFormat(
                                resources.getString(R.string.datetime_format),
                                Locale.getDefault()
                            ).format(Date())
                            form.validationBiometric = false
                            form.iccid = iccid
                            form.birthDate =
                                changeDateFormat(form.birthDate, "yyyy-MM-dd", "dd/MM/yyyy")

                            activationViewModel.sendFormWithStatus(form)
                            activationViewModel.loadError.value = false

                        } else {
                            tryScanAgain()
                        }
                        return@let
                    }

                    if (formError) {
                        simcardError()
                    } else {
                        UserPrefs.resetUserBarscanAttempts(context)

                        val diagSucc = invokerBarcodeSuccess(context!!)
                        diagSucc.show()

                        diagSucc.findViewById<Button>(R.id.btnBarcodeSuccess).setOnClickListener {
                            diagSucc.dismiss()
                            dialog?.dismiss()
                            showSpinner(this.activity)
                            this.biometricViewModel.storeBestFingerprintsByDni(UserPrefs.getUserDni(context!!)!!)
                        }
                    }
                }
            }

        })

        this.biometricViewModel.loading.observe(this, Observer { loading ->
            loading?.let {
                if (loading) {
                    if (!biometricViewModel.loadError.value!!) {
                        hideSpinner(this.activity)

                        val action =
                            SimCardFragmentDirections.actionSimCardFragmentToBiometricFragment()
                        findNavController().navigate(action)
                    } else {
                        hideSpinner(this.activity)
                        simcardError()
                    }
                }
            }
        })
    }

    private fun simcardError() {
        UserPrefs.resetUserBarscanAttempts(context)
        dialog?.dismiss()
        val action =
            SimCardFragmentDirections.actionSimCardFragmentToErrorFragment()
        findNavController().navigate(action)
    }

    private fun initBarcodeScanner() {
        mScannerView = ZXingScannerView(context)
        dialog = Dialog(context!!, R.style.dialog_scanner)

        dialog?.setContentView(mScannerView)
        dialog?.show()

        val prefixText = SpannableString("Enfoque el código de barras\n de su chip \uD83D\uDCB3")
        val prefixTextLen = prefixText.length

        prefixText.setSpan(
            CustomTypefaceSpan("", ResourcesCompat.getFont(context!!, R.font.gotham_bold)!!),
            0,
            prefixTextLen,
            0
        )

        prefixText.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    context!!,
                    R.color.sand
                )
            ), 0, prefixTextLen, 0
        )

        val tv = TextView(context)
        tv.text = ""
        tv.append(prefixText)
        tv.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tv.gravity = Gravity.BOTTOM
        tv.top
        tv.setTextColor(ContextCompat.getColor(context!!, R.color.white))
        tv.textSize = 18f
        val tvParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        tvParams.setMargins(
            96,
            0,
            96,
            (context!!.resources.displayMetrics.heightPixels * 0.25).toInt()
        )
        tv.layoutParams = tvParams
        dialog?.addContentView(
            tv, tvParams
        )

        mScannerView.setResultHandler(this)
        mScannerView.startCamera()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            if (EasyPermissions.hasPermissions(context!!, Manifest.permission.CAMERA)) {
                initBarcodeScanner()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
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
            initBarcodeScanner()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        initBarcodeScanner()
    }

    override fun handleResult(rawResult: Result?) {

        mScannerView.stopCamera()

        rawResult?.let {
            if (it.text.length == 20 && validateOnlyNumber(it.text)) {
                activationViewModel.checkIccidValid(it.text)
                iccid = it.text
            } else {
                tryScanAgain()
            }
        }
    }

    private fun tryScanAgain() {
        val diagError = invokerBarcodeError(context!!)
        diagError.show()

        diagError.findViewById<Button>(R.id.btnBarcodeError).setOnClickListener {
            diagError.dismiss()
            mScannerView.resumeCameraPreview(this)
            mScannerView.setResultHandler(this)
            mScannerView.startCamera()
        }
    }
}
