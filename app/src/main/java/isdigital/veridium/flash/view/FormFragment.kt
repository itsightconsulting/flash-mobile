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

        var subT1 = SpannableString("\nPolítica de privacidad de datos personales\n\n")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)

        subT1 = SpannableString("CONDICIONES GENERALES DEL SERVICIO EN MODALIDAD PREPAGO\n")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append("Este contrato lo celebramos FLASH SERVICIOS PERÚ S.R.L., con RUC 20604775834 con domicilio en Av. Primavera 517, oficina 405, distrito de San Borja, a través de la marca FLASH MOBILE (“Flash”) y usted.\n\n")

        subT1 = SpannableString("EL SERVICIO\n")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append("Nos comprometemos a prestarte los servicios que elijas de telefonía móvil, mensajes de texto (SMS) y/o internet en modalidad prepago. Los servicios son ofrecidos bajo la modalidad de recargas; de manera que, podrás acceder a los servicios previo pago de la tarifa correspondiente al plan de la recarga que elijas de acuerdo a tus necesidades.\n\n")

        subT1 = SpannableString("PLAZO\n")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append("Indeterminado.\n\n")

        subT1 = SpannableString("CONDICIONES Y LIMITACIONES DEL SERVICIO\n")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append("La calidad de los servicios contratados depende de: (i) la ubicación (estructura y altura del lugar donde utilizas el servicio; por ejemplo, ascensores, sótanos, túneles, cerros), (ii) las características técnicas, configuración y capacidades del equipo que tengas, (iii) la capacidad contratada, (iv) el uso de aplicaciones “peer to peer” (P2P) o aplicaciones similares, (v) volumen de tráfico y congestión de red; y, (vi) cualquiera causa externa (clima, sismo, hechos de terceros, etc).\n")
        txtTerms.append("Para conocer las áreas de cobertura entra a www.flashmobile.pe\n\n")

        subT1 = SpannableString("TARIFAS\n")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append("En el siguiente enlace www.flashmobile.pe/planes encontrarás la siguiente información sobre las tarifas vigentes. A continuación, detallamos las velocidades de Internet ofrecidas, de acuerdo a la cobertura y tecnología:\n\n")
        txtTerms.append("- Cobertura y Tecnología\n")
        txtTerms.append("\t + 2G\n")
        txtTerms.append("\t + 3G\n")
        txtTerms.append("\t + 4G\n")
        txtTerms.append("\n")
        txtTerms.append("- Velocidad contratada (baja/subida)\n")
        txtTerms.append("\t + 0.013Mbps / 0.013 Mbps\n")
        txtTerms.append("\t + 1Mbps / 0.1 Mbps\n")
        txtTerms.append("\t + 5 Mbps / 1 Mbps\n")
        txtTerms.append("\n")
        txtTerms.append("- Velocidad mínima garantizada (bajada/subida)\n")
        txtTerms.append("\t + 0.005Mbps / 0.005 Mbps\n")
        txtTerms.append("\t + 0.4 Mbps / 0.04 Mbps\n")
        txtTerms.append("\t + 2Mbps / 0.4 Mbps\n")
        txtTerms.append("\n")
        txtTerms.append(
            "Costo por minutos en llamadas por demanda: S/ 0.49 " +
                    "La información personal puede incluir, entre otros, información de identificación (como nombre, número de tarjeta de identificación y edad), " +
                    "información de contacto (número de teléfono, dirección de correo electrónico y domicilio) e información financiera. " +
                    "Flash podrá realizar el Tratamiento a través de sus trabajadores, consultores, asesores y/o terceros encargados para tal efecto. " +
                    "El abonado declara que ha sido informado sobre sus derechos como titular de los Datos Personales, " +
                    "entre los que se encuentran los derechos de acceso, actualización, inclusión, rectificación, cancelación y oposición. " +
                    "Cualquier consulta o reclamo en relación con el Tratamiento de los Datos Personales del Abonado podrá dirigirse a la siguiente dirección de correo electrónico soporte.clientes@flashmobile.pe.\n\n"
        )

        subT1 = SpannableString("SERVICIO FUERA DEL PAÍS (ROAMING)\n")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append("Inicialmente no tendremos este servicio disponible en la República del Perú\n\n")

        subT1 = SpannableString("TRANSFERENCIA DE SALDO\n")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append("La vigencia de las recargas es de mínimo sesenta (60) días.  Si durante la vigencia no consumes todo el valor de tu recarga, el saldo se sumará automáticamente a cualquier nueva recarga que hagas dentro de los treinta (30) días siguientes.\n")
        txtTerms.append("Si no haces una recarga dentro de los treinta (30) días siguientes a que se venza tu recarga, el saldo que no hayas consumido se perderá, salvo que te hayamos ofrecido recargas con vigencia superior o indeﬁnida.\n\n")

        subT1 = SpannableString("REEMBOLSO TARJETAS DE CRÉDITO/ DÉBITO\n")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append("Para la solicitud de reembolso de transacciones realizadas con tarjeta de crédito o tarjeta débito, deberás solicitar el reembolso directamente por el chat de Servicio al Cliente o por correo electrónico al buzón de correo: soporte.clientes@flashmobile.pe argumentando su interés en un reembolso y adjuntando la certiﬁcación bancaria de la cuenta de ahorros o cuenta corriente del propietario de la tarjeta con la que se realizó la transacción.\n\n")

        subT1 = SpannableString("USO INDEBIDO DEL SERVICIO\n")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append("El uso de los servicios es estrictamente personal e intransferible. En caso Flash detectara un uso indebido del Servicio, seguirá el procedimiento establecido en la Resolución de Consejo Directivo N° 060-2006-CD/OSIPTEL que aprueba la “Norma que establece el Procedimiento que aplicaran las empresas operadoras para la suspensión cautelar y el corte definitivo por uso indebido de los servicios públicos de telecomunicaciones”, o la normativa que lo sustituya.\n")
        txtTerms.append("De manera enunciativa y no limitativa, las siguientes conductas se considerarán como usos indebidos del servicio: (i) comercializar el servicio sin autorización; (ii) utilizar herramientas de hardware o software para realizar llamadas masivas o envío masivo de mensajes de texto; entre otros. Para mayor detalle, visitar www.flashmobile.pe\n\n")

        subT1 = SpannableString("DESACTIVACIÓN O BAJA DEL SERVICIO POR NO USO\n")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append("Cuando no hagas uso del servicio (no tengas saldos acumulados, no generes comunicaciones, ni actives tarjetas prepago o recargas), podremos desactivar tu línea, previo aviso de 15 días hábiles a la fecha de desactivación.\n")

        subT1 = SpannableString("AUTORIZACIÓN PARA TRATAMIENTO DE DATOS PERSONALES\n")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append("Usted conoce que la información vinculada o que pueda ser asociada a usted (los “Datos Personales”) será incorporada a un banco de datos de titularidad de Flash; por lo que, otorga su consentimiento de manera previa, libre, expresa inequívoca e informada a Flash para, incluidos, sin limitación, su recolección, uso, administración, análisis, segmentación, almacenamiento, transmisión, transferencia y/o supresión (el “Tratamiento”).\n")

        subT1 = SpannableString("CESIÓN DEL CONTRATO\n")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append("Flash podrá ceder, traspasar en forma total o parcial los derechos y/u obligaciones de este contrato, pero te avisaremos oportunamente.\n\n")

        subT1 = SpannableString("TERMINACIÓN\n")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append("Puedes desactivar tu línea cuando quieras haciendo tu solicitud a través de cualquiera de nuestros Medios de atención.\n\n")
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
