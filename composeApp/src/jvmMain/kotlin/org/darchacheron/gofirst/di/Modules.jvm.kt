package org.darchacheron.gofirst.di

import org.darchacheron.gofirst.settings.JvmSettingsRepository
import org.darchacheron.gofirst.settings.SettingsRepository
import org.koin.dsl.module

actual val platformModule = module {
    single<SettingsRepository> { JvmSettingsRepository() }
}