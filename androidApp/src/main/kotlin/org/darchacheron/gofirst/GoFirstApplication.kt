package org.darchacheron.gofirst

import android.app.Application
import org.darchacheron.gofirst.di.initKoin
import org.koin.android.ext.koin.androidContext

class GoFirstApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@GoFirstApplication)
        }
    }
}