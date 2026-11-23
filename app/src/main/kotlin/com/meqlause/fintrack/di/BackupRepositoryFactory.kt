package com.meqlause.fintrack.di

import android.content.Context
import com.meqlause.fintrack.core.backup.FileBackupRepository
import com.meqlause.fintrack.core.common.coroutines.IoDispatcher
import com.meqlause.fintrack.core.common.time.ClockProvider
import com.meqlause.fintrack.core.domain.port.BackupFiles
import com.meqlause.fintrack.core.domain.port.BackupStore
import com.meqlause.fintrack.core.domain.repository.BackupRepository
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/** FR-110, FR-111, DOC-11: builds the encrypted backup repository and stamps it with the installed version name. */
internal class BackupRepositoryFactory
    @Inject
    constructor(
        private val store: BackupStore,
        private val settings: SettingsRepository,
        private val files: BackupFiles,
        private val clock: ClockProvider,
        @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
        @param:ApplicationContext private val context: Context,
    ) {
        /** FR-110: the backup repository used by export and restore. */
        fun create(): BackupRepository =
            FileBackupRepository(
                store = store,
                settings = settings,
                files = files,
                clock = clock,
                ioDispatcher = ioDispatcher,
                appVersionName = installedVersionName(),
            )

        private fun installedVersionName(): String =
            context.packageManager
                .getPackageInfo(context.packageName, 0)
                .versionName
                .orEmpty()
    }
