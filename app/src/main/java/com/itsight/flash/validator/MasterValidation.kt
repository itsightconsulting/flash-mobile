package com.itsight.flash.validator

import android.widget.AutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText

class MasterValidation {

    private var inputs: ArrayList<Validations> = ArrayList()

    fun getRules() = this.inputs

    fun valid(
        textInputEditText: TextInputEditText,
        eventsActive: Boolean = true,
        preRequisite: (() -> Boolean)? = null
    ): Validations {
        val objVal = Validations(textInputEditText, null, this, eventsActive, preRequisite)
        this.inputs.add(objVal)
        return objVal
    }

    fun valid(
        autoCompleteTextView: AutoCompleteTextView,
        eventsActive: Boolean = true, preRequisite: (() -> Boolean)? = null
    ): Validations {
        val objVal = Validations(null, autoCompleteTextView, this, eventsActive, preRequisite)
        this.inputs.add(objVal)
        return objVal
    }

    fun checkValidity(): Boolean {
        return this.inputs.filter { it.internalValidation() }.size == this.inputs.size
    }
}