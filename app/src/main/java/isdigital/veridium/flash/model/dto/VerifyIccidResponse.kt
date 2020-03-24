package isdigital.veridium.flash.model.dto

data class VerifyIccidResponse(
    val message: String,
    val code: String,
    val status: String
)