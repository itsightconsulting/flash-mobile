package com.itsight.flash.util

import android.app.Dialog
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.itsight.flash.FlashApplication
import com.itsight.flash.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList



enum class POSTYPES(val value: Int) {
    POS_APPROVED(1),
    POS_APPLICANT(2)
}

enum class RULESVAL(val value: Int) {
    REQUIRED(0),
    MIN_LENGTH(1),
    MAX_LENGTH(2),
    EMAIL(3),
}

enum class PROFILES {
    DEV_HOME, DEV_WORK, DEV_CLOUD, PRODUCTION
}

val API_PROFILE = PROFILES.DEV_CLOUD.name


val API_BASE_URL = getAPIBaseURLByProfile(API_PROFILE)
val API_RESOURCE_SERVERBASE_URL = getAPIResourceServerBaseURLByProfile(API_PROFILE)
const val API_BASE_AUTH_SERVER = "https://epay-auth.azurewebsites.net"
const val API_BASE_RSRC_SERVER = "https://epay-rsrc.azurewebsites.net"


val documentTypes: HashMap<Int, String> = hashMapOf(
    1 to "RUC",
    2 to "DNI",
    3 to "CE",
    4 to "PASAPORTE"
)

val profiles: HashMap<Int, String> = hashMapOf(
    1 to "Super Admin",
    2 to "Proveedor",
    3 to "Disribuidor",
    4 to "Administrador",
    5 to "BackOffice",
    6 to "Vendedor(a)",
    7 to "PDV"
)

fun getAPIBaseURLByProfile(profile: String): String {
    return when (profile) {
        PROFILES.DEV_CLOUD.name -> "$API_BASE_AUTH_SERVER/api/v1/"
        PROFILES.DEV_WORK.name -> "http://192.168.1.4:8080/api/v1/"
        PROFILES.DEV_HOME.name -> "http://192.168.1.6:8080/api/v1/"
        PROFILES.PRODUCTION.name -> "/"
        else -> "/"
    }
}

fun getAPIResourceServerBaseURLByProfile(profile: String): String {
    return when (profile) {
        PROFILES.DEV_CLOUD.name -> "$API_BASE_RSRC_SERVER/api/v1/"
        PROFILES.DEV_WORK.name -> "http://192.168.1.4:8090/api/v1/"
        PROFILES.DEV_HOME.name -> "http://192.168.1.6:8090/api/v1/"
        PROFILES.PRODUCTION.name -> "/"
        else -> "/"
    }
}

fun getText(editText: EditText): String {
    return editText.text.toString()
}

fun View.csSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_INDEFINITE,
    messageCallback: String = "",
    callback: (() -> Unit)? = null
) {
    if(callback != null){
        Snackbar.make(this, message, duration)
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
            .setAction(messageCallback) { callback() }
            .show()
    }else {
        Snackbar.make(this, message, duration)
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
            .show()
    }
}


fun getRealPathFromUri(uri: Uri): String? {
    // DocumentProvider
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(
            FlashApplication.appContext,
            uri
        )
    ) {
        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]

            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageState().toString() + "/" + split[1]
            }
        } else if (isDownloadsDocument(uri)) {

            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
            )

            return getDataColumn(FlashApplication.appContext!!, contentUri, null, null)
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]

            var contentUri: Uri? = null
            if ("image" == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])

            return contentUri?.let {
                getDataColumn(
                    FlashApplication.appContext!!,
                    it,
                    selection,
                    selectionArgs
                )
            }
        }// MediaProvider
        // DownloadsProvider
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {

        // Return the remote address
        return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
            FlashApplication.appContext!!,
            uri,
            null,
            null
        )

    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }// File
    // MediaStore (and general)

    return null
}

fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}

fun isGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.content" == uri.authority
}

fun forceMinimize(requireActivity: FragmentActivity, lifecycleOwner: LifecycleOwner) {
    //Minimize the app programmatically
    val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            requireActivity.moveTaskToBack(true)
        }
    }
    requireActivity.onBackPressedDispatcher.addCallback(
        lifecycleOwner, onBackPressedCallback
    )
}

fun getDataColumn(
    context: Context, uri: Uri, selection: String?,
    selectionArgs: Array<String>?
): String? {

    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)

    try {
        cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        if (cursor != null && cursor!!.moveToFirst()) {
            val index = cursor!!.getColumnIndexOrThrow(column)
            return cursor!!.getString(index)
        }
    } finally {
        if (cursor != null)
            cursor!!.close()
    }
    return null
}


@Throws(IOException::class)
public fun createImageFile(context: Context): File {
    // Create an image file name
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir: File = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
    return File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    )
}

fun invokerSuccessDialog(context: Context, bodyMessage: String?): Dialog {
    val dialog = Dialog(context)
    dialog.setContentView(R.layout.alert_success)

    dialog.window!!.setLayout(
        (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
        WindowManager.LayoutParams.WRAP_CONTENT
    )
    bodyMessage?.let {
        dialog.findViewById<TextView>(R.id.alertSuccessMessage).text = bodyMessage
    }
    val buttonDialog = dialog.findViewById(R.id.btnAlertDismiss) as Button
    buttonDialog.setOnClickListener {
        dialog.dismiss()
    }
    return dialog
}

fun invokerErrorDialog(context: Context, bodyMessage: String?): Dialog {
    val dialog = Dialog(context)
    dialog.setContentView(R.layout.alert_error)

    dialog.window!!.setLayout(
        (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
        WindowManager.LayoutParams.WRAP_CONTENT
    )
    bodyMessage?.let {
        val txtMsg = dialog.findViewById<TextView>(R.id.alertErrorMessage)
        txtMsg.text = bodyMessage
        txtMsg.movementMethod = ScrollingMovementMethod()
    }
    val buttonDialog = dialog.findViewById(R.id.btnAlertDismiss) as Button
    buttonDialog.setOnClickListener {
        dialog.dismiss()
    }
    return dialog
}

fun Boolean.onTrue(block: (et: TextInputEditText) -> Unit, et: TextInputEditText): Boolean {
    if (this) block(et)
    return this
}

fun Boolean.nextEvaluation(
    block: (et: TextInputEditText) -> Boolean,
    et: TextInputEditText
): Boolean {
    if (!block(et)) {
        return false
    }
    return this
}

class MasterValidation {

    private var inputs: ArrayList<Validations> = ArrayList()

    fun getRules() = this.inputs

    fun valid(textInputEditText: TextInputEditText, eventsActive: Boolean = true): Validations {
        val objVal = Validations(textInputEditText, null, this, eventsActive)
        this.inputs.add(objVal)
        return objVal
    }

    fun valid(autoCompleteTextView: AutoCompleteTextView, eventsActive: Boolean = true): Validations {
        val objVal = Validations(null, autoCompleteTextView, this, eventsActive)
        this.inputs.add(objVal)
        return objVal
    }

    fun checkValidity(): Boolean {
        return this.inputs.filter{it.internalValidation()}.size == this.inputs.size
    }

}

class ValRules (callback: () -> Boolean, preReqCallback: (() -> Boolean)? = null, flMsg: String, ruleId: Int){

    var msg: String = flMsg
    private val flCallback = callback
    private val preCallback = preReqCallback
    private val id = ruleId
    fun check(): Boolean{
        if(preCallback != null){
            if(preCallback.invoke()){
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

class Validations(textInputEditText: TextInputEditText?, autoCompleteTextView: AutoCompleteTextView?, masterValidation: MasterValidation, eventEnabled: Boolean) {

    private val textInputLayout = (textInputEditText?:autoCompleteTextView!!).parent.parent as TextInputLayout
    val value = (textInputEditText?:autoCompleteTextView!!).text.toString()
    var valid: Boolean = true
    val list: ArrayList<ValRules> = ArrayList()
    val editText = textInputEditText?:autoCompleteTextView!!
    var minLen: Int = 0
    var min:Int = 0
    var maxLen: Int = 0
    val master = masterValidation
    val id = (textInputEditText?:autoCompleteTextView!!).id

    init {
        textInputEditText?.let { it ->
            if(!eventEnabled) return@let

            it.setOnFocusChangeListener { view: View, focus: Boolean ->
                if(focus) return@setOnFocusChangeListener
                if(this.list.size > 0){
                    var ele = this.list.firstOrNull { !it.check() }
                    ele?.let {
                        textInputLayout.error = it.msg
                    }
                    if(ele == null){
                        textInputLayout.error = ""
                    }
                }
            }

            it.doAfterTextChanged {
                if(this.list.size > 0){
                    var ele = this.list.firstOrNull { !it.check() }
                    if(ele == null){
                        textInputLayout.error = ""
                    }
                }
            }
        }

        autoCompleteTextView?.let { it ->
            if(!eventEnabled) return@let

            it.setOnItemClickListener {parent, view, position, id ->
                var ele = this.list.firstOrNull { !it.check() }
                ele?.let {
                    textInputLayout.error = it.msg
                }
                if(ele == null){
                    textInputLayout.error = ""
                }
            }
        }
    }

    fun internalValidation(): Boolean{
        var flValid = true
        var ele = this.list.firstOrNull { !it.check() }
        ele?.let {
            flValid = false
            textInputLayout.error = it.msg
        }
        if(ele == null){
            textInputLayout.error = ""
        }
        return flValid
    }

    fun email(): Validations {
        this.list.add(
            ValRules(::flValidateEmail, null,"Email with invalid format", RULESVAL.EMAIL.value)
        )
        return this
    }

    fun email(csMsg: String): Validations {
        this.list.add(
            ValRules(::flValidateEmail,null, csMsg, RULESVAL.EMAIL.value)
        )
        return this
    }

    fun flValidateEmail(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(editText!!.text.toString()).matches()
    }

    fun minLength(len: Int): Validations {
        this.minLen = len
        this.list.add(
            ValRules(::flValidateLength, null,"Debe ingresar mínimo $len caracteres", RULESVAL.MIN_LENGTH.value)
        )
        return this
    }

    fun flValidateLength(): Boolean {
        return editText!!.text.toString().isBlank() || editText!!.text.toString().length >= this.minLen
    }

    fun required(): Validations{
        this.list.add(
            ValRules(::flRequired, null,"Este campo es obligatorio", RULESVAL.REQUIRED.value)
        )
        return this
    }

    fun min(min: Int): Validations{
        this.min = min
        this.list.add(
            ValRules(::flmin, null,"Debe ingresar un valor mayor a: S/. ${"%.2f".format(this.min.toDouble())}", RULESVAL.REQUIRED.value)
        )
        return this
    }

    fun flmin(): Boolean {
        return editText!!.text.toString().isBlank() || editText!!.text.toString().toDouble() >= this.min
    }

    fun required(prerequisite: () -> Boolean): Validations{
        this.list.add(
            ValRules(::flRequired, prerequisite, "Este campo es obligatorio", RULESVAL.REQUIRED.value)
        )
        return this
    }

    fun flRequired(): Boolean{
        return  editText!!.text.toString().isNotEmpty()
    }

    fun callbackTrue(): Boolean{
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
