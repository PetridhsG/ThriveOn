package gr.aueb.thriveon.di

import gr.aueb.thriveon.core.dispatchers.ThriveOnDispatchers
import gr.aueb.thriveon.domain.interactors.AuthInteractor
import gr.aueb.thriveon.domain.interactors.AuthInteractorImpl
import gr.aueb.thriveon.domain.interactors.DailyTaskInteractor
import gr.aueb.thriveon.domain.interactors.DailyTaskInteractorImpl
import gr.aueb.thriveon.domain.interactors.NotificationsInteractor
import gr.aueb.thriveon.domain.interactors.NotificationsInteractorImpl
import gr.aueb.thriveon.domain.interactors.PostInteractor
import gr.aueb.thriveon.domain.interactors.PostInteractorImpl
import gr.aueb.thriveon.domain.interactors.PrivateTaskInteractor
import gr.aueb.thriveon.domain.interactors.PrivateTaskInteractorImpl
import gr.aueb.thriveon.domain.interactors.SearchInteractor
import gr.aueb.thriveon.domain.interactors.SearchInteractorImpl
import gr.aueb.thriveon.domain.interactors.UserInteractor
import gr.aueb.thriveon.domain.interactors.UserInteractorImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val interactorsModule = module {
    single<DailyTaskInteractor> {
        DailyTaskInteractorImpl(get(), get(), get())
    }

    single<PostInteractor> {
        PostInteractorImpl(get(), get())
    }

    single<SearchInteractor> {
        SearchInteractorImpl(get(), get())
    }

    single<UserInteractor> {
        UserInteractorImpl(get(), get())
    }

    single<PrivateTaskInteractor> {
        PrivateTaskInteractorImpl(
            get(),
            get(named(ThriveOnDispatchers.IO))
        )
    }

    single<AuthInteractor> {
        AuthInteractorImpl(get(), get(), get())
    }

    single<NotificationsInteractor> {
        NotificationsInteractorImpl(get(), get())
    }
}
