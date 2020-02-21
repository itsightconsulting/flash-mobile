package isdigital.veridium.flash.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import isdigital.veridium.flash.model.dto.ResponseVerifyDNI
import isdigital.veridium.flash.service.component.DaggerOrderComponent
import isdigital.veridium.flash.service.module.OrderService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import isdigital.veridium.flash.model.pojo.ActivationPOJO
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
        loading.value = false

        disposable.add(
            orderService.findAllByDNI(dni).subscribeOn(Schedulers.newThread()).observeOn(
                AndroidSchedulers.mainThread()
            ).subscribeWith(object : DisposableSingleObserver<ResponseVerifyDNI>() {
                override fun onSuccess(t: ResponseVerifyDNI) {
                    if (t.status == 0) {
                        userHasOrders = t.data.count() > 0
                        lstOrder.value = t.data//.formsInformation
                        loading.value = true
                        loadError.value = false
                    }else if (t.status == 2) {
                        errorMessage = t.message
                        loading.value = true
                        loadError.value = true
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