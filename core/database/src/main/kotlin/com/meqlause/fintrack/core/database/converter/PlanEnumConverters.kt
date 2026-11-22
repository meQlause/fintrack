package com.meqlause.fintrack.core.database.converter

import androidx.room.TypeConverter
import com.meqlause.fintrack.core.model.InstallmentStatus
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanStatus

// DOC-02: installment and loan enums are stored in the database as their name, never as their ordinal
internal class PlanEnumConverters {
    @TypeConverter
    fun installmentStatusToName(value: InstallmentStatus): String = value.name

    @TypeConverter
    fun nameToInstallmentStatus(name: String): InstallmentStatus = InstallmentStatus.valueOf(name)

    @TypeConverter
    fun loanDirectionToName(value: LoanDirection): String = value.name

    @TypeConverter
    fun nameToLoanDirection(name: String): LoanDirection = LoanDirection.valueOf(name)

    @TypeConverter
    fun loanStatusToName(value: LoanStatus): String = value.name

    @TypeConverter
    fun nameToLoanStatus(name: String): LoanStatus = LoanStatus.valueOf(name)
}
