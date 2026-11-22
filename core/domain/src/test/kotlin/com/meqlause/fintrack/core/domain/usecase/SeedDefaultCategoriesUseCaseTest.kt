package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

/** DOC-02, FR-004: the default categories are seeded once, with the kinds and keys from the 02 table. */
class SeedDefaultCategoriesUseCaseTest {
    private val repository = InMemoryCategoryRepository()
    private val clock = Clock.fixed(Instant.parse("2026-11-23T00:00:00Z"), ZoneOffset.UTC)
    private val names = FixedDefaultCategoryNames(TABLE.associate { (category, expected) -> category to expected.name })
    private val seed = SeedDefaultCategoriesUseCase(repository, names, clock)

    /** FR-004: an empty category table receives every default with its name, kind and keys. */
    @Test
    fun `given no categories when seeding then every default is inserted with its name kind and keys (FR-004)`() =
        runBlocking {
            seed()

            assertThat(seededRows()).containsExactly(*TABLE.map { it.second }.toTypedArray())
        }

    /** FR-004: default categories are default, active and listed in table order. */
    @Test
    fun `given no categories when seeding then defaults are active and in manual order (FR-004)`() =
        runBlocking {
            seed()

            val seeded = repository.observeAll().first()
            assertThat(seeded.map { it.state }).isEqualTo(List(TABLE.size) { CategoryState(true, false) })
            assertThat(seeded.map { it.profile.sortOrder }).isEqualTo((0 until TABLE.size).toList())
            assertThat(seeded.map { it.audit.createdAt }.distinct()).isEqualTo(listOf(clock.instant()))
        }

    /** FR-004: a second run finds the categories already there and changes nothing. */
    @Test
    fun `given seeded categories when seeding again then nothing changes (FR-004)`() =
        runBlocking {
            seed()
            val firstRun = repository.observeAll().first()

            seed()

            assertThat(repository.observeAll().first()).isEqualTo(firstRun)
        }

    /** FR-004: categories that already exist, for example created by the user, are never supplemented. */
    @Test
    fun `given an existing category when seeding then no default is inserted (FR-004)`() =
        runBlocking {
            val custom =
                Category(
                    categoryId = CategoryId(0L),
                    profile = CategoryProfile("Kustom", CategoryKind.EXPENSE, Appearance("star", "teal"), 0),
                    state = CategoryState(isDefault = false, isArchived = false),
                    audit = Audit(clock.instant(), clock.instant()),
                )
            repository.insert(custom)

            seed()

            assertThat(seededRows()).containsExactly(Row("Kustom", CategoryKind.EXPENSE, "star", "teal"))
        }

    private suspend fun seededRows(): List<Row> =
        repository.observeAll().first().map {
            Row(it.profile.name, it.profile.kind, it.profile.appearance.iconKey, it.profile.appearance.colorKey)
        }

    private data class Row(
        val name: String,
        val kind: CategoryKind,
        val iconKey: String,
        val colorKey: String,
    )

    private class FixedDefaultCategoryNames(
        private val names: Map<DefaultCategory, String>,
    ) : DefaultCategoryNames {
        override fun nameOf(category: DefaultCategory): String = checkNotNull(names[category])
    }

    private companion object {
        // DOC-02, FR-004: the default categories table, in manual order
        val TABLE: List<Pair<DefaultCategory, Row>> =
            listOf(
                DefaultCategory.FOOD_AND_DRINK to Row("Makanan & Minuman", CategoryKind.EXPENSE, "food", "orange"),
                DefaultCategory.TRANSPORT to Row("Transportasi", CategoryKind.EXPENSE, "transport", "blue"),
                DefaultCategory.SHOPPING to Row("Belanja", CategoryKind.EXPENSE, "shopping", "pink"),
                DefaultCategory.BILL to Row("Tagihan", CategoryKind.EXPENSE, "bill", "purple"),
                DefaultCategory.ENTERTAINMENT to Row("Hiburan", CategoryKind.EXPENSE, "entertainment", "violet"),
                DefaultCategory.HEALTH to Row("Kesehatan", CategoryKind.EXPENSE, "health", "red"),
                DefaultCategory.EDUCATION to Row("Pendidikan", CategoryKind.EXPENSE, "education", "indigo"),
                DefaultCategory.HOME to Row("Rumah", CategoryKind.EXPENSE, "home", "amber"),
                DefaultCategory.PHONE_AND_INTERNET to Row("Pulsa & Internet", CategoryKind.EXPENSE, "phone", "cyan"),
                DefaultCategory.SOCIAL_AND_EVENTS to Row("Sosial & Kondangan", CategoryKind.EXPENSE, "gift", "rose"),
                DefaultCategory.DONATION_AND_ZAKAT to Row("Donasi & Zakat", CategoryKind.EXPENSE, "charity", "green"),
                DefaultCategory.FEES_AND_ADMIN to Row("Biaya & Admin", CategoryKind.EXPENSE, "fee", "slate"),
                DefaultCategory.OTHER_EXPENSE to Row("Lainnya", CategoryKind.EXPENSE, "other", "gray"),
                DefaultCategory.SALARY to Row("Gaji", CategoryKind.INCOME, "salary", "green"),
                DefaultCategory.BONUS to Row("Bonus", CategoryKind.INCOME, "bonus", "teal"),
                DefaultCategory.BUSINESS to Row("Usaha / Freelance", CategoryKind.INCOME, "business", "blue"),
                DefaultCategory.GIFT_INCOME to Row("Hadiah", CategoryKind.INCOME, "gift", "rose"),
                DefaultCategory.OTHER_INCOME to Row("Lainnya", CategoryKind.INCOME, "other", "gray"),
            )
    }
}
