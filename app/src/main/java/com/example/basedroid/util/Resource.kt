package com.example.basedroid.util

/**
 * kind of abstract class but with sealed class we can define which classes are allowed to
 * inherit from Resource class
 */

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {

    /**
     * Only these classes are allowed to inherit from Resource class
     */
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}