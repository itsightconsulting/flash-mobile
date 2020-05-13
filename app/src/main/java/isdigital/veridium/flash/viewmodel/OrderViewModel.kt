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
import isdigital.veridium.flash.model.dto.ResponseVerifyDNI
import isdigital.veridium.flash.model.pojo.ActivationPOJO
import isdigital.veridium.flash.preferences.UserPrefs
import isdigital.veridium.flash.service.component.DaggerOrderComponent
import isdigital.veridium.flash.service.module.OrderService
import isdigital.veridium.flash.util.ERROR_TYPES
import isdigital.veridium.flash.util.GENERIC_ERROR_MESSAGE
import isdigital.veridium.flash.util.manageCode
import javax.inject.Inject

class OrderViewModel(application: Application) : BaseViewModel(application) {

    private val disposable = CompositeDisposable()

    @Inject
    lateinit var orderService: OrderService

    var lstOrder = MutableLiveData<List<ActivationPOJO>>()
    val loading = MutableLiveData<Boolean>()
    val loadError = MutableLiveData<Boolean>()
    var userHasOrders: Boolean = false
    var errorMessage: String = ""
    var refreshToken = MutableLiveData<Boolean>()
    var cantRefreshToken: Int = 0


    init {
        DaggerOrderComponent.create().inject(this)
    }

    fun getAllByDni(dni: String) {
        UserPrefs.putUserDni(FlashApplication.appContext, dni)

        loading.value = false

        disposable.add(
            orderService.findAllByDNI(dni).subscribeOn(Schedulers.newThread()).observeOn(
                AndroidSchedulers.mainThread()
            ).subscribeWith(object : DisposableSingleObserver<ResponseVerifyDNI>() {
                override fun onSuccess(t: ResponseVerifyDNI) {
                    var estado = false
                    refreshToken.value = false

                    if (t.status == 0 && (t.code == "0000000000" || t.code == "2040401001")) {
                        if (t.data.maximumActivationsCompletedStateReached) {
                            estado = true
                            errorMessage =
                                "Has alcanzado las 5 activaciones por DNI. Ingresa un DNI diferente"
                        } else {
                            userHasOrders = t.data.pendingActivations.count() > 0
                            lstOrder.value = t.data.pendingActivations
                            loading.value = true
                            loadError.value = false
                        }
                    } else if (t.status == 2) {
                        sendToCrashlyticsFailResponseBody("$t")

                        val errorType = manageCode(t.code)
                        if (errorType == ERROR_TYPES.TOKEN.value) {
                            cantRefreshToken += 1
                            refreshToken.value = true
                        } else
                            errorMessage = t.message

                        estado = true
                        Log.d("ERRORONE", t.toString())
                    } else {
                        Log.d("ERRORTWO", t.toString())
                        estado = true
                        errorMessage = "Ocurrio, un error no esperado."
                    }


                    if (estado) {
                        loadError.value = estado
                        loading.value = false
                    }
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    sendToCrashlyticsFailResponseBody("DNI: $dni")
                    refreshToken.value = false
                    loadError.value = true
                    loading.value = false
                    errorMessage = GENERIC_ERROR_MESSAGE //e.message.toString()
                    // instanceHttpError(e).message
                }
            })
        )
    }

    fun sendToCrashlyticsFailResponseBody(response: String){
        Crashlytics.logException(RuntimeException(
            "Method.getAllByDni(dni: String), ${System.nanoTime()}: $response"))
    }


    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}