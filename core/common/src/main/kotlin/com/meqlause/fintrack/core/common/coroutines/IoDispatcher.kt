package com.meqlause.fintrack.core.common.coroutines

import javax.inject.Qualifier

/** DOC-03: qualifier for the I/O coroutine dispatcher (database, file and backup work). Never hardcode it. */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
public annotation class IoDispatcher
