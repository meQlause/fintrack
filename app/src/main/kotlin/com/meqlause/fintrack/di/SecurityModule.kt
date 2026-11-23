package com.meqlause.fintrack.di

import com.meqlause.fintrack.core.domain.security.PinHasher
import com.meqlause.fintrack.core.security.Pbkdf2PinHasher
import com.meqlause.fintrack.security.Pbkdf2PinHashAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// DOC-10: binds the PIN hasher port of core:domain to the PBKDF2 implementation of core:security
@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {
    @Provides
    @Singleton
    fun providePinHasher(): PinHasher = Pbkdf2PinHashAdapter(Pbkdf2PinHasher())
}
