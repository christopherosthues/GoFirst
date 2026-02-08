package org.darchacheron.gofirst.di

import org.darchacheron.gofirst.play.PlayViewModel
import org.darchacheron.gofirst.settings.SettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule =
    module {
        viewModelOf(::PlayViewModel)
        viewModelOf(::SettingsViewModel)
    }
