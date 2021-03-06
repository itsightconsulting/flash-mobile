package isdigital.veridium.flash.view


import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
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
    private var dialogSpin: Dialog? = null
    //private var iccid: String = ""
    private var success = false

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
        settingTipsText()
    }

    private fun settingTipsText() {
        txtTipsList.text = ""
        val tipsListText =
            resources.getString(R.string.sim_validation_tip_one)
                .split("|")
        val tipTextBold = tipsListText[0]
        val tipTextNB = tipsListText[1]

        val tipTextBold2 = tipsListText[2]
        val tipTextNB2 = tipsListText[3]

        val tipTextBold3 = tipsListText[4]
        val tipTextNB3 = tipsListText[5]

        val tipTextBold4 = tipsListText[6]
        val tipTextNB4 = tipsListText[7]

        var tipBold = SpannableString(tipTextBold)
        tipBold.setSpan(StyleSpan(Typeface.BOLD), 0, tipTextBold.length, 0)
        txtTipsList.append(tipBold)
        txtTipsList.append(tipTextNB)

        tipBold = SpannableString(tipTextBold2)
        tipBold.setSpan(StyleSpan(Typeface.BOLD), 0, tipTextBold2.length, 0)
        txtTipsList.append(tipBold)
        txtTipsList.append(tipTextNB2)

        tipBold = SpannableString(tipTextBold3)
        tipBold.setSpan(StyleSpan(Typeface.BOLD), 0, tipTextBold3.length, 0)
        txtTipsList.append(tipBold)
        txtTipsList.append(tipTextNB3)

        tipBold = SpannableString(tipTextBold4)
        tipBold.setSpan(StyleSpan(Typeface.BOLD), 0, tipTextBold2.length, 0)
        txtTipsList.append(tipBold)
        txtTipsList.append(tipTextNB4)
    }

    private fun viewModelInjections() {
        this.activationViewModel = ViewModelProviders.of(this).get(ActivationViewModel::class.java)
        this.biometricViewModel = ViewModelProviders.of(this).get(BiometricViewModel::class.java)
    }

    private fun eventListeners() {

        txtManualICCID.setOnClickListener {
            val action = SimCardFragmentDirections.actionSimCardFragmentToFormIccidNumberFragment()
            findNavController().navigate(action)
        }

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
                    val error = activationViewModel.loadError.value ?: false
                    val formError = activationViewModel.formError.value ?: false
                    val simActivated = activationViewModel.simActivated.value ?: false
                    dialogSpin?.dismiss()

                    if (error) {
                        Log.d("simActivated", simActivated.toString())
                        if (simActivated) {
                            ViewInfoSimCard()
                            return@let
                        } else {
                            UserPrefs.putUserBarscanAttempts(context)

                            val attemps = UserPrefs.getUserBarscanAttempts(context)
                            Log.d("attemps", attemps.toString())
                            if (this.activationViewModel.responseCode == "1020001001" || attemps == MAX_BAR_SCANNER_TEMPS) {

                                //val form = UserPrefs.getActivation(context)
                                //form.iccid = iccid

                                /*activationViewModel.sendFormWithStatus(
                                    PartnerData.formPreparation(
                                        form, passBarcode = false, passBiometric = false
                                    )
                                )
                                 */
                                this.activationViewModel.formError.value = true;
                                this.activationViewModel.loading.value = true;
                                activationViewModel.loadError.value = false
                            } else {
                                tryScanAgain()
                            }
                            return@let
                        }
                    }

                    if (formError) {
                        simcardError()
                    } else {
                        if (success) {
                            return@let
                        }
                        success = true

                        UserPrefs.resetUserBarscanAttempts(context)

                        val diagSucc = invokerBarcodeSuccess(context!!)
                        diagSucc.show()

                        diagSucc.findViewById<Button>(R.id.btnBarcodeSuccess).setOnClickListener {
                            success = false
                            //Important when user are in biometric fragment and want comeback to this view
                            this.activationViewModel.loading.value = false

                            diagSucc.dismiss()
                            dialog?.dismiss()
                            showSpinner(this.activity)
                            this.biometricViewModel.storeBestFingerprintsByDni(
                                UserPrefs.getUserDni(
                                    context!!
                                )!!
                            )
                        }
                    }
                }
            }

        })

        this.biometricViewModel.loading.observe(this, Observer { loading ->
            loading?.let {
                if (loading) {
                    if (!biometricViewModel.loadError.value!!) {
                        //Important when user are in biometric fragment and want comeback to this view
                        this.biometricViewModel.loading.value = false

                        UserPrefs.putIccid(context, this.activationViewModel.responseIccid)

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
        dialog?.setOnDismissListener {
            val action =
                SimCardFragmentDirections.actionSimCardFragmentToErrorIccIdFragment()
            findNavController().navigate(action)
        }
        dialog?.dismiss()
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

        /*val prefixText2 = SpannableString("\uD83D\uDC47 Ingresar ICCID manualmente")
        val prefixText2Len = prefixText2.length

        prefixText2.setSpan(
            CustomTypefaceSpan("", ResourcesCompat.getFont(context!!, R.font.gotham_bold)!!),
            0,
            prefixText2Len,
            0
        )

        prefixText2.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    context!!,
                    R.color.dark_purple
                )
            ), 0, prefixText2Len, 0
        )

        val tv2 = TextView(context)
        tv2.text = ""
        tv2.append(prefixText2)
        tv2.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tv2.gravity = Gravity.BOTTOM
        tv2.top
        tv2.setTextColor(ContextCompat.getColor(context!!, R.color.white))
        tv2.textSize = 18f

        val tvParams2 = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        tvParams2.setMargins(
            64,
            (context!!.resources.displayMetrics.heightPixels * 0.92).toInt(),
            64,
            0
        )

        tv2.layoutParams = tvParams2

        tv2.setOnClickListener {
            //invokerICCIDDialog(context!!).show()
            dialog?.dismiss()
            findNavController().navigate(R.id.formIccidNumberFragment)
        }

        dialog?.addContentView(
            tv2, tvParams2
        )*/
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
            if ((it.text.length == LENGTH_BAR_CODE || it.text.length == LENGTH_PUK_CODE) && validateOnlyNumber(
                    it.text
                )
            ) {
                evaluateIccid(it.text)
            } else {
                UserPrefs.putUserBarscanAttempts(context)

                val attemps = UserPrefs.getUserBarscanAttempts(context)
                Log.d("attemps", attemps.toString())
                if (this.activationViewModel.responseCode == "1020001001" || attemps == MAX_BAR_SCANNER_TEMPS) {

                    //val form = UserPrefs.getActivation(context)
                    //form.iccid = it.text
/*
                    activationViewModel.sendFormWithStatus(
                        PartnerData.formPreparation(
                            form, passBarcode = false, passBiometric = false
                        )
                    )
 */
                    this.activationViewModel.formError.value = true;
                    this.activationViewModel.loading.value = true;
                    activationViewModel.loadError.value = false
                } else {
                    tryScanAgain()
                }
            }
        }
    }

    private fun tryScanAgain() {
        val diagError = invokerBarcodeError(context!!)
        diagError.show()

        /*diagError.findViewById<Button>(R.id.btnBarcodeError).setOnClickListener {
            diagError.dismiss()
            mScannerView.resumeCameraPreview(this)
            mScannerView.setResultHandler(this)
            mScannerView.startCamera()
        }*/
        diagError.findViewById<Button>(R.id.btnBarcodeError).setOnClickListener {
            mScannerView.stopCamera()
            dialog?.dismiss()
            diagError.dismiss()
        }
    }

    private fun ViewInfoSimCard() {
        val diagError = invokerBarcodeErrorActivado(context!!)
        diagError.show()
        val openURL = Intent(Intent.ACTION_VIEW)
        diagError.findViewById<Button>(R.id.btnSolicitarPort).setOnClickListener {
            diagError.dismiss()
            openURL.data = Uri.parse(REQUEST_PORTABILITY)
            startActivity(openURL)
        }

        diagError.findViewById<Button>(R.id.btnVerEstado).setOnClickListener {
            diagError.dismiss()
            openURL.data = Uri.parse(VIEW_STATUS_SIM_CARD)
            startActivity(openURL)
        }

        diagError.findViewById<Button>(R.id.btnBarcodeError).setOnClickListener {
            UserPrefs.resetUserBarscanAttempts(context)
            diagError.dismiss()
            dialog?.setOnDismissListener {
                val action =
                    SimCardFragmentDirections.actionSimCardFragmentToPreActivationFragment()
                findNavController().navigate(action)
            }
            dialog?.dismiss()
        }
    }

    /*
        private fun invokerICCIDDialog(context: Context): Dialog {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.dialog_enter_iccid)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)

            dialog.window!!.setLayout(
                (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            val buttonDialog = dialog.findViewById(R.id.btnIccidDialog) as Button
            val txtIccid = dialog.findViewById(R.id.txtIccid) as TextInputLayout
            val etIccid = dialog.findViewById(R.id.etIccid) as TextInputEditText

            etIccid.doAfterTextChanged {
                if (etIccid.length() == 20) {
                    txtIccid.error = ""
                }
            }

            buttonDialog.setOnClickListener {
                val iccidLen = etIccid.length()
                if (iccidLen != 20) {
                    txtIccid.error = "Debe ingresar 20 dígitos"
                } else {
                    dialog.dismiss()
                    evaluateIccid(etIccid.text.toString())
                }
            }
            val btnCancelIccidDialog = dialog.findViewById(R.id.btnCancelIccidDialog) as Button
            btnCancelIccidDialog.setOnClickListener {
                dialog.dismiss()
            }
            return dialog
        }
    */
    private fun evaluateIccid(flIccid: String) {
        instanceDialogSpinner()
        val form = UserPrefs.getActivation(context)
        activationViewModel.checkIccidValid(
            flIccid, PartnerData.formPreparation(
                form, true, true
            )
        )
        //iccid = flIccid
        //UserPrefs.putIccid(context, iccid)
    }

    private fun instanceDialogSpinner() {
        dialogSpin = Dialog(context!!, R.style.dialog_scanner)
        dialogSpin?.let {
            it.setContentView(R.layout.alert_scanner_spin)
            it.setCanceledOnTouchOutside(false)
            it.setCancelable(false)
            it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.show()
        }
    }
}
