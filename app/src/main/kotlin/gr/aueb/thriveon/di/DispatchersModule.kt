package gr.aueb.thriveon.di

import gr.aueb.thriveon.core.dispatchers.ThriveOnDispatchers
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

val dispatchersModule = module {
    single<CoroutineContext>(
        qualifier = named(ThriveOnDispatchers.Main)
    ) {
        Dispatchers.Main
    }

    single<CoroutineContext>(
        qualifier = named(ThriveOnDispatchers.Default)
    ) {
        Dispatchers.Default
    }

    single<CoroutineContext>(
        qualifier = named(ThriveOnDispatchers.IO)
    ) {
        Dispatchers.IO
    }
}
