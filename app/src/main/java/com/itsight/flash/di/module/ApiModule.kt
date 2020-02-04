package com.itsight.flash.di.module

import dagger.Module


@Module
class ApiModule {

    init {

    }

    /*@Provides
    fun restApiUser(): UserApi {
        return Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(UserApi::class.java)
    }

    @Provides
    fun restApiSellerSale(): SellerSaleApi{
        return ServiceManager().createService(SellerSaleApi::class.java)
    }*/
}