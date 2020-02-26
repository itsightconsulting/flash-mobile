package isdigital.veridium.flash.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
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
import isdigital.veridium.flash.util.API_PASSWORD
import isdigital.veridium.flash.util.API_USERNAME
import isdigital.veridium.flash.util.ERROR_TYPES
import isdigital.veridium.flash.util.manageCode
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
                        //val success: Boolean = t.status == 0
                        val success: Boolean = (t.status == 0 && t.code == "0000000000")
                        if (success) {
                            formError.value = false
                            loading.value = true
                        } else {
                            val errorType = manageCode(t.code)
                            if (errorType == ERROR_TYPES.TOKEN.value) {
                                errorMessage = t.message
                            } else {
                                errorMessage = t.message
                            }
                            formError.value = true
                            loading.value = true
                        }
                    }
                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        formError.value = true
                        loading.value = true
                        errorMessage =
                            FlashApplication.appContext.resources.getString(R.string.api_generic_error)
                    }
                })
        )
    }

    fun checkIccidValid(iccid: String) {
        loading.value = false
        disposable.add(activationService.validateICCID(iccid).subscribeOn(
            Schedulers.newThread()
        ).observeOn(AndroidSchedulers.mainThread()).subscribeWith(
            object : DisposableSingleObserver<VerifyIccidResponse>() {
                override fun onSuccess(t: VerifyIccidResponse) {
                    loadError.value = false
                    loading.value = true
                }

                override fun onError(e: Throwable) {
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

        ServiceManager().createService(TokenApi::class.java).getToken(bodyToken).enqueue(
            object : retrofit2.Callback<ApiResponse<Token>> {
                override fun onFailure(call: Call<ApiResponse<Token>>, t: Throwable) {
                    errorMessage = "Obtención del token fallida"
                    loading.value = true;
                    loadError.value = true;
                }

                override fun onResponse(
                    call: Call<ApiResponse<Token>>,
                    response: Response<ApiResponse<Token>>
                ) {
                    api_token = response.body()!!.data.token
                    loadError.value = false;
                    loading.value = true;
                }
            })
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}