package pe.mobile.cuy.model.pojo

import java.io.Serializable

// ConsolidatedDataRequest
data class ActivationPOJO(

    var formId: String?,
    var formStatus: String?,
    var iccid: String?,
    var formCreationDate: String?, // YYYY-DD-MM HH-mm-ss

    val dni: String,
    val name: String,
    val lastName: String,
    var birthDate: String, // YYYY-MM-DD
    val email: String,
    val wantPortability: Boolean,
    val sponsorTeamId: String?,
    val phoneNumber: String?,
    val currentCompany: String?,
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