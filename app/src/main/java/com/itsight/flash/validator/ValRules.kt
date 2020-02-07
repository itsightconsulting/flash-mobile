package com.itsight.flash.validator

class ValRules(
    callback: () -> Boolean,
    preReqCallback: (() -> Boolean)? = null,
    flMsg: String,
    ruleId: Int
) {

    var msg: String = flMsg
    private val flCallback = callback
    private val preCallback = preReqCallback
    private val id = ruleId
    fun check(): Boolean {
        if (preCallback != null) {
            if (preCallback.invoke()) {
                return flCallback()
            }
            return true
        }
        return flCallback()
    }

    fun getId(): Any {
        return this.id
    }
}