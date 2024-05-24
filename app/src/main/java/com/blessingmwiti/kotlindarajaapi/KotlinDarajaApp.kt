package com.blessingmwiti.kotlindarajaapi

import android.app.Application
import com.blessingmwiti.kotlindarajaapi.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class KotlinDarajaApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@KotlinDarajaApp)
            modules(appModule)
        }
    }
}