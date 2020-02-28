package isdigital.veridium.flash.util

import isdigital.veridium.flash.model.pojo.ActivationPOJO
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class PartnerData {

    companion object {
        private const val DateTimeFormat = "yyyy-MM-dd HH:mm:ss"

        fun formPreparation(
            activation: ActivationPOJO,
            passBarcode: Boolean,
            passBiometric: Boolean
        ): HashMap<String, String> {
            val body = HashMap<String, String>()
            val completed = passBarcode && passBiometric
            activation.formId?.let {
                if(it.isNotBlank()){
                    body["formId"] = it
                }
            }

            if (completed) {
                body["formStatus"] = FORMSTATUS.COMPLETED.value
            } else {
                body["formStatus"] =
                    if (passBarcode) FORMSTATUS.REJECTBIO.value else FORMSTATUS.REJECTICCD.value
            }

            body["iccid"] = activation.iccid ?: ""

            if (passBarcode) {
                body["validationBiometric"] = passBiometric.toString()
                body["validationBiometricDate"] = SimpleDateFormat(
                    DateTimeFormat
                ).format(Date())
            }

            body["dni"] = activation.dni
            body["name"] = activation.name
            body["lastName"] = activation.lastName

            //Come from new form
            if (activation.formId.isNullOrEmpty())
                activation.birthDate =
                    changeDateFormat(activation.birthDate, "yyyy-MM-dd", "dd/MM/yyyy")

            body["birthDate"] = activation.birthDate
            body["email"] = activation.email
            if (activation.wantPortability != null) {
                body["wantPortability"] = activation.wantPortability.toString()
            }
            body["sponsorTeamId"] = activation.sponsorTeamId ?: ""
            body["phoneNumber"] = activation.phoneNumber ?: ""
            body["currentCompany"] = activation.currentCompany ?: ""
            body["planType"] = activation.planType ?: ""

            return body
        }
    }
}