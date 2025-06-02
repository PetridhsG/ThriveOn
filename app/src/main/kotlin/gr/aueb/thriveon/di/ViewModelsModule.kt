package gr.aueb.thriveon.di

import gr.aueb.thriveon.ui.screens.feed.FeedViewModel
import gr.aueb.thriveon.ui.screens.home.HomeViewModel
import gr.aueb.thriveon.ui.screens.editProfile.EditProfileViewModel
import gr.aueb.thriveon.ui.screens.friends.FriendsViewModel
import gr.aueb.thriveon.ui.screens.profile.ProfileViewModel
import gr.aueb.thriveon.ui.screens.titles.TitlesViewModel
import gr.aueb.thriveon.ui.screens.search.SearchViewModel
import gr.aueb.thriveon.ui.screens.signIn.SignInViewModel
import gr.aueb.thriveon.ui.screens.categories.CategoriesViewModel
import gr.aueb.thriveon.ui.screens.notifications.NotificationsViewModel
import gr.aueb.thriveon.ui.screens.progress.ProgressViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { SignInViewModel(get(), get(), get()) }
    viewModel { CategoriesViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { FeedViewModel(get(), get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { NotificationsViewModel(get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { FriendsViewModel(get()) }
    viewModel { TitlesViewModel(get()) }
    viewModel { ProgressViewModel(get()) }
    viewModel { EditProfileViewModel(get(), get()) }
}
