package isdigital.veridium.flash.model.dto

data class ResponseVerifyDNI(
    val data: PendingActivations,
    val message: String,
    val status: Int,
    val code: String
)