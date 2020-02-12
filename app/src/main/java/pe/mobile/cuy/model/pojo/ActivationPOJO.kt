package pe.mobile.cuy.model.pojo

import java.io.Serializable

class ActivationPOJO(
    val dni: String,
    val name: String,
    val lastName: String,
    val birthDate: String,
    val email: String,
    val wantToPortability: Boolean,
    val sponsorTeamId: String?,
    val phoneNumber: String?,
    val currentCompany: String?,
    val planType: String?
) : Serializable