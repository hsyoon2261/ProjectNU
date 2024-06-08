package org.example.projectnu.common.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Caller(val requestType: KClass<*>, val responseType: KClass<*>)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Callee(val requestType: KClass<*>, val responseType: KClass<*>)
