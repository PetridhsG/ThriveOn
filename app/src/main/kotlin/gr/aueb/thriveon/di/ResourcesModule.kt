package gr.aueb.thriveon.di

import gr.aueb.thriveon.core.resources.ResourceProvider
import gr.aueb.thriveon.core.resources.ResourceProviderImpl
import org.koin.dsl.module

val resourcesModule = module {
    single<ResourceProvider> {
        ResourceProviderImpl(get())
    }
}
