package isdigital.veridium.flash.model.pojo

import java.io.Serializable

// ConsolidatedDataRequest
data class ActivationPOJO(

    var formId: String?,
    var formStatus: String?,
    var iccid: String?,
    var formCreationDate: String?, // YYYY-DD-MM HH-mm-ss

    var dni: String,
    var name: String,
    var lastName: String,
    var birthDate: String, // YYYY-MM-DD
    var email: String,
    var wantPortability: Boolean,
    var sponsorTeamId: String?,
    var phoneNumber: String?,
    var currentCompany: String?,
    var planType: String?,

    var validationBiometric: Boolean?,
    var validationBiometricDate: String? // YYYY-MM-DD HH-mm-ss

) : Serializable {
    constructor(
        dni: String,
        name: String,
        lastName: String,
        birthDate: String,
        email: String,
        wantPortability: Boolean,
        sponsorTeamId: String?,
        phoneNumber: String?,
        currentCompany: String?,
        planType: String?
    ) : this(
        null, null, null, null,
        dni,
        name,
        lastName,
        birthDate,
        email,
        wantPortability,
        sponsorTeamId,
        phoneNumber,
        currentCompany,
        planType,
        null, null
    )

}