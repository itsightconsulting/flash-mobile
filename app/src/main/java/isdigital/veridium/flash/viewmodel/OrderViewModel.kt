package isdigital.veridium.flash.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
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
                    refreshToken.value = false;

                    if (t.status == 0 && t.code == "0000000000") {
                        if (t.data.count() < 6) {
                            userHasOrders = t.data.count() > 0
                            lstOrder.value = t.data//.formsInformation
                            loading.value = true
                            loadError.value = false
                        } else {
                            estado = true
                            errorMessage =
                                "Has alcanzado las 5 activaciones por DNI. Ingresa un DNI diferente"
                        }
                    } else if (t.status == 2) {
                        val errorType = manageCode(t.code)
                        if (errorType == ERROR_TYPES.TOKEN.value) {
                            cantRefreshToken += 1
                            refreshToken.value = true
                        } else {
                            errorMessage = t.message
                        }
                        estado = true
                    }


                    if (dni.startsWith("44")) {
                        estado = false
                        userHasOrders = false
                        loading.value = true
                        loadError.value = false
                    }

                    if (estado) {
                        loadError.value = estado
                        loading.value = false
                    }
                }

                override fun onError(e: Throwable) {
                    loadError.value = true
                    loading.value = false
                    errorMessage = e.message.toString()
                    // instanceHttpError(e).message
                }
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}