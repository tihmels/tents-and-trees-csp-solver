package de.tihmels

import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import kotlin.reflect.full.companionObject

interface Logging

inline fun <reified T : Logging> T.logger(): Logger = getLogger(getClassForLogging(T::class.java))

fun <T : Any> getClassForLogging(javaClass: Class<T>): Class<*> {
    return javaClass.enclosingClass?.takeIf {
        it.kotlin.companionObject?.java == javaClass
    } ?: javaClass
}