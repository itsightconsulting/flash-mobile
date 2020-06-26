package isdigital.veridium.flash.view


import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar

import isdigital.veridium.flash.R
import isdigital.veridium.flash.util.csSnackbar
import isdigital.veridium.flash.validator.MasterValidation
import kotlinx.android.synthetic.main.form_phone_fragment.*
import isdigital.veridium.flash.FlashApplication
import isdigital.veridium.flash.model.pojo.ActivationPOJO
import isdigital.veridium.flash.preferences.UserPrefs
import isdigital.veridium.flash.util.CustomTypefaceSpan
import isdigital.veridium.flash.util.PLAN_TYPES

/**
 * A simple [Fragment] subclass.
 */
class FormPhoneFragment : Fragment() {

    private lateinit var validatorMatrix: MasterValidation
    private lateinit var oActivation: ActivationPOJO
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.form_phone_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingTermAcceptText()
        this.validatorMatrix =
            MasterValidation()
                .valid(etPhoneNumber, true, ::checkPortabilityArtificial)
                .required()
                .startWith("9", "El número telefónico debe empezar con 9")
                .minLength(9, "El número telefónico debe contener 9 dígitos")
                .maxLength(9, "El número telefónico debe contener 9 dígitos")
                .validateNumber()
                .and()
                .valid(etConfirmPhoneNumber, true, ::checkPortabilityArtificial)
                .equalsTo(etPhoneNumber, "Los números no coinciden. Revísalos e intenta de nuevo")
                .and()
                .valid(acCurrentCompany)
                .required("Campo obligatorio. Por favor, selecciona un operador")
                .and()
                .valid(acPlanType)
                .required("Campo obligatorio. Por favor selecciona un tipo de plan")
                .active()

        val operadorList = arrayOf("Bitel", "Claro", "Cuy", "Entel", "Movistar")
        val tipoPlanList = arrayOf(PLAN_TYPES.POSTPAGO.value, PLAN_TYPES.PREPAGO.value)

        oActivation = UserPrefs.getActivation(FlashApplication.appContext)
        if (oActivation.currentCompany != "" && oActivation.planType != "") {
            etPhoneNumber.setText(oActivation.phoneNumber)
            etConfirmPhoneNumber.setText(oActivation.phoneNumber)
            acCurrentCompany.setText(oActivation.currentCompany)
            acPlanType.setText(oActivation.planType)
            chkTermsLabel.isChecked = true;
        }

        setAdapterToElement(operadorList, acCurrentCompany)
        setAdapterToElement(tipoPlanList, acPlanType)

        btn_continue.setOnClickListener {
            clickListenerForBtnContinue()
        }
    }

    private fun checkPortabilityArtificial(): Boolean {
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setAdapterToElement(listado: Array<String>, elemento: AutoCompleteTextView) {
        val adapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_menu_popup_item,
            listado
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        elemento.setAdapter(adapter)
    }

    private fun settingTermAcceptText() {
        tvTermsLabel.text = ""
        val termsText =
            resources.getString(R.string.conditions_applicable_to_portability_check_accept)
                .split("|")
        val termsTextNb = termsText[0]
        val termsTextBold = termsText[1]
        val termsTextNb2 = termsText[2]
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
                    R.color.dark_purple
                )
            ), 0, prefixTextLen, 0
        )
        tvTermsLabel.append(termsTextNb)
        tvTermsLabel.append(prefixText)
        tvTermsLabel.append(termsTextNb2)
    }

    private fun getTerms(): Boolean {
        var estado: Boolean = chkTermsLabel.isChecked
        if (estado)
            txtTermsLabel.error = ""
        else txtTermsLabel.error = "   Debes aceptar las condiciones aplicables a la portabilidad"
        return estado;
    }

    private fun clickListenerForBtnContinue() {
        var Terms = getTerms();

        if (this.validatorMatrix.checkValidity()
            || Terms == false
        ) {

            val activationPOJO = ActivationPOJO(
                oActivation.dni,
                oActivation.name,
                oActivation.paternalLastName,
                oActivation.maternalLastName,
                oActivation.birthDate,
                oActivation.email,
                oActivation.wantPortability,
                oActivation.sponsorTeamId,
                etPhoneNumber.text.toString().trim(),
                acCurrentCompany.text.toString().trim(),
                acPlanType.text.toString().trim(),
                oActivation.populatedCenter,
                oActivation.coveragePopulatedCenter,
                oActivation.acceptTermsCoveragePopulatedCenter
            )
            UserPrefs.putActivation(FlashApplication.appContext, activationPOJO)

            val action =
                FormPhoneFragmentDirections.actionFormPhoneFragmentToFormConfirmFragment()
            findNavController().navigate(action)
        } else
            this.view?.csSnackbar(
                "Debe completar los campos requeridos",
                Snackbar.LENGTH_LONG
            )
    }
}