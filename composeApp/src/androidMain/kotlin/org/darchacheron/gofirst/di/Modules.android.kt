package org.darchacheron.gofirst.di

import org.darchacheron.gofirst.settings.AndroidSettingsRepository
import org.darchacheron.gofirst.settings.SettingsRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

actual val platformModule = module {
    single<SettingsRepository> { AndroidSettingsRepository(androidApplication()) }
}