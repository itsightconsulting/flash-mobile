package pe.mobile.cuy.view


import android.content.res.Resources
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar

import pe.mobile.cuy.R
import pe.mobile.cuy.util.csSnackbar
import pe.mobile.cuy.validator.MasterValidation
import kotlinx.android.synthetic.main.form_phone_fragment.*
import pe.mobile.cuy.FlashApplication
import pe.mobile.cuy.model.pojo.ActivationPOJO
import pe.mobile.cuy.preferences.UserPrefs

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

        this.validatorMatrix =
            MasterValidation()
                .valid(etPhoneNumber, true, ::checkPortabilityArtificial)
                .required()
                .minLength(9)
                .maxLength(9)
                .validateNumber()
                .and()
                .valid(etConfirmPhoneNumber, true, ::checkPortabilityArtificial)
                .equalsTo(etPhoneNumber)
                .and()
                .valid(acCurrentCompany)
                .required()
                .and()
                .valid(acPlanType)
                .required()
                .active()

        val OperadorList = arrayOf("Bitel", "Claro", "Cuy", "Entel", "Movistar")
        //"Bitel|Claro|Cuy|Entel|Movistar".split('|')
        val TipoPlanList = arrayOf("Postpago", "Prepago")
        setAdapterToElement(OperadorList, acCurrentCompany)
        setAdapterToElement(TipoPlanList, acPlanType)

        oActivation = UserPrefs.getActivation(FlashApplication.appContext)
        if (oActivation.currentCompany != "" && oActivation.planType != "") {
            etPhoneNumber.setText(oActivation.phoneNumber)
            acCurrentCompany.setText(oActivation.currentCompany)
            acPlanType.setText(oActivation.planType)
        }

        btn_continue.setOnClickListener {
            ClickListener_for_btnContinue()
        }
    }

    fun checkPortabilityArtificial(): Boolean {
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun setAdapterToElement(listado: Array<String>, elemento: AutoCompleteTextView) {
        val adapter = ArrayAdapter(
            context!!,
            R.layout.dropdown_menu_popup_item,
            listado
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        elemento.setAdapter(adapter)
    }

    fun ClickListener_for_btnContinue() {

        if (this.validatorMatrix.checkValidity()) {
            if (oActivation == null) throw  Resources.NotFoundException()

            val ActivationPOJO = ActivationPOJO(
                oActivation.dni,
                oActivation.name,
                oActivation.lastName,
                oActivation.birthDate,
                oActivation.email,
                oActivation.wantToPortability,
                oActivation.sponsorTeamId,
                etPhoneNumber.text.toString(),
                acCurrentCompany.text.toString(),
                acPlanType.text.toString()
            )
            UserPrefs.putActivation(FlashApplication.appContext, ActivationPOJO)

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
