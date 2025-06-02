package gr.aueb.thriveon.di

import gr.aueb.thriveon.core.controllers.RealmKeyController
import gr.aueb.thriveon.core.controllers.RealmKeyControllerImpl
import gr.aueb.thriveon.core.controllers.encryptedSharedPrefs.PrefsController
import gr.aueb.thriveon.core.controllers.encryptedSharedPrefs.PrefsControllerImpl
import gr.aueb.thriveon.core.provider.PrefsRealmProvider
import gr.aueb.thriveon.core.provider.PrefsRealmProviderImpl
import org.koin.dsl.module

val controllersModule = module {
    single<PrefsController> {
        PrefsControllerImpl(get())
    }

    single<PrefsRealmProvider> {
        PrefsRealmProviderImpl(get())
    }

    single<RealmKeyController> {
        RealmKeyControllerImpl(get())
    }
}
