package isdigital.veridium.flash.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import isdigital.veridium.flash.FlashApplication
import isdigital.veridium.flash.R
import isdigital.veridium.flash.api.TokenApi
import isdigital.veridium.flash.configuration.ServiceManager
import isdigital.veridium.flash.model.dto.ConsolidatedDataResponse
import isdigital.veridium.flash.model.dto.Token
import isdigital.veridium.flash.model.dto.VerifyIccidResponse
import isdigital.veridium.flash.model.generic.ApiResponse
import isdigital.veridium.flash.service.component.DaggerActivationComponent
import isdigital.veridium.flash.service.module.ActivationService
import isdigital.veridium.flash.util.*
import retrofit2.Call
import retrofit2.Response
import java.util.*
import javax.inject.Inject

class ActivationViewModel(application: Application) : BaseViewModel(application) {

    private val disposable = CompositeDisposable()

    @Inject
    lateinit var activationService: ActivationService

    val loading = MutableLiveData<Boolean>()
    val loadError = MutableLiveData<Boolean>()
    val formError = MutableLiveData<Boolean>()
    val simActivated = MutableLiveData<Boolean>()
    var responseCode: String = "" // = MutableLiveData<String>() // responseCode
    var responseIccid: String = ""
    var errorMessage: String = ""
    var api_token: String = ""

    init {
        DaggerActivationComponent.create().inject(this)
    }

    fun sendFormWithStatus(form: HashMap<String, String>) {
        loading.value = false
        disposable.add(
            activationService.saveActivationForm(
                form
            )
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ConsolidatedDataResponse>() {
                    override fun onSuccess(t: ConsolidatedDataResponse) {
                        //Log.d("sendFormWithStatus", form.toString())
                        val success: Boolean = (t.status == 0 && t.code == "0000000000")
                        if (success) {
                            formError.value = form["formStatus"] != FORMSTATUS.COMPLETED.value
                            loading.value = true
                        } else {
                            val errorType = manageCode(t.code)
                            errorMessage = if (errorType == ERROR_TYPES.TOKEN.value) {
                                t.message
                            } else {
                                t.message
                            }
                            formError.value = true
                            loading.value = true
                        }
                    }

                    override fun onError(e: Throwable) {
                        sendToCrashlyticsFailRequestBody(form = form.toString())
                        e.printStackTrace()
                        formError.value = true
                        loading.value = true
                        errorMessage =
                            FlashApplication.appContext.resources.getString(R.string.api_generic_error)
                    }
                })
        )
    }

    fun checkIccidValid(iccid: String, form: HashMap<String, String>) {
        //Log.d("checkIccidValid", form.toString())
        loading.value = false
        disposable.add(activationService.validateICCID(iccid, form).subscribeOn(
            Schedulers.newThread()
        ).observeOn(AndroidSchedulers.mainThread()).subscribeWith(
            object : DisposableSingleObserver<VerifyIccidResponse>() {
                override fun onSuccess(t: VerifyIccidResponse) {
                    //Log.d("iccid", iccid.toString())
                    //Log.d("VerifyIccidResponse", t.toString())
                    var success: Boolean = (t.status.toInt() == 0 && t.code == "0000000000")
                    /*if (iccid.get(19) == '0') success = true;*/
                    if (success) {
                        loadError.value = false
                        loading.value = true
                        if (t.data != null)
                            responseIccid = t.data.iccid!!
                    } else {
                        //if (t.status.toInt() == 2) {
                        if (t.code == "0044000000") {//TOKEN EXPIRED
                            sendToCrashlyticsFailRequestBody(form = t.toString())
                        } else {
                            simActivated.value = t.code == "1040001005"; //ICCID REGISTRADO
                            responseCode = t.code; //== "1020001001"; //máximo de intentos
                        }
                        //}
                        loadError.value = true
                        loading.value = true
                        errorMessage = t.message;
                        //FlashApplication.appContext.resources.getString(R.string.api_generic_error)
                    }
                }

                override fun onError(e: Throwable) {
                    sendToCrashlyticsFailRequestBody(form = "ERROR Throwable: ${e.message}")
                    e.printStackTrace()
                    loadError.value = true
                    loading.value = true
                    errorMessage =
                        FlashApplication.appContext.resources.getString(R.string.api_generic_error)
                }
            }
        ))
    }

    fun auth() {
        val bodyToken = HashMap<String, String>()
        bodyToken["username"] = API_USERNAME
        bodyToken["password"] = API_PASSWORD

        ServiceManager().createServiceSimple(TokenApi::class.java).getToken(bodyToken).enqueue(
            object : retrofit2.Callback<ApiResponse<Token>> {
                override fun onFailure(call: Call<ApiResponse<Token>>, t: Throwable) {
                    t.printStackTrace()
                    loadError.value = true
                    errorMessage = "Obtención del token fallida"
                    loading.value = true
                    sendToCrashlyticsTokenFail(errorMessage)
                }

                override fun onResponse(
                    call: Call<ApiResponse<Token>>,
                    response: Response<ApiResponse<Token>>
                ) {
                    val body = response.body()
                    if (body != null) {
                        if (body.status == 0) {//success
                            api_token = body.data.token
                            Log.d("TOKENRESPONSE", api_token)
                            loadError.value = false
                            loading.value = true
                        } else {
                            errorMessage = "Obtención del token fallida"
                            loadError.value = true
                            loading.value = true
                            sendToCrashlyticsTokenFail(body.toString())
                        }
                    } else {
                        errorMessage = "Obtención del token fallida"
                        sendToCrashlyticsTokenFail("Empty body when token endpoint was requested")
                    }
                }
            })
    }

    fun sendToCrashlyticsFailRequestBody(form: String) {
        Crashlytics.logException(
            RuntimeException(
                "Method.sendFormWithStatus(form: HashMap<String, String>), ${System.nanoTime()}: $form"
            )
        )
    }

    fun sendToCrashlyticsTokenFail(response: String) {
        Crashlytics.logException(
            RuntimeException(
                "Method.auth(), ${System.nanoTime()}: $response"
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}