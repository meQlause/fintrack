package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import kotlinx.serialization.Serializable

// DOC-11: one category row in the backup
@Serializable
internal data class CategoryDto(
    val categoryId: Long,
    val profile: ProfileDto,
    val state: StateDto,
    val audit: AuditDto,
) {
    fun toModel(): Category =
        Category(
            categoryId = CategoryId(categoryId),
            profile = profile.toModel(),
            state = CategoryState(state.isDefault, state.isArchived),
            audit = audit.toModel(),
        )

    companion object {
        fun from(category: Category): CategoryDto =
            CategoryDto(
                categoryId = category.categoryId.value,
                profile = ProfileDto.from(category.profile),
                state = StateDto(category.state.isDefault, category.state.isArchived),
                audit = AuditDto.from(category.audit),
            )
    }

    @Serializable
    internal data class ProfileDto(
        val name: String,
        val kind: String,
        val appearance: AppearanceDto,
        val sortOrder: Int,
    ) {
        fun toModel(): CategoryProfile =
            CategoryProfile(name, BackupValues.enumNamed<CategoryKind>(kind), appearance.toModel(), sortOrder)

        companion object {
            fun from(profile: CategoryProfile): ProfileDto =
                ProfileDto(profile.name, profile.kind.name, AppearanceDto.from(profile.appearance), profile.sortOrder)
        }
    }

    @Serializable
    internal data class StateDto(
        val isDefault: Boolean,
        val isArchived: Boolean,
    )
}
