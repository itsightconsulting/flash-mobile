package pe.mobile.cuy.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import pe.mobile.cuy.service.module.ActivationService
import javax.inject.Inject

class ActivationViewModel(application: Application) : BaseViewModel(application) {

    private val composite = CompositeDisposable()

    @Inject
    lateinit var activationService: ActivationService

    val loading = MutableLiveData<Boolean>()
    val loadError = MutableLiveData<Boolean>()
    var errorMessage: String = ""


}