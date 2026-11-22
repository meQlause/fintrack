package com.meqlause.fintrack.core.common.coroutines

import javax.inject.Qualifier

/** DOC-03: qualifier for the CPU-bound coroutine dispatcher (parsing runs here). Never hardcode it. */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
public annotation class DefaultDispatcher
