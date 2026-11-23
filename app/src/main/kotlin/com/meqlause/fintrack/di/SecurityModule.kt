package com.meqlause.fintrack.di

import android.content.Context
import com.meqlause.fintrack.core.domain.security.BiometricAvailability
import com.meqlause.fintrack.core.domain.security.PinHasher
import com.meqlause.fintrack.core.security.Pbkdf2PinHasher
import com.meqlause.fintrack.security.DeviceBiometricAvailability
import com.meqlause.fintrack.security.Pbkdf2PinHashAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// DOC-10, FR-101: binds the PIN hasher and the biometric check of core:domain to their Android implementations
@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {
    @Provides
    @Singleton
    fun providePinHasher(): PinHasher = Pbkdf2PinHashAdapter(Pbkdf2PinHasher())

    @Provides
    @Singleton
    fun provideBiometricAvailability(
        @ApplicationContext context: Context,
    ): BiometricAvailability = DeviceBiometricAvailability(context)
}
