package pe.mobile.cuy.model.dto

data class ConsolidatedResponse(
    val formId: String?
    , val dni: String
    , val formStatus: String
    , val iccid: String
    , val formCreationDate: String
    , val name: String
    , val lastName: String
    , val birthDate: String
    , val email: String
    , val sponsorTeamId: String?
    , val wantPortability: Boolean
    , val phoneNumber: String?
    , val currentCompany: String?
    , val planType: String?
    , val validationBiometric: Boolean
    , val validationBiometricDate: String
)