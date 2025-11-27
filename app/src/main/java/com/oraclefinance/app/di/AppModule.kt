package com.oraclefinance.app.di

import com.oraclefinance.app.data.api.ApiService
import com.oraclefinance.app.data.repository.AuthRepository
import com.oraclefinance.app.data.repository.FinanceRepository
import com.oraclefinance.app.viewmodel.HomeViewModel
import com.oraclefinance.app.viewmodel.LoginViewModel
import io.ktor.client.HttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.oraclefinance.app.BuildConfig

val appModule = module {
    single { ApiService(get<HttpClient>(), BuildConfig.BASE_URL) }
    single { AuthRepository(get(), get()) }
    single { FinanceRepository(get()) }

    viewModel { LoginViewModel(get()) }
    viewModel { HomeViewModel(get(), get()) }
}
