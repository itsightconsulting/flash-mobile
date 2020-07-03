package isdigital.veridium.flash.view


import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar

import isdigital.veridium.flash.R
import isdigital.veridium.flash.validator.MasterValidation
import kotlinx.android.synthetic.main.form_phone_fragment.*
import isdigital.veridium.flash.FlashApplication
import isdigital.veridium.flash.model.pojo.ActivationPOJO
import isdigital.veridium.flash.preferences.UserPrefs
import isdigital.veridium.flash.util.*

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
        tvTermsLabel.setOnClickListener {
            clickListenerFortvTermsLabel()
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
        else txtTermsLabel.error = "Debes aceptar las condiciones aplicables a la portabilidad"
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
        var subT1 = SpannableString("\n1. Gratuidad del proceso: ")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append("Tu proceso de portabilidad no tendrá ningún costo. Sin embargo, ten presente que debes adquirir la Sim Card Flash a través de uno de nuestros Brand Leaders.\n\n")

        subT1 = SpannableString("2. Plazo de ejecución: ")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append("Tu portabilidad quedará efectuada dentro de las veinticuatro (24) horas siguientes a partir del momento en que tu solicitud de portabilidad haya sido presentada.\n\n")

        subT1 = SpannableString("3. Requisitos:\n")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append("• Ser titular de la línea a portar\n")
        txtTerms.append("• No contar con otra solicitud de portabilidad en trámite\n")
        txtTerms.append("• Tener más de un (1) mes en tu operador actual\n")
        txtTerms.append(
            "• No tener el servicio suspendido por deuda, mandato judicial, declaración de insolvencia o por uso indebido\n" +
                    "del servicio.\n"
        )
        txtTerms.append("• No estar dado de baja mas de 30 días\n")
        txtTerms.append("• No tener deuda vencida con su operador actual\n\n")

        subT1 = SpannableString("4. Condiciones del servicio: ")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append(
            "Para que conozcas las características, tecnología empleada, modalidad, alcance y limitaciones o restricciones de nuestro servicio, " +
                    "dirígete a nuestra página web flashmobile.pe sección “Atencion al cliente” botón “Información para abonados”. Así mismo para que conozcas " +
                    "las condiciones económicas del servicio ofrecido dirígete a nuestra página web flashmobile.pe sección “Planes”.\n\n"
        )

        subT1 = SpannableString("5. Equipos: ")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append(
            "Para saber si tu equipo terminal es compatible con la red de Flash dirígete a nuestra a nuestra página web flashmobile.pe " +
                    "sección “Información para abonados” botón “Condiciones del Servicio” y “Características del Servicio”.\n\n"
        )

        subT1 = SpannableString("6. Mapa de cobertura: ")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append(
            "Conoce la cobertura del servicio ofrecido por Flash en nuestro mapa de cobertura\n\n"
        )

        subT1 = SpannableString("7. Objeción de la portabilidad: ")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append(
            "Ten presente que tu portabilidad puede ser objetada si a la fecha de tu solicitud de portabilidad:\n\n"
        )
        txtTerms.append(
            "• Tienes suspendido el servicio por mandato judicial, por deuda, por declaración de insolvencia, por uso indebido " +
                    "del servicio o por uso prohibido;\n"
        )
        txtTerms.append("• Tienes deuda exigible con tu operador cedente;\n")
        txtTerms.append(
            "• No cuentas con una relación contractual con el operador cedente por haberse dado de baja y no se encuentra " +
                    "dentro del plazo de los treinta (30) días calendario siguientes a la terminación del contrato;\n"
        )
        txtTerms.append("• No tienes al menos un (1) mes de servicio en la red del operador cedente.\n\n")

        subT1 = SpannableString("8. Exigibilidad de obligaciones: ")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append(
            "Es posible que tu operador cedente exija el cobro de obligaciones pendientes de pago a la fecha de la solicitud de la " +
                    "portabilidad por concepto de servicio o como consecuencia de la obligación de la terminación anticipada del contrato. " +
                    "Adicionalmente, ten en cuenta que dentro de los treinta (30) días calendarios de deshabilitado el servicio de la red de " +
                    "tu operador cedente, este puede solicitar la suspensión del servicio por existir obligaciones económicas exigibles.\n\n"
        )

        subT1 = SpannableString("9. Afectación del servicio: ")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append("Ten presente que tu servicio podrá verse afectado por un máximo de tres (3) horas durante el proceso de cambio de tu línea a Flash.\n\n")

        subT1 = SpannableString("10. Perdida de Saldos: ")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)
        txtTerms.append(
            "Tus saldos prepagos y el trafico incluido en tus planes tarifarios no consumidos, así como cualquier otro beneficio otorgado " +
                    "por tu operador cedente, luego del cambio de tu línea a Flash se perderá.\n\n"
        )

        subT1 = SpannableString("DECLARACIÓN DE CONOCIMIENTO\n")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)

        subT1 = SpannableString("Declaro tener conocimiento sobre lo siguiente:\n")
        subT1.setSpan(StyleSpan(Typeface.BOLD), 0, subT1.length, 0)
        txtTerms.append(subT1)

        txtTerms.append(
            "• Con la portabilidad doy por concluido mi contrato con mi actual empresa operadora del servicio " +
                    "de telefonía fija o móvil por lo que dejo sin efecto las tarifas y condiciones contratadas " +
                    "con dicha empresa; y contrato a una nueva empresa operadora con nuevas condiciones " +
                    "(precio, minutos incluidos, etc.), manteniendo los mismos numeros señalados anteriormente.\n"
        )

        txtTerms.append(
            "• El tramite de portabitidad es gratuito y demora 24 horas desde la presentación de esta" +
                    " solicitud (excepto cuando voy a portar mas de 10 números). La portación de numeros del servicio " +
                    "de telefonía fija esta sujeta a la instalación previa del nuevo servicio.\n"
        )
        txtTerms.append(
            "• Si voy a portar mi número del servicio de telefonía fija, mi nueva empresa operadora " +
                    "debe confirmarme que tiene facilidades técnicas para darme el servicio.\n"
        )
        txtTerms.append("• Puedo utilizar mi equipo terminal (equipo telefónico), a menos que este no sea compatible con la red de la nueva empresa operadora.\n")
        txtTerms.append(
            "• Si estoy portando números del servicio móvil, el cambio de empresa operadora se efectuará así no haya recogido el chip " +
                    "y/o equipo de mi nueva empresa. Si mi solicitud es presencial, mi nueva empresa operadora tiene la obligación de darme el chip " +
                    "en el momento de la firma de mi nuevo contrato (hasta 10 números portados).\n"
        )
        txtTerms.append(
            "• Si he contratado el servicio de telefonía fija con mi actual empresa operadora a un plaza forzoso que " +
                    "todavia no acaba, y decido portar mi número, dicha empresa podría cobrarme penalidades derivadas de terminar " +
                    "mi contrato antes del vencimiento de dicho plaza forzoso. La penalidad debe estar incluida en mi contrato.\n"
        )
        txtTerms.append(
            "• Que mi último recibo telefónico puede no incluir montos por servicios brindados después " +
                    "de la emisión de dicho recibo; montos que pueden ser cobrados posteriormente y cuya falta de pago " +
                    "puede implicar que mi actual empresa operadora solicite a mi nueva empresa la suspensión par 30 días de mi servicio telefónico.\n"
        )
        txtTerms.append(
            "• La portabilidad se refiere únicamente al servicio telefónico (móvil o fijo) y no a otros servicios " +
                    "(internet, TV cable, larga distancia) que podrían brindarse conjuntamente con mi servicio telefónico (paquetes), " +
                    "por lo que al terminar mi contrato de servicio telefónico (fijo o móvil), las otros servicios seguirán siendo brindados " +
                    "y cobrados par mi actual empresa operadora de internet, TV cable, larga distancia, pero tal vez en condiciones tarifarias " +
                    "diferentes; teniendo el derecho de resolver también dichos contratos y contratar los mismos, de ser el caso, a mi nueva empresa operadora.\n"
        )
        txtTerms.append(
            "• Mi servicio telefónico puede verse afectado por un plazo máximo de tres (3) horas mientras dure el " +
                    "cambio de mi actual empresa operadora a mi nueva empresa (entre las 0:00 am y 6:00 am).\n\n"
        )

    }


    private fun clickListenerForBtnContinue() {
        var Terms = getTerms();

        if (this.validatorMatrix.checkValidity()
            && Terms == true
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