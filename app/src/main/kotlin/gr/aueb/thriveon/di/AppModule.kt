package gr.aueb.thriveon.di

import gr.aueb.thriveon.ThriveOnApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

fun initializeKoin(
    application: ThriveOnApplication,
) {
    startKoin {
        androidLogger(Level.DEBUG)

        androidContext(application)

        modules(
            controllersModule,
            dispatchersModule,
            viewModelsModule,
            resourcesModule,
            databaseModule,
            firebaseModule,
            interactorsModule,
            networkModule
        )
    }
}
