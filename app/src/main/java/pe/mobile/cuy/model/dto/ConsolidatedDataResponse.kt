package pe.mobile.cuy.model.dto

data class ConsolidatedDataResponse(
    val message: String,
    val code: String,
    val status: String,
    val data: ConsolidatedResponse
)