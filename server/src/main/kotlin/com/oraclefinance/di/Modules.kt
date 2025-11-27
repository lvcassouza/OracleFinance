package com.oraclefinance.di

import com.oraclefinance.repositories.TransactionsRepository
import com.oraclefinance.repositories.UsersRepository
import org.koin.dsl.module

val modules = module {
    single { UsersRepository() }
    single { TransactionsRepository() }
}
