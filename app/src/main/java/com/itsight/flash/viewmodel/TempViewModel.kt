package com.itsight.flash.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devtides.dogs.model.DogsApiService
import com.itsight.flash.model.DogBreed
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class TempViewModel: ViewModel() {

    val disposable = CompositeDisposable()
    val loading = MutableLiveData<Boolean>()

    init {

    }

    fun test(){
        loading.value = false
        disposable.add(DogsApiService().getDogs().subscribeOn(Schedulers.newThread()).
            observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<List<DogBreed>>() {

                override fun onSuccess(dogList: List<DogBreed>) {
                    loading.value = true
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

            })
        )
    }


    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}