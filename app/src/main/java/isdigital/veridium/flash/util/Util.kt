package isdigital.veridium.flash.util

import android.app.Dialog
import android.content.ContentUris
import android.content.Context
import android.content.res.Resources
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Parcel
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import isdigital.veridium.flash.FlashApplication
import isdigital.veridium.flash.R
import isdigital.veridium.flash.model.dto.ErrorResponse
import isdigital.veridium.flash.model.dto.OrderInformation
import isdigital.veridium.flash.model.parcelable.OrderInformationArgs
import isdigital.veridium.flash.model.pojo.ActivationPOJO
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


enum class POSTYPES(val value: Int) {
    POS_APPROVED(1),
    POS_APPLICANT(2)
}

enum class RULESVAL(val value: Int) {
    REQUIRED(0),
    MIN_LENGTH(1),
    MAX_LENGTH(2),
    EMAIL(3),
    EQUALS_TO(4),
    START_WITH(5)
}

enum class FORMSTATUS(val value: String) {
    COMPLETED("Completed"),
    REJECTICCD("Rejected ICCD"),
    REJECTBIO("Rejected BIO")
}

enum class PLAN_TYPES(val value: String) {
    POSTPAGO("Postpago"),
    PREPAGO("Prepago")
}

enum class OPERATORS(val value: String) {
    BITEL("Bitel"),
    CLARO("Claro"),
    CUY("Cuy"),
    ENTEL("Entel"),
    MOVISTAR("Movistar")
}

enum class HANDS(val value: String) {
    LEFT("Left"),
    RIGHT("Right")
}


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

fun getText(editText: EditText): String {
    return editText.text.toString()
}

fun View.csSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_INDEFINITE,
    messageCallback: String = "",
    callback: (() -> Unit)? = null
) {
    if (callback != null) {
        Snackbar.make(this, message, duration)
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
            .setAction(messageCallback) { callback() }
            .show()
    } else {
        Snackbar.make(this, message, duration)
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
            .show()
    }
}

fun showSpinner(activity: FragmentActivity?) {

    activity?.let {
        Glide.with(it.applicationContext).load(R.drawable.cargador).into(
            it.findViewById(R.id.mainSpinner)
        )
        it.findViewById<LinearLayout>(R.id.transitionerLayout).visibility = View.VISIBLE
    }
}


fun hideSpinner(activity: FragmentActivity?) {
    activity?.let {
        it.findViewById<LinearLayout>(R.id.transitionerLayout).visibility = View.GONE
        Glide.with(it.applicationContext).load(R.drawable.ic_hourglass_empty).into(
            it.findViewById(R.id.mainSpinner)
        )
    }
}

fun instanceHttpError(e: Throwable): ErrorResponse {
    val exception = e as HttpException
    if (exception.response() != null) {
        val error = JSONObject(exception.toString())
        return ErrorResponse(
            error.getString("code"),
            error.getString("message"),
            error.getInt("status")
        )
    }
    return ErrorResponse(
        exception.code().toString(),
        GENERIC_ERROR_MESSAGE,
        exception.code()
    )
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

            return getDataColumn(FlashApplication.appContext, contentUri, null, null)
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
                    FlashApplication.appContext,
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
            FlashApplication.appContext,
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
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(index)
        }
    } finally {
        if (cursor != null)
            cursor.close()
    }
    return null
}


@Throws(IOException::class)
public fun createImageFile(context: Context): File {
    // Create an image file name
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
    return File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    )
}

fun invokerSuccessDialog(context: Context, bodyMessage: String?): Dialog {
    val dialog = Dialog(context)
    dialog.setContentView(R.layout.alert_success)
    dialog.setCanceledOnTouchOutside(false)
    dialog.setCancelable(false)

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
    dialog.setCanceledOnTouchOutside(false)
    dialog.setCancelable(false)

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

fun invokerQuitDialog(context: Context): Dialog {
    val dialog = Dialog(context)
    dialog.setContentView(R.layout.alert_quit)
    dialog.setCanceledOnTouchOutside(false)
    dialog.setCancelable(false)
    dialog.window!!.setLayout(
        (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
        WindowManager.LayoutParams.WRAP_CONTENT
    )
    return dialog
}

fun invokerBarcodeSuccess(context: Context): Dialog {
    val dialog = Dialog(context)
    dialog.setContentView(R.layout.info_barcode_success)
    dialog.setCanceledOnTouchOutside(false)
    dialog.setCancelable(false)
    dialog.window!!.setLayout(
        (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
        WindowManager.LayoutParams.WRAP_CONTENT
    )
    return dialog
}

fun invokerBarcodeError(context: Context): Dialog {
    val dialog = Dialog(context)
    dialog.setContentView(R.layout.info_barcode_error)
    dialog.setCanceledOnTouchOutside(false)
    dialog.setCancelable(false)
    dialog.window!!.setLayout(
        (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
        WindowManager.LayoutParams.WRAP_CONTENT
    )
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

fun Parcel.writeBooleanMe(flag: Boolean?) {
    when (flag) {
        true -> writeInt(1)
        false -> writeInt(0)
        else -> writeInt(-1)
    }
}

fun Parcel.readBooleanMe(): Boolean {
    return when (readInt()) {
        1 -> true
        0 -> false
        else -> false
    }
}

fun orderInformationToArgsBase(
    orders: List<OrderInformation>,
    ordersArgs: ArrayList<OrderInformationArgs>
): ArrayList<OrderInformationArgs> {
    orders.forEach {
        ordersArgs.add(
            OrderInformationArgs(
                it.id,
                it.planType,
                it.sponsorTeamId,
                it.name,
                it.lastName,
                it.birthDate,
                it.email,
                it.wantPortability,
                UUID.randomUUID().toString(),
                it.currentCompany,
                it.status,
                it.creationDate
            )
        )
    }
    return ordersArgs
}

fun orderInformationToArgs(
    orders: List<ActivationPOJO>,
    ordersArgs: ArrayList<OrderInformationArgs>
): ArrayList<OrderInformationArgs> {
    orders.forEach {
        ordersArgs.add(
            OrderInformationArgs(
                it.formId,
                it.planType,
                it.sponsorTeamId,
                it.name,
                it.lastName,
                it.birthDate,
                it.email,
                it.wantPortability,
                it.phoneNumber,
                it.currentCompany,
                it.formStatus,
                it.creationDate
            )
        )
    }
    return ordersArgs
}


fun changeDateFormat(dateStr: String, formatoFin: String, _formatoInit: String?): String {
    var formatoInit = _formatoInit
    if (formatoInit.isNullOrEmpty()) formatoInit = "dd/MM/yyyy"

    val parser = SimpleDateFormat(formatoInit)
    val formatter = SimpleDateFormat(formatoFin)
    return formatter.format(parser.parse(dateStr))
}

fun getPlanType(c_planType: String): String {
    // "example": "Prepaid (prepago)"
    var n_planType: String = ""
    if (c_planType == PLAN_TYPES.POSTPAGO.value) n_planType = "Postpaid (postpago)"
    else if (c_planType == PLAN_TYPES.PREPAGO.value) n_planType = "Prepaid (prepago)"
    else throw Resources.NotFoundException()
    return n_planType
}

fun verifyAvailableNetwork(activity: FragmentActivity): Boolean {
    val connectivityManager =
        activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager != null
}