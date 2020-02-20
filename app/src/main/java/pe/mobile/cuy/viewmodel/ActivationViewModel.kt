package pe.mobile.cuy.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import pe.mobile.cuy.FlashApplication
import pe.mobile.cuy.R
import pe.mobile.cuy.model.dto.ConsolidatedDataResponse
import pe.mobile.cuy.model.dto.VerifyIccidResponse
import pe.mobile.cuy.model.pojo.ActivationPOJO
import pe.mobile.cuy.service.component.DaggerActivationComponent
import pe.mobile.cuy.service.module.ActivationService
import javax.inject.Inject

class ActivationViewModel(application: Application) : BaseViewModel(application) {

    private val disposable = CompositeDisposable()

    @Inject
    lateinit var activationService: ActivationService

    val loading = MutableLiveData<Boolean>()
    val loadError = MutableLiveData<Boolean>()
    val formError = MutableLiveData<Boolean>()
    var errorMessage: String = ""

    init {
        DaggerActivationComponent.create().inject(this)
    }

    fun sendFormWithStatus(form: ActivationPOJO) {
        loading.value = false
        disposable.add(
            activationService.saveActivationForm(
                form.formId,
                form.dni,
                form.formStatus!!,
                form.iccid!!,
                form.formCreationDate!!,
                form.name,
                form.lastName,
                form.birthDate,
                form.email,
                form.sponsorTeamId,
                form.wantPortability,
                form.phoneNumber,
                form.planType,
                form.validationBiometric!!,
                form.formCreationDate
            )
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ConsolidatedDataResponse>() {
                    override fun onSuccess(t: ConsolidatedDataResponse) {
                        formError.value = false
                        loading.value = true
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

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}