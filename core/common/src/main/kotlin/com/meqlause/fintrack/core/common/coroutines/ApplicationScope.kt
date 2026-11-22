package com.meqlause.fintrack.core.common.coroutines

import javax.inject.Qualifier

/** DOC-03: qualifier for the application-lifetime CoroutineScope that outlives screens and services. */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
public annotation class ApplicationScope
