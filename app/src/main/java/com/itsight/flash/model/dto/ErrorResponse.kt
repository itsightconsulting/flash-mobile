package com.itsight.flash.model.dto

data class ErrorResponse(val code: String, val message: String, val status: Int){
    constructor(message: String, status: Int):this("", message, status)
}