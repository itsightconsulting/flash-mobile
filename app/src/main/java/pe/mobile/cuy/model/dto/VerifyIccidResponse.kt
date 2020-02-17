package pe.mobile.cuy.model.dto

import java.util.*

data class VerifyIccidResponse(
    val message: String,
    val code: String,
    val status: String,
    val data: Objects
)