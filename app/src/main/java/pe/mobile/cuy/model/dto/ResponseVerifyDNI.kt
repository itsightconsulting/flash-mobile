package pe.mobile.cuy.model.dto

import pe.mobile.cuy.model.pojo.ActivationPOJO

data class ResponseVerifyDNI(val data: List<ActivationPOJO>, val message: String, val status: String)