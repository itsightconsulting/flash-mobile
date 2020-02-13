package pe.mobile.cuy.model.pojo

import java.io.Serializable

class ActivationPOJO(

    val formId: String?,
    val token: String?,
    val status: Boolean?,
    val iccid: String?,
    val creationDate: String?, // YYYY-DD-MM HH-mm-ss

    val dni: String,
    val name: String,
    val lastName: String,
    val birthDate: String, // YYYY-MM-DD
    val email: String,
    val wantPortability: Boolean,
    val sponsorTeamId: String?,
    val phoneNumber: String?,
    val currentCompany: String?,
    val planType: String?,

    val validatedBiometric: Boolean?,
    val validatedBiometricDate: String? // YYYY-MM-DD HH-mm-ss

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
        null, null, null, null, null,
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