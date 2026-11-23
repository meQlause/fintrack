package com.meqlause.fintrack.category

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEmpty
import com.meqlause.fintrack.core.domain.category.DefaultCategory
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class DefaultCategoryNamesTest {
    private val names = DefaultCategoryNames(ApplicationProvider.getApplicationContext<Context>())

    // FR-004 AC: the default categories are seeded with their Indonesian names from the string resources (DOC-02)
    @Test
    fun `given default categories when named then the Indonesian name is read from the resources (FR-004)`() {
        assertThat(names.nameOf(DefaultCategory.FOOD_AND_DRINK)).isEqualTo("Makanan & Minuman")
        assertThat(names.nameOf(DefaultCategory.OTHER_INCOME)).isEqualTo("Lainnya")
    }

    @Test
    fun `given every default category when named then each has a non-blank name (FR-004)`() {
        val all = DefaultCategory.entries.map { names.nameOf(it) }

        assertThat(all).hasSize(DefaultCategory.entries.size)
        all.forEach { assertThat(it).isNotEmpty() }
    }
}
