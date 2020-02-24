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
                    if (t.status == 0 && t.code == "0000000000") {
                        userHasOrders = t.data.count() > 0
                        lstOrder.value = t.data//.formsInformation
                        loading.value = true
                        loadError.value = false
                    } else if (t.status == 2) {
                        if (t.code == "1111111111" || t.code == "2222222222" || t.code == "2050001001" || t.code == "2050001002") {
                            // error del servidor HTTP estado 500
                            errorMessage = t.message
                        } else if (t.code == "0040400000" || t.code == "0044000000" || t.code == "0040100000") {
                            // error del servidor HTTP estado 400 Token
                            // REFRESH TOKEN
                            errorMessage = t.message
                        } else if (t.code == "2040001001" || t.code == "2040001002" || t.code == "2040401001") {
                            // error del servidor HTTP estado 400
                            errorMessage = t.message
                        }

                        loadError.value = true
                        loading.value = false
                    }
                }

                override fun onError(e: Throwable) {
                    loadError.value = true
                    loading.value = false
                    //errorMessage = instanceHttpError(e).message
                }
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}