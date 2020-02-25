package isdigital.veridium.flash.util

import isdigital.veridium.flash.R
import isdigital.veridium.flash.model.pojo.ActivationPOJO
import isdigital.veridium.flash.preferences.UserPrefs
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class PartnerData {


    companion object {
        private const val DateTimeFormat = "YYYY-MM-DD HH-mm-ss"

        fun formPreparation(
            activation: ActivationPOJO,
            passBarcode: Boolean,
            passBiometric: Boolean
        ): HashMap<String, String> {
            val body = HashMap<String, String>()
            val completed = passBarcode && passBiometric
            if (completed) {
                body["formStatus"] = FORMSTATUS.COMPLETED.value
            } else {
                body["formStatus"] =
                    if (passBarcode) FORMSTATUS.REJECTBIO.value else FORMSTATUS.REJECTICCD.value
            }

            body["formCreationDate"] = SimpleDateFormat(
                DateTimeFormat,
                Locale.getDefault()
            ).format(Date())
            body["iccid"] = activation.iccid ?: ""

            if (passBarcode) {
                body["validationBiometric"] = passBiometric.toString()
                body["validationBiometricDate"] = body["formCreationDate"]!!
            }

            body["dni"] = activation.dni
            body["name"] = activation.name
            body["lastName"] = activation.lastName

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