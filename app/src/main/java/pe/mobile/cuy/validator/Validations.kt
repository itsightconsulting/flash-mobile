package pe.mobile.cuy.validator

import android.util.Patterns
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import pe.mobile.cuy.util.RULESVAL

class Validations(
    TextInputEditText: TextInputEditText?,
    autoCompleteTextView: AutoCompleteTextView?,
    masterValidation: MasterValidation,
    eventEnabled: Boolean,
    highPriorityCallback: (() -> Boolean)? = null
) {
    private val textInputLayout =
        (TextInputEditText ?: autoCompleteTextView!!).parent.parent as TextInputLayout
    val value = (TextInputEditText ?: autoCompleteTextView!!).text.toString()
    var valid: Boolean = true
    val list: ArrayList<Rule> = ArrayList()
    val editText = TextInputEditText ?: autoCompleteTextView!!
    val master = masterValidation
    val id = (TextInputEditText ?: autoCompleteTextView!!).id
    val preCallback: (() -> Boolean)? = highPriorityCallback

    private var minLen: Int = 0
    private var min: Int = 0
    private var maxLen: Int = 0

    init {
        TextInputEditText?.let { it ->
            if (!eventEnabled) return@let

            it.setOnFocusChangeListener { view: View, focus: Boolean ->
                if (focus) return@setOnFocusChangeListener
                if (this.list.size > 0) {
                    var ele = this.list.firstOrNull { !it.check() }
                    ele?.let {
                        textInputLayout.error = it.msg
                    }
                    if (ele == null) {
                        textInputLayout.error = ""
                    }
                }
            }

            it.doAfterTextChanged {
                if (this.list.size > 0) {
                    var ele = this.list.firstOrNull { !it.check() }
                    if (ele == null) {
                        textInputLayout.error = ""
                    }
                }
            }
        }

        autoCompleteTextView?.let { it ->
            if (!eventEnabled) return@let

            it.setOnItemClickListener { parent, view, position, id ->
                var ele = this.list.firstOrNull { !it.check() }
                ele?.let {
                    textInputLayout.error = it.msg
                }
                if (ele == null) {
                    textInputLayout.error = ""
                }
            }
        }
    }

    fun internalValidation(): Boolean {
        var flValid = true
        var ele = this.list.firstOrNull { !it.check() }
        ele?.let {
            flValid = false
            textInputLayout.error = it.msg
        }
        if (ele == null) {
            textInputLayout.error = ""
        }
        return flValid
    }

    fun email(): Validations {
        this.list.add(
            Rule(
                callback = ::flValidateEmail,
                preReqCallback = preCallback,
                flMsg = "El correo tiene un formato inválido",
                ruleId = RULESVAL.EMAIL.value
            )
        )
        return this
    }

    fun email(csMsg: String): Validations {
        this.list.add(
            Rule(
                callback = ::flValidateEmail,
                preReqCallback = preCallback,
                flMsg = csMsg,
                ruleId = RULESVAL.EMAIL.value
            )
        )
        return this
    }

    fun flValidateEmail(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(editText.text.toString()).matches()
    }

    fun minLength(len: Int): Validations {
        this.minLen = len
        this.list.add(
            Rule(
                callback = ::flValidateMinLength,
                preReqCallback = preCallback,
                flMsg = "Debe ingresar mínimo $len caracteres",
                ruleId = RULESVAL.MIN_LENGTH.value
            )
        )
        return this
    }

    fun maxLength(len: Int): Validations {
        this.maxLen = len
        this.list.add(
            Rule(
                callback = ::flValidateMaxLength,
                preReqCallback = preCallback,
                flMsg = "Debe ingresar máximo $len caracteres",
                ruleId = RULESVAL.MAX_LENGTH.value
            )
        )
        return this
    }

    fun flValidateMinLength(): Boolean {
        return editText.text.toString().isBlank() || editText.text.toString().length >= this.minLen
    }

    fun flValidateMaxLength(): Boolean {
        return editText.text.toString().isBlank() || editText.text.toString().length <= this.maxLen
    }

    fun required(): Validations {
        this.list.add(
            Rule(
                callback = ::flRequired,
                preReqCallback = preCallback,
                flMsg = "Este campo es obligatorio",
                ruleId = RULESVAL.REQUIRED.value
            )
        )
        return this
    }

    fun min(min: Int): Validations {
        this.min = min
        this.list.add(
            Rule(
                callback = ::flmin,
                preReqCallback = preCallback,
                flMsg = "Debe ingresar un valor mayor o igual a: ${"%.2f".format(this.min.toDouble())}",
                ruleId = RULESVAL.REQUIRED.value
            )
        )
        return this
    }

    fun flmin(): Boolean {
        return editText.text.toString().isBlank() || editText.text.toString().toDouble() >= this.min
    }

    fun flRequired(): Boolean {
        return editText.text.toString().isNotEmpty()
    }

    fun equalsTo(editText: TextInputEditText): Validations {
        this.list.add(
            Rule(
                preReqCallback = preCallback,
                equalsToCallback = ::flEqualsTo,
                flMsg = "El campo ingresado debe ser igual al campo anterior",
                ruleId = RULESVAL.EQUALS_TO.value,
                editText = editText
            )
        )
        return this
    }

    fun flEqualsTo(etText: TextInputEditText): Boolean {
        return this.editText.text.toString() == etText.text.toString()
    }

    fun callbackTrue(): Boolean {
        return true
    }


    fun validateNumber(): Validations {
        try {

        } catch (ex: NumberFormatException) {
            valid = false
        }
        return this
    }

    fun and(): MasterValidation {
        return this.master
    }

    fun active(): MasterValidation {
        return this.master
    }

}