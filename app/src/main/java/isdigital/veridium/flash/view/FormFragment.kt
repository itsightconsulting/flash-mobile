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
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import isdigital.veridium.flash.FlashApplication
import isdigital.veridium.flash.model.pojo.ActivationPOJO
import isdigital.veridium.flash.preferences.UserPrefs
import isdigital.veridium.flash.util.CustomTypefaceSpan
import isdigital.veridium.flash.util.invokerTermContent

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
            .required()
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
            if (etDateOfBirth.text.isNullOrEmpty())
                buildDatePickerDialog(year, month, day, minDate)
            else {
//11/06/1998
                val fechaSelect = etDateOfBirth.text!!.split("/");
                val s_day = fechaSelect[0].toInt()
                val s_month = fechaSelect[1].toInt() - 1
                val s_year = fechaSelect[2].toInt()
                buildDatePickerDialog(s_year, s_month, s_day, minDate)
            }

        }

        btn_continue.setOnClickListener {
            clickListenerForBtnContinue()
        }

        tvTermsLabel.setOnClickListener {
            clickListenerFortvTermsLabel()
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
        val termsText =
            resources.getString(R.string.privacy_policy_personal_data_check_accept).split("|")
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
            txtCoveragePopulatedCenter.error = "Por favor, selecciona una opción"
        else txtCoveragePopulatedCenter.error = ""
        return estado;
    }

    private fun getAcceptTermsCoveragePopulatedCenter(): String {
        var estado: String = ""
        if (rbNoAcceptTermsCoveragePopulatedCenter.isChecked) estado = "No"
        if (rbYesAcceptTermsCoveragePopulatedCenter.isChecked) estado = "Yes"
        if (estado == "")
            txtAcceptTermsCoveragePopulatedCenter.error = "Por favor, selecciona una opción"
        else txtAcceptTermsCoveragePopulatedCenter.error = ""
        return estado;
    }

    private fun getWantPortability(): Boolean? {
        var estado: Boolean? = null
        if (rbDoNotWantToPort.isChecked) estado = false
        if (rbWantToPort.isChecked) estado = true
        if (estado == null)
            txtDoYouWantToPort.error = "Por favor, selecciona una opción"
        else txtDoYouWantToPort.error = ""
        return estado;
    }

    private fun getTerms(): Boolean {
        var estado: Boolean = chkTermsLabel.isChecked
        if (estado)
            txtTermsLabel.error = ""
        else txtTermsLabel.error = "Debes aceptar la Política de privacidad de datos personales"
        return estado;
    }

    private fun clickListenerFortvTermsLabel() {
        val diagSucc = invokerTermContent(context!!)
        var txtTerms = diagSucc.findViewById<TextView>(R.id.txtTerms)
        settingFullTermAndCoditionsText(txtTerms);
        diagSucc.show()
        diagSucc.findViewById<Button>(R.id.btnClose).setOnClickListener {
            diagSucc.dismiss()
        }
    }

    private fun settingFullTermAndCoditionsText(txtTerms: TextView) {

        txtTerms.text = ""

        txtTerms.append(
            "\nFLASH Servicios Perú S.R.L. (“FLASH”) se compromete a observar los principios de legalidad, consentimiento, información, calidad, " +
                    "propósito, proporcionalidad y responsabilidad establecidos por la Ley 29733 Ley de Protección de Datos Personales y su Reglamento aprobado por " +
                    "el Decreto 003-2013-JUS. En este sentido, FLASH es responsable del tratamiento de la información personal “Información Personal” que ha recopilado " +
                    "o que recopilará en el futuro y del uso de dicha información y su debida protección. Los términos en mayúscula utilizados pero no definidos en este " +
                    "Aviso de Privacidad se definen como se especifica en el Contrato. Usted (“Usuario”) por el presente reconoce y acepta que FLASH es el propietario de " +
                    "la base de datos en la cual se almacena toda su información Personal. FLASH se compromete a proteger la privacidad de la Información Personal de sus " +
                    "Usuarios. FLASH protege la Información Personal al mantener estrictas garantías físicas, electrónicas y de procedimiento que cumplen o exceden las " +
                    "leyes y regulaciones aplicables. Por el presente, el Usuario reconoce y acepta que FLASH puede compartir la Información Personal de los Usuarios " +
                    "con compañías relacionadas con FLASH dentro y fuera del país (flujo transfronterizo de datos personales), de acuerdo con la legislación vigente, " +
                    "para los fines relacionados con los negocios de FLASH, de acuerdo a lo establecido en este Aviso de Privacidad. En este contexto. FLASH informa " +
                    "al Usuario sobre los propósitos del tratamiento que se le dará a su Información Personal:\n\n"
        )

        txtTerms.append(
            "1. La Información Personal proporcionada por el Usuario a FLASH será materia de tratamiento para los siguientes propósitos: (i) " +
                    "para proporcionar servicios al Usuario; (ii) gestionar las compras de productos y servicios de FLASH por parte del Usuario; (iii) " +
                    "para fines comerciales, incluida la entrega de productos y servicios; (iv) para fines fiscales y de facturación; (v) " +
                    "para cumplir con las obligaciones de FLASH con el Usuario; (vi) para fines de control interno, incluyendo el suministro de " +
                    "productos y servicios al Usuario; y (vii) para fines estadísticos, entre otros.\n\n"
        )
        txtTerms.append(
            "2. Para los fines antes mencionados, FLASH puede recopilar Información Personal como nombre, teléfono, " +
                    "dirección, correo electrónico, fecha de nacimiento, número de identificación, estado civil, nacionalidad, " +
                    "sexo, edad, tarjeta de crédito e información de cuenta bancaria, dirección IP, así como otra información " +
                    "proporcionada durante el plazo de la relación contractual con FLASH. Flash incluirá dicha información en la " +
                    "base de datos de los Usuarios de FLASH, de acuerdo con los requisitos legales.\n\n\n"
        )
        txtTerms.append(
            "3. El Usuario acepta y autoriza expresamente a FLASH a transferir su Información Personal a aquellos terceros que deben " +
                    "obtener su Información Personal con el fin de procesar pagos, completando las compras de productos y servicios del Usuario " +
                    "y gestionando la participación del Usuario dentro del sistema FLASH.\n\n\n"
        )
        txtTerms.append(
            "4. Cada Usuario reconoce que FLASH puede compartir su Información Personal, incluyendo: nombre, correo electrónico, " +
                    "número de teléfono y dirección física y otra Información Personal, con otros terceros. Uno de los propósitos principales " +
                    "para compartir esta información es mejorar la efectividad de la herramienta de administración de contactos que está disponible " +
                    "a través del Back Office de FLASH. Además, FLASH puede compartir la Información Personal del Usuario con empresas relacionadas " +
                    "en Perú y en el extranjero (flujo transfronterizo de datos personales) para los fines establecidos en este documento.\n\n"
        )
        txtTerms.append(
            "5. El Usuario reconoce y acepta que FLASH tratará la Información Personal, de acuerdo con los términos y condiciones " +
                    "descritos en este Aviso de Privacidad al continuar el proceso de contratación para convertirse en un Usuario de FLASH. " +
                    "Además, al continuar con el proceso de contratación, el Usuario acepta los propósitos del tratamiento de la Información " +
                    "Personal, en los términos y condiciones establecidas en el presente Documento.\n\n"
        )
        txtTerms.append("6. El Usuario deberá contactar en www.flashperu.pe para más detalles sobre las prácticas de privacidad de FLASH.\n\n")
        txtTerms.append(
            "7. El Usuario puede revocar el consentimiento otorgado a FLASH para el procesamiento de la Información Personal " +
                    "mediante una solicitud por escrito enviada a Avenida Primavera 517, Oficina 405, San Borja, Lima. Además, el " +
                    "Usuario tendrá derecho a (i) tener acceso a la Información Personal del Usuario en posesión de FLASH y los " +
                    "detalles de su procesamiento; (ii) actualizar o rectificar dicha Información Personal cuando sea inexacta o " +
                    "incompleta; (iii) exigir la cancelación, eliminación o destrucción de su Información Personal y (iv) oponerse " +
                    "al procesamiento de su Información Personal para fines específicos o porque existe una causa legítima para ello. " +
                    "El Usuario puede ejercer los derechos de acceso, rectificación, cancelación y oposición mediante solicitud por " +
                    "escrito con la siguiente información: nombre y dirección, documentos que prueben la identidad del Usuario o la " +
                    "representación legal del Usuario; una descripción clara y precisa de la Información Personal sobre la que busca " +
                    "ejercer cualquiera de estos derechos y de la solicitud. Esta solicitud debe enviarse a las oficinas de FLASH, ubicadas en " +
                    "Avenida Primavera 517, Oficina 405, San Borja, Lima. FLASH responderá su solicitud dentro del período máximo permitido por la ley. " +
                    "No obstante lo anterior, el Usuario reconoce y acepta que FLASH puede retener cierta información del Usuario que solicita la cancelación, " +
                    "eliminación o destrucción de la Información personal para los fines permitidos por la ley aplicable, de conformidad con el  artículo 69 " +
                    "del Reglamento de la Ley de Protección de Datos , aprobado por Decreto Supremo 003-2013-JUS.\n\n"
        )
        txtTerms.append(
            "8. FLASH se reserva el derecho de actualizar y / o modificar los términos de este Aviso de Privacidad. " +
                    "En caso que los cambios sean sobre una modificación o actualización esencial, FLASH comunicará esta situación " +
                    "por adelantado por correo electrónico al Usuario.\n\n\n"
        )
        txtTerms.append("9. La información general de FLASH en Perú es la siguiente: Flash Services Perú S.R.L. www.flashperu.pe\n\n")
        txtTerms.append(
            "10. El Usuario declara haber leído y aceptado este Aviso de Privacidad, de forma expresa y gratuita. " +
                    "En ese sentido, el Usuario acepta y autoriza a Flash al tratamiento de su Información Personal.\n\n"
        )
    }

    private fun clickListenerForBtnContinue() {

        var wantPortability = getWantPortability();
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
