package isdigital.veridium.flash.model.dto

import java.util.*

data class VerifyIccidResponse(
    val message: String,
    val code: String,
    val status: String,
    val data: Objects
)