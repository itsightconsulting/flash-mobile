package isdigital.veridium.flash.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar

import isdigital.veridium.flash.R
import isdigital.veridium.flash.preferences.UserPrefs
import isdigital.veridium.flash.util.*
import isdigital.veridium.flash.validator.MasterValidation
import isdigital.veridium.flash.viewmodel.ActivationViewModel
import isdigital.veridium.flash.viewmodel.BiometricViewModel
import kotlinx.android.synthetic.main.form_iccid_number_fragment.*

/**
 * A simple [Fragment] subclass.
 */
class FormIccidNumberFragment : Fragment() {
    private lateinit var validatorMatrix: MasterValidation
    private lateinit var activationViewModel: ActivationViewModel
    private lateinit var biometricViewModel: BiometricViewModel
    //private var iccid: String = ""
    private var success = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.form_iccid_number_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelInjections()
        eventListeners()
        settingBoldText()

        val PukLenMessage = resources.getString(R.string.iccid_length)
        this.validatorMatrix = MasterValidation()
            .valid(etICCIDNumber, true)
            .required()
            .minLength(LENGTH_PUK_CODE, PukLenMessage)
            .maxLength(LENGTH_PUK_CODE, PukLenMessage).active()

        btnValidateIccId.setOnClickListener {
            if (!this.validatorMatrix.checkValidity()) {
                this.view?.csSnackbar(
                    "Debe completar los campos requeridos",
                    Snackbar.LENGTH_LONG
                )
            } else {
                showSpinner(this.activity)
                evaluateIccid(etICCIDNumber.text.toString())
            }
        }

    }

    private fun viewModelInjections() {
        this.activationViewModel = ViewModelProviders.of(this).get(ActivationViewModel::class.java)
        this.biometricViewModel = ViewModelProviders.of(this).get(BiometricViewModel::class.java)
    }

    private fun eventListeners() {
        activationViewModel.loading.observe(this, Observer { loading ->
            loading?.let {
                if (it) {
                    val error = activationViewModel.loadError.value ?: false
                    val formError = activationViewModel.formError.value ?: false
                    val simActivated = activationViewModel.simActivated.value ?: false
                    //error = false;
                    //formError = false;
                    if (error) {
                        if (simActivated) {
                            ViewInfoSimCard()
                            return@let
                        } else {
                            UserPrefs.putUserBarscanAttempts(context)

                            val attemps = UserPrefs.getUserBarscanAttempts(context)
                            Log.d("attemps", attemps.toString())
                            if (this.activationViewModel.responseCode == "1020001001" || attemps == MAX_BAR_SCANNER_TEMPS) {
                                // attemps == MAX_BAR_SCANNER_TEMPS) {

                                //val form = UserPrefs.getActivation(context)
                                //form.iccid = iccid

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
                                tryPutAgain()
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
                            showSpinner(this.activity)
                            this.biometricViewModel.storeBestFingerprintsByDni(
                                UserPrefs.getUserDni(
                                    context!!
                                )!!
                            )
                        }
                        //findNavController().navigate(R.id.successFragment)
                    }
                }
            }

        })
        biometricViewModel.loading.observe(this, Observer { loading ->
            loading?.let {
                if (loading) {
                    if (!biometricViewModel.loadError.value!!) {
                        //Important when user are in biometric fragment and want comeback to this view
                        this.biometricViewModel.loading.value = false
                        UserPrefs.putIccid(context, this.activationViewModel.responseIccid)
                        hideSpinner(this.activity)

                        val action =
                            FormIccidNumberFragmentDirections.actionFormIccidNumberFragmentToBiometricFragment()
                        findNavController().navigate(action)
                    } else {
                        hideSpinner(this.activity)
                        simcardError()
                    }
                }
            }
        })
    }

    private fun evaluateIccid(flPUK: String) {
        //val flIccid = ICCID.replace("{0}", flPUK)
        val form = UserPrefs.getActivation(context)
        activationViewModel.checkIccidValid(
            flPUK, PartnerData.formPreparation(
                form, true, true
            )
        )
        //iccid = flPUK
        //UserPrefs.putIccid(context, iccid)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun tryPutAgain() {
        val diagError = errorBarCodeValidation(context!!) // invokerBarcodeError(context!!)
        diagError.show()

        diagError.findViewById<Button>(R.id.btnBarcodeError).setOnClickListener {
            //UserPrefs.resetUserBarscanAttempts(context)
            diagError.setOnDismissListener {
                val action =
                    FormIccidNumberFragmentDirections.actionFormIccidNumberFragmentToSimCardFragment();
                findNavController().navigate(action)
            }
            diagError.dismiss()
        }
        hideSpinner(this.activity)
    }

    private fun ViewInfoSimCard() {
        val diagError = invokerBarcodeErrorActivado(context!!)
        diagError.show()
        val openURL = Intent(Intent.ACTION_VIEW)
        UserPrefs.resetUserBarscanAttempts(context)

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
            diagError.dismiss()
            val action =
                FormIccidNumberFragmentDirections.actionFormIccidNumberFragmentToPreActivationFragment()
            findNavController().navigate(action)
        }
        hideSpinner(this.activity)
    }

    private fun simcardError() {
        hideSpinner(this.activity)
        UserPrefs.resetUserBarscanAttempts(context)
        val action =
            FormIccidNumberFragmentDirections.actionFormIccidNumberFragmentToErrorIccIdFragment()
        findNavController().navigate(action)
    }

    private fun settingBoldText() {
        textView3.text = ""
        val termsText = resources.getString(R.string.iccid_extra_information).split("|")
        val termsTextBold = termsText[0]
        val termsTextNb = termsText[1]
        val prefixText = SpannableString(termsTextBold)
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
                    R.color.black
                )
            ), 0, prefixTextLen, 0
        )
        textView3.append(prefixText)
        textView3.append(termsTextNb)
    }
}
