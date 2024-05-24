package com.blessingmwiti.kotlindarajaapi.di

import com.blessingmwiti.kotlindarajaapi.data.MpesaClient
import com.blessingmwiti.kotlindarajaapi.data.MpesaRepositoryImpl
import com.blessingmwiti.kotlindarajaapi.domain.MpesaRepository
import com.blessingmwiti.kotlindarajaapi.presentation.payment.MpesaViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { MpesaClient.mpesaService }
    single<MpesaRepository> { MpesaRepositoryImpl(get()) }
    viewModel { MpesaViewModel(get()) }
}
