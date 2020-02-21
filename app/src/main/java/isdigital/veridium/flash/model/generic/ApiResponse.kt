package isdigital.veridium.flash.model.generic

data class ApiResponse<T>(val message: String,
                          val code: String,
                          val status: Int,
                          val data: T)