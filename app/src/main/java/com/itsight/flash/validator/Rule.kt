package com.itsight.flash.validator

import com.google.android.material.textfield.TextInputEditText

class Rule(
    callback: (() -> Boolean)? = null,
    preReqCallback: (() -> Boolean)? = null,
    equalsToCallback: ((editText: TextInputEditText) -> Boolean)? = null,
    editText: TextInputEditText? = null,
    flMsg: String,
    ruleId: Int
) {

    var msg: String = flMsg
    private val flCallback = callback
    private val flEqualsTo = equalsToCallback
    private val preCallback = preReqCallback
    private val flEditText = editText
    private val id = ruleId
    fun check(): Boolean {
        var res: Boolean
        preCallback?.let {
            res = preCallback.invoke()
            if (!res) {
                return true
            }
        }

        flCallback?.let {
            return it()
        }

        flEqualsTo?.let {
            return it.invoke(flEditText!!)
        }
        return false
    }

    fun getId(): Any {
        return this.id
    }
}