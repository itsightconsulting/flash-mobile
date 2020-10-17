package isdigital.veridium.flash.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crashlytics.android.Crashlytics
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import isdigital.veridium.flash.FlashApplication
import isdigital.veridium.flash.model.dto.BestFingers
import isdigital.veridium.flash.model.dto.ReniecUser
import isdigital.veridium.flash.model.generic.ApiResponse
import isdigital.veridium.flash.preferences.UserPrefs
import isdigital.veridium.flash.service.component.DaggerBiometricComponent
import isdigital.veridium.flash.service.module.BiometricService
import javax.inject.Inject

class BiometricViewModel : ViewModel() {

    @Inject
    lateinit var api: BiometricService
    private val disposable = CompositeDisposable()
    val loading = MutableLiveData<Boolean>()
    val loadError = MutableLiveData<Boolean>()
    var responseCode: String = "" // = MutableLiveData<String>() // responseCode

    init {
        DaggerBiometricComponent.create().inject(this)
    }

    fun storeBestFingerprintsByDni(dni: String) {
        this.loading.value = false
        val body = HashMap<String, String>()
        body["dni"] = dni

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
                            if (t.status == 1) {
                                sendToCrashlyticsFingerprintsFail(t.toString())
                            }
                            loadError.value = true
                            loading.value = true
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.message?.let {
                            sendToCrashlyticsFingerprintsFail(it)
                        }
                        loadError.value = true
                        loading.value = true
                        e.printStackTrace()
                    }
                })
        )
    }

    fun validateVeridiumFingerprints(body: HashMap<String, String>) {
        //Log.d("validateVeridiu", body.toString())
        disposable.add(
            api.validateFingerprints(body).subscribeOn(
                Schedulers.newThread()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ApiResponse<ReniecUser>>() {
                    override fun onSuccess(t: ApiResponse<ReniecUser>) {
                        Log.d("validateVeridiu", t.toString())
                        val success: Boolean = (t.status.toInt() == 0 && t.code == "0000000000")
                        if (success) {
                            loadError.value = false
                            loading.value = true
                        } else {

                            if (t.status == 1 || t.code == "0044000000") {
                                sendToCrashlyticsFingerprintsFail(t.toString())
                            }
                            //if (t.status == 2) {
                            //if (t.code == "0044000000") {//TOKEN EXPIRED
                            //sendToCrashlyticsFingerprintsFail(t.toString())
                            //}
                            //else {
                            responseCode = t.code; //== "1020001001"; //máximo de intentos
                            //}
                            //}
                            loadError.value = true
                            loading.value = true
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.message?.let {
                            sendToCrashlyticsFingerprintsFail(it)
                        }
                        loadError.value = true
                        loading.value = true
                        e.printStackTrace()
                    }
                })
        )
    }

    fun sendToCrashlyticsFingerprintsFail(response: String) {
        Crashlytics.logException(
            RuntimeException(
                "Error, ${System.nanoTime()}: $response"
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}