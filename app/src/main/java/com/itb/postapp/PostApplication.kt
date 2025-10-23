package com.itb.postapp

import android.app.Application
import com.itb.postapp.di.appModule
import com.itb.postapp.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class PostApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            val androidContext = androidContext(this@PostApplication)
            modules(
                listOf(
                    appModule,
                    dataModule
                )
            )
        }
    }
}