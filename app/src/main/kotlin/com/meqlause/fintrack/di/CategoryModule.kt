package com.meqlause.fintrack.di

import android.content.Context
import com.meqlause.fintrack.category.DefaultCategoryNames
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.domain.usecase.SeedDefaultCategoriesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.time.Clock

/** FR-004: seeds the default categories with their Indonesian names from the app string resources. */
@Module
@InstallIn(SingletonComponent::class)
object CategoryModule {
    @Provides
    fun provideSeedDefaultCategories(
        categories: CategoryRepository,
        clock: Clock,
        @ApplicationContext context: Context,
    ): SeedDefaultCategoriesUseCase =
        SeedDefaultCategoriesUseCase(categories, DefaultCategoryNames(context)::nameOf, clock)
}
