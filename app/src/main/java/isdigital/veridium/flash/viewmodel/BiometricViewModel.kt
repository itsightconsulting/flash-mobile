package isdigital.veridium.flash.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import isdigital.veridium.flash.FlashApplication
import isdigital.veridium.flash.model.dto.BestFingers
import isdigital.veridium.flash.model.generic.ApiResponse
import isdigital.veridium.flash.preferences.UserPrefs
import isdigital.veridium.flash.service.component.DaggerBiometricComponent
import isdigital.veridium.flash.service.module.BiometricService
import javax.inject.Inject

class BiometricViewModel : ViewModel() {

    private val tempHostName = "AAA111BBB222CCC333"

    @Inject
    lateinit var api: BiometricService
    private val disposable = CompositeDisposable()
    val loading = MutableLiveData<Boolean>()
    val loadError = MutableLiveData<Boolean>()

    init {
        DaggerBiometricComponent.create().inject(this)
    }

    fun storeBestFingerprintsByDni(dni: String) {
        this.loading.value = false
        val body = HashMap<String, String>()
        body["dni"] = dni
        body["hostName"] = tempHostName
        disposable.add(
            api.getBestFingerprintsByDni(body).subscribeOn(
                Schedulers.newThread()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ApiResponse<BestFingers>>() {
                    override fun onSuccess(t: ApiResponse<BestFingers>) {
                        val success: Boolean = t.status == 0
                        if (success) {
                            loadError.value = false
                            loading.value = true
                            UserPrefs.putBestFingerPrints(FlashApplication.appContext, t.data)
                        } else {
                            loadError.value = true
                            loading.value = true
                        }
                    }

                    override fun onError(e: Throwable) {
                        loadError.value = true
                        loading.value = true
                        e.printStackTrace()
                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}