package com.meqlause.fintrack.core.parser

import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/** DOC-07, FR-083: loads every JSON fixture under src/test/resources/fixtures, sorted by path. */
internal object FixtureLoader {
    private const val FIXTURE_ROOT = "fixtures"
    private const val JSON_SUFFIX = ".json"
    private val json = Json { ignoreUnknownKeys = true }

    /** DOC-07: an absent or empty fixtures folder yields no fixtures. */
    fun load(): List<NotificationFixture> {
        val rootUri = javaClass.classLoader.getResource(FIXTURE_ROOT)?.toURI() ?: return emptyList()
        val root = Paths.get(rootUri)
        return Files.walk(root).use { paths ->
            paths
                .filter { it.fileName.toString().endsWith(JSON_SUFFIX) }
                .sorted()
                .map { read(root, it) }
                .toList()
        }
    }

    private fun read(
        root: Path,
        file: Path,
    ): NotificationFixture {
        val relative = root.relativize(file).toString().replace('\\', '/')
        return json.decodeFromString<NotificationFixture>(Files.readString(file)).copy(source = relative)
    }
}
