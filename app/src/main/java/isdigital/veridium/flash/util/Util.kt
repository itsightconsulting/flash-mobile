package isdigital.veridium.flash.util

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Parcel
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import isdigital.veridium.flash.R
import isdigital.veridium.flash.model.dto.ErrorResponse
import isdigital.veridium.flash.model.dto.OrderInformation
import isdigital.veridium.flash.model.parcelable.OrderInformationArgs
import isdigital.veridium.flash.model.pojo.ActivationPOJO
import org.json.JSONObject
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

enum class RULESVAL(val value: Int) {
    REQUIRED(0),
    MIN_LENGTH(1),
    MAX_LENGTH(2),
    EMAIL(3),
    EQUALS_TO(4),
    START_WITH(5)
}

enum class FORMSTATUS(val value: String) {
    COMPLETED("completed"),
    REJECTICCD("rejected iccid"),
    REJECTBIO("rejected bio")
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


enum class ERROR_TYPES(val value: String) {
    SERVER("Server"),
    TOKEN("Token"),
    INTERNO("Interno")
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

fun invokerBarcodeErrorActivado(context: Context): Dialog {
    val dialog = Dialog(context)
    dialog.setContentView(R.layout.info_barcode_activated)
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

fun errorBarCodeValidation(context: Context): Dialog {
    val dialog = Dialog(context)
    dialog.setContentView(R.layout.info_barcode_error_alt)
    dialog.setCanceledOnTouchOutside(false)
    dialog.setCancelable(false)
    dialog.window!!.setLayout(
        (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
        WindowManager.LayoutParams.WRAP_CONTENT
    )
    return dialog
}

fun invokerTermContent(context: Context): Dialog {
    val dialog = Dialog(context)
    dialog.setContentView(R.layout.terms_content)
    dialog.setCanceledOnTouchOutside(false)
    dialog.setCancelable(false)
    dialog.window!!.setLayout(
        (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
        WindowManager.LayoutParams.WRAP_CONTENT
    )
    return dialog
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
                it.paternalLastName,
                it.maternalLastName,
                it.birthDate,
                it.email,
                it.wantPortability,
                UUID.randomUUID().toString(),
                it.currentCompany,
                it.status,
                it.creationDate,
                it.populatedCenter,
                it.coveragePopulatedCenter,
                it.acceptTermsCoveragePopulatedCenter
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
                it.paternalLastName,
                it.maternalLastName,
                it.birthDate,
                it.email,
                it.wantPortability,
                it.phoneNumber,
                it.currentCompany,
                it.formStatus,
                it.creationDate,
                it.populatedCenter,
                it.coveragePopulatedCenter,
                it.acceptTermsCoveragePopulatedCenter
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

fun getPlanType(plantType: String): String {
    return when (plantType) {
        PLAN_TYPES.POSTPAGO.value -> "Postpaid"
        PLAN_TYPES.PREPAGO.value -> "Prepaid"
        else -> throw Resources.NotFoundException()
    }
}

fun verifyAvailableNetwork(activity: FragmentActivity): Boolean {
    val connectivityManager =
        activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}

fun manageCode(code: String): String {
    var errorType = ""
    if (code == "1111111111" || code == "2222222222" || code == "2050001001" || code == "2050001002") {
        // error del servidor HTTP estado 500
        errorType = ERROR_TYPES.SERVER.value
    } else if (code == "0040400000" || code == "0044000000" || code == "0040100000") {
        // error del servidor HTTP estado 400 Token
        errorType = ERROR_TYPES.TOKEN.value
    } else if (code == "2040001001" || code == "2040001002" || code == "2040401001") {
        // error del servidor HTTP estado 400
        errorType = ERROR_TYPES.INTERNO.value
    }
    return errorType
}

fun hideSponsorTeamId(sponsorTeamId: String): String {
    var text_replace: String = "";
    if (sponsorTeamId.length <= 2)
        text_replace = sponsorTeamId.replaceRange(
            0, sponsorTeamId.length, "***"
        );
    else text_replace = sponsorTeamId.replaceRange(
        sponsorTeamId.length - 3, sponsorTeamId.length, "***"
    );
    return text_replace;
}