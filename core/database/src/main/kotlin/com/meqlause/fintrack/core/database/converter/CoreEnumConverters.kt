package com.meqlause.fintrack.core.database.converter

import androidx.room.TypeConverter
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletType

// DOC-02: enums are stored in the database as their name, never as their ordinal
internal class CoreEnumConverters {
    @TypeConverter
    fun walletTypeToName(value: WalletType): String = value.name

    @TypeConverter
    fun nameToWalletType(name: String): WalletType = WalletType.valueOf(name)

    @TypeConverter
    fun investKindToName(value: InvestKind?): String? = value?.name

    @TypeConverter
    fun nameToInvestKind(name: String?): InvestKind? = name?.let { InvestKind.valueOf(it) }

    @TypeConverter
    fun categoryKindToName(value: CategoryKind): String = value.name

    @TypeConverter
    fun nameToCategoryKind(name: String): CategoryKind = CategoryKind.valueOf(name)

    @TypeConverter
    fun transactionTypeToName(value: TransactionType): String = value.name

    @TypeConverter
    fun nameToTransactionType(name: String): TransactionType = TransactionType.valueOf(name)

    @TypeConverter
    fun transactionSourceToName(value: TransactionSource): String = value.name

    @TypeConverter
    fun nameToTransactionSource(name: String): TransactionSource = TransactionSource.valueOf(name)

    @TypeConverter
    fun moneyDirectionToName(value: MoneyDirection): String = value.name

    @TypeConverter
    fun nameToMoneyDirection(name: String): MoneyDirection = MoneyDirection.valueOf(name)

    @TypeConverter
    fun inboxStatusToName(value: InboxStatus): String = value.name

    @TypeConverter
    fun nameToInboxStatus(name: String): InboxStatus = InboxStatus.valueOf(name)
}
