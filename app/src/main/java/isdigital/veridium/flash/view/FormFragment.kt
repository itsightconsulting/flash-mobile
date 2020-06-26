package isdigital.veridium.flash.view


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import isdigital.veridium.flash.R
import isdigital.veridium.flash.util.csSnackbar
import isdigital.veridium.flash.validator.MasterValidation
import kotlinx.android.synthetic.main.form_fragment.*
import java.util.*
import android.app.DatePickerDialog
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import isdigital.veridium.flash.FlashApplication
import isdigital.veridium.flash.model.pojo.ActivationPOJO
import isdigital.veridium.flash.preferences.UserPrefs
import isdigital.veridium.flash.util.CustomTypefaceSpan

/**
 * A simple [Fragment] subclass.
 */
class FormFragment : Fragment() {
    private lateinit var validatorMatrix: MasterValidation
    private lateinit var oActivation: ActivationPOJO

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.form_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Datos preview
//        etName.setText("Miranda")
//        etLastName.setText("Pedrosa")
//        etEmail.setText("mail@mail.com")
//        etDateOfBirth.setText("10/06/1980")
//        rbDoNotWantToPort.isChecked = true

        settingTermAcceptText();

        oActivation = UserPrefs.getActivation(FlashApplication.appContext)
        if (oActivation.name != "" && oActivation.paternalLastName != "") {
            etName.setText(oActivation.name)
            etPaternalLastName.setText(oActivation.paternalLastName)
            etMaternalLastName.setText(oActivation.maternalLastName)
            etDateOfBirth.setText(oActivation.birthDate)
            etEmail.setText(oActivation.email)
            etSponserTeamID.setText(oActivation.sponsorTeamId)
            etPopulatedCenter.setText(oActivation.populatedCenter)
            chkTermsLabel.isChecked = true;

            if (oActivation.wantPortability) rbWantToPort.isChecked = true
            else rbDoNotWantToPort.isChecked = true

            if (oActivation.coveragePopulatedCenter == "Yes") rbYesCoveragePopulatedCenter.isChecked =
                true
            else rbNoCoveragePopulatedCenter.isChecked = true

            if (oActivation.acceptTermsCoveragePopulatedCenter == "Yes") rbYesAcceptTermsCoveragePopulatedCenter.isChecked =
                true
            else rbNoAcceptTermsCoveragePopulatedCenter.isChecked = true

        }


        this.validatorMatrix = MasterValidation()
            .valid(etName, true)
            .required()
            .and()
            .valid(etPaternalLastName, true)
            .required()
            .and()
            .valid(etMaternalLastName, true)
            .required()
            .and()
            .valid(etPopulatedCenter, true)
            .required()
            .and()
            .valid(etDateOfBirth, true)
            .required("Campo obligatorio. Por favor, selecciona una fecha")
            .and()
            .valid(etEmail, true)
            .required()
            .maxLength(320)
            .email()
            .and()
            .valid(etSponserTeamID, true)
            .minLength(2)
            .maxLength(10)
            .validateNumber()
            .active()

        // Calendar
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        cal.add(Calendar.YEAR, -18)
        val minDate = cal.time

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        etDateOfBirth.setOnClickListener {
            buildDatePickerDialog(year, month, day, minDate)
        }

        btn_continue.setOnClickListener {
            clickListenerForBtnContinue()
        }



/*this.view?.let {
    invokerQuitDialog(context!!).show()
}*/
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun buildDatePickerDialog(year: Int, month: Int, day: Int, minDate: Date) {
        val dpd = DatePickerDialog(
            context!!,
            DatePickerDialog.OnDateSetListener { _, nYear, nMonth, nDayOfMonth ->
                var nDayOfMonthStr = nDayOfMonth.toString()
                var nMonthStr = (nMonth + 1).toString()
                if (nDayOfMonth < 10)
                    nDayOfMonthStr = "0$nDayOfMonth"

                if ((nMonth + 1) < 10)
                    nMonthStr = "0" + (nMonth + 1)
                etDateOfBirth.setText("$nDayOfMonthStr/$nMonthStr/$nYear")
            }, year, month, day
        )
        dpd.datePicker.maxDate = minDate.time
        dpd.show()
    }

    private fun settingTermAcceptText() {
        tvTermsLabel.text = ""
        val termsText = resources.getString(R.string.privacy_policy_personal_data_check_accept).split("|")
        val termsTextNb = termsText[0]
        val termsTextBold = termsText[1]
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
    }

    private fun getCoveragePopulatedCenter(): String {
        var estado: String = ""
        if (rbNoCoveragePopulatedCenter.isChecked) estado = "No"
        if (rbYesCoveragePopulatedCenter.isChecked) estado = "Yes"
        if (estado == "")
            txtCoveragePopulatedCenter.error = "   Por favor, selecciona una opción"
        else txtCoveragePopulatedCenter.error = ""
        return estado;
    }

    private fun getAcceptTermsCoveragePopulatedCenter(): String {
        var estado: String = ""
        if (rbNoAcceptTermsCoveragePopulatedCenter.isChecked) estado = "No"
        if (rbYesAcceptTermsCoveragePopulatedCenter.isChecked) estado = "Yes"
        if (estado == "")
            txtAcceptTermsCoveragePopulatedCenter.error = "   Por favor, selecciona una opción"
        else txtAcceptTermsCoveragePopulatedCenter.error = ""
        return estado;
    }

    private fun getwantPortability(): Boolean? {
        var estado: Boolean? = null
        if (rbDoNotWantToPort.isChecked) estado = false
        if (rbWantToPort.isChecked) estado = true
        if (estado == null)
            txtDoYouWantToPort.error = "   Por favor, selecciona una opción"
        else txtDoYouWantToPort.error = ""
        return estado;
    }

    private fun getTerms(): Boolean {
        var estado: Boolean = chkTermsLabel.isChecked
        if (estado)
            txtTermsLabel.error = ""
        else txtTermsLabel.error = "   Debes aceptar la Política de privacidad de datos personales"
        return estado;
    }

    private fun clickListenerForBtnContinue() {

        var wantPortability = getwantPortability();
        var coveragePopulatedCenter = getCoveragePopulatedCenter();
        var acceptTermsCoveragePopulatedCenter = getAcceptTermsCoveragePopulatedCenter();
        var Terms = getTerms();

        if (!this.validatorMatrix.checkValidity()
            || wantPortability == null
            || coveragePopulatedCenter == ""
            || acceptTermsCoveragePopulatedCenter == ""
            || Terms == false
        )
            this.view?.csSnackbar(
                "Debe completar los campos requeridos",
                Snackbar.LENGTH_LONG
            )
        else {
            // Save activation data
            saveActivationPojo(
                wantPortability,
                coveragePopulatedCenter,
                acceptTermsCoveragePopulatedCenter
            )

            var action = FormFragmentDirections.actionFormFragmentToFormConfirmFragment()
            if (wantPortability) action =
                FormFragmentDirections.actionFormFragmentToFormPhoneFragment()
            findNavController().navigate(action)
        }
    }

    private fun saveActivationPojo(
        wantPortability: Boolean,
        coveragePopulatedCenter: String,
        acceptTermsCoveragePopulatedCenter: String
    ) {

        val dni: String? = UserPrefs.getUserDni(FlashApplication.appContext)

        val activationPOJO = ActivationPOJO(
            dni!!,
            etName.text.toString().trim(),
            etPaternalLastName.text.toString().trim(),
            etMaternalLastName.text.toString().trim(),
            etDateOfBirth.text.toString(),
            etEmail.text.toString(),
            wantPortability,
            etSponserTeamID.text.toString().trim(),
            oActivation.phoneNumber,
            oActivation.currentCompany,
            oActivation.planType,
            etPopulatedCenter.text.toString().trim(),
            coveragePopulatedCenter,
            acceptTermsCoveragePopulatedCenter
        )
        UserPrefs.putActivation(FlashApplication.appContext, activationPOJO)
    }
}
