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
    var paternalLastName: String,
    var maternalLastName: String,
    var birthDate: String, // YYYY-MM-DD
    var email: String,

    var wantPortability: Boolean,
    var sponsorTeamId: String?,
    var phoneNumber: String?,
    var currentCompany: String?,
    var planType: String?,

    var validationBiometric: Boolean?,
    var validationBiometricDate: String?, // YYYY-MM-DD HH-mm-ss
    var creationDate: String?,

    var populatedCenter: String,
    var coveragePopulatedCenter: String,
    var acceptTermsCoveragePopulatedCenter: String

) : Serializable {
    constructor(
        dni: String,
        name: String,
        paternalLastName: String,
        maternalLastName: String,
        birthDate: String,
        email: String,
        wantPortability: Boolean,
        sponsorTeamId: String?,
        phoneNumber: String?,
        currentCompany: String?,
        planType: String?,
        populatedCenter: String,
        coveragePopulatedCenter: String,
        acceptTermsCoveragePopulatedCenter: String
    ) : this(
        null, null, null, null,
        dni,
        name,
        paternalLastName,
        maternalLastName,
        birthDate,
        email,
        wantPortability,
        sponsorTeamId,
        phoneNumber,
        currentCompany,
        planType,
        null,
        null,
        null,
        populatedCenter,
        coveragePopulatedCenter,
        acceptTermsCoveragePopulatedCenter
    )

    constructor(
        formId: String,
        dni: String,
        name: String,
        paternalLastName: String,
        maternalLastName: String,
        birthDate: String,
        email: String,
        wantPortability: Boolean,
        sponsorTeamId: String?,
        phoneNumber: String?,
        currentCompany: String?,
        planType: String?,
        creationDate: String?,
        populatedCenter: String,
        coveragePopulatedCenter: String,
        acceptTermsCoveragePopulatedCenter: String
    ) : this(
        formId, null, null, null,
        dni,
        name,
        paternalLastName,
        maternalLastName,
        birthDate,
        email,
        wantPortability,
        sponsorTeamId,
        phoneNumber,
        currentCompany,
        planType,
        null, null, creationDate,
        populatedCenter,
        coveragePopulatedCenter,
        acceptTermsCoveragePopulatedCenter
    )

}