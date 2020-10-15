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
            //val completed = passBarcode && passBiometric
            activation.formId?.let {
                if (it.isNotBlank()) {
                    body["formId"] = it
                }
            }
/*
            if (completed) {
                body["formStatus"] = FORMSTATUS.COMPLETED.value
            } else {
                body["formStatus"] =
                    if (passBarcode) FORMSTATUS.REJECTBIO.value else FORMSTATUS.REJECTICCD.value
            }
 */

            /*
            if(!activation.iccid.isNullOrEmpty())
                body["iccid"] = ICCID.replace("{0}",activation.iccid!!)
*/
            body["iccid"] = activation.iccid ?: ""
            /*if (body["iccid"]!!.length >= 2)
                body["iccid"] = body["iccid"]!!.substring(0, body["iccid"]!!.length)*/

            if (passBarcode) {
                body["validationBiometric"] = passBiometric.toString()
                body["validationBiometricDate"] = SimpleDateFormat(
                    DateTimeFormat
                ).format(Date())
            }

            body["dniCliente"] = activation.dni
            body["name"] = activation.name
            body["maternalLastName"] = activation.maternalLastName
            body["paternalLastName"] = activation.paternalLastName

            //Come from new form
            if (activation.formId.isNullOrEmpty())
                activation.birthDate =
                    changeDateFormat(activation.birthDate, "yyyy-MM-dd", "dd/MM/yyyy")

            body["birthDate"] = activation.birthDate
            body["email"] = activation.email
            if (activation.wantPortability != null) {
                body["wantPortability"] = activation.wantPortability.toString()
            }

            body["populatedCenter"] = activation.populatedCenter.toString()
            body["coveragePopulatedCenter"] = activation.coveragePopulatedCenter.toString()
            body["acceptTermsCoveragePopulatedCenter"] =
                activation.acceptTermsCoveragePopulatedCenter.toString()


            body["sponsorTeamId"] = activation.sponsorTeamId ?: ""
            body["phoneNumber"] = activation.phoneNumber ?: ""
            body["currentCompany"] = activation.currentCompany ?: ""
            if (activation.planType.isNullOrEmpty()) {
                body["planType"] = ""
            } else {
                body["planType"] = getPlanType(activation.planType!!)
            }

            return body
        }
    }
}