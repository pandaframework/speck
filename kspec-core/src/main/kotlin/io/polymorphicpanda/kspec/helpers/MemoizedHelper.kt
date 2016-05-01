package io.polymorphicpanda.kspec.helpers

import kotlin.reflect.KProperty

/**
 * @author Ranie Jade Ramiso
 */
class MemoizedHelper<T>(val factory: () -> T) {
    private var instance: T? = null

    fun get(): T {
        if (instance == null) {
            instance = factory()
        }
        return instance!!
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = get()

    fun forget() {
        instance = null
    }
}
