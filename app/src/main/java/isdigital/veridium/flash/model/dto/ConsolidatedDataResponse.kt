package isdigital.veridium.flash.model.dto

import java.util.*

data class ConsolidatedDataResponse(
    val message: String,
    val code: String,
    val status: Int,
    val data: Objects
)