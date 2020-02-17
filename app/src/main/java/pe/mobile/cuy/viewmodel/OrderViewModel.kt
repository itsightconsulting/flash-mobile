package pe.mobile.cuy.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import pe.mobile.cuy.model.dto.OrderInformation
import pe.mobile.cuy.model.dto.ResponseVerifyDNI
import pe.mobile.cuy.service.component.DaggerOrderComponent
import pe.mobile.cuy.service.module.OrderService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class OrderViewModel(application: Application) : BaseViewModel(application) {

    private val disposable = CompositeDisposable()

    @Inject
    lateinit var orderService: OrderService

    var lstOrder = MutableLiveData<List<OrderInformation>>()
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
                    userHasOrders = true
                    lstOrder.value = t.data.formsInformation
                    loading.value = true
                    loadError.value = false
                }

                override fun onError(e: Throwable) {
                    userHasOrders = false
                    lstOrder.value = arrayListOf(
                        OrderInformation(
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            true,
                            UUID.randomUUID().toString(),
                            "",
                            "",
                            ""
                        )
                    )
                    loading.value = true
                    loadError.value = false

                    /*errorMessage = instanceHttpError(e).message
                    loadError.value = true*/
                }
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}