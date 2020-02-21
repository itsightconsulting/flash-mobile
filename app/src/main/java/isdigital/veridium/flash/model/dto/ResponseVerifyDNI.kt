package isdigital.veridium.flash.model.dto

import isdigital.veridium.flash.model.pojo.ActivationPOJO

data class ResponseVerifyDNI(val data: List<ActivationPOJO>, val message: String, val status: Int)