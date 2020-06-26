package isdigital.veridium.flash.model.dto

data class OrderInformation(
    val id: String?,
    val planType: String?,
    val sponsorTeamId: String?,
    val name: String?,
    val paternalLastName: String?,
    val maternalLastName: String?,
    val birthDate: String?,
    val email: String?,
    val wantPortability: Boolean,
    val phoneNumber: String,
    val currentCompany: String?,
    val status: String?,
    val creationDate: String?,
    val populatedCenter: String?,
    val coveragePopulatedCenter: String?,
    val acceptTermsCoveragePopulatedCenter: String?
)