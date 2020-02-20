package isdigital.veridium.flash.model.dto

data class ConsolidatedDataResponse(
    val message: String,
    val code: String,
    val status: String,
    val data: ConsolidatedResponse
)