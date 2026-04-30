package com.example.apptrix.di

import com.example.service.AuthService
import com.example.service.BiometricRepositoryImpl
import com.example.service.repository.AuthInterface
import com.example.service.repository.BiometricRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindAuthRepository(
        impl: AuthService,
    ): AuthInterface

    companion object {
        @Provides
        @Singleton
        fun provideBiometricRepository(): BiometricRepository {
            return BiometricRepositoryImpl()
        }
    }
}
