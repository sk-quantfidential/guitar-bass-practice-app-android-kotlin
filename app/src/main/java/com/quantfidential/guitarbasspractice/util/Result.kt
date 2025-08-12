package com.quantfidential.guitarbasspractice.util

/**
 * A generic wrapper for handling success and error states.
 * Provides a type-safe way to handle operations that can fail.
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: AppException) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

/**
 * Comprehensive error types for the application
 */
sealed class AppException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    
    // Network related errors
    class NetworkException(message: String, cause: Throwable? = null) : AppException(message, cause)
    class ApiException(val code: Int, message: String) : AppException("API Error ($code): $message")
    class TimeoutException(message: String = "Operation timed out") : AppException(message)
    
    // Database related errors
    class DatabaseException(message: String, cause: Throwable? = null) : AppException(message, cause)
    class DataCorruptionException(message: String) : AppException(message)
    
    // Business logic errors
    class ValidationException(message: String) : AppException(message)
    class InvalidStateException(message: String) : AppException(message)
    class ResourceNotFoundException(message: String) : AppException(message)
    
    // AI/Parsing errors
    class AIGenerationException(message: String, cause: Throwable? = null) : AppException(message, cause)
    class JsonParsingException(message: String, cause: Throwable? = null) : AppException(message, cause)
    
    // Security errors
    class SecurityException(message: String) : AppException(message)
    class DecryptionException(message: String) : AppException(message)
    
    // Exercise playback errors
    class PlaybackException(message: String, cause: Throwable? = null) : AppException(message, cause)
    
    // Generic error for unexpected exceptions
    class UnknownException(cause: Throwable) : AppException("An unexpected error occurred", cause)
}

/**
 * Extension functions for easier Result handling
 */
inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T> Result<T>.onError(action: (AppException) -> Unit): Result<T> {
    if (this is Result.Error) action(exception)
    return this
}

inline fun <T> Result<T>.onLoading(action: () -> Unit): Result<T> {
    if (this is Result.Loading) action()
    return this
}

/**
 * Maps the success value to a new type
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> this
        is Result.Loading -> this
    }
}

/**
 * Flat maps the success value to a new Result
 */
inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> {
    return when (this) {
        is Result.Success -> transform(data)
        is Result.Error -> this
        is Result.Loading -> this
    }
}

/**
 * Returns the data if successful, or null if error/loading
 */
fun <T> Result<T>.getOrNull(): T? {
    return if (this is Result.Success) data else null
}

/**
 * Returns the data if successful, or the default value
 */
fun <T> Result<T>.getOrDefault(default: T): T {
    return if (this is Result.Success) data else default
}

/**
 * Utility function to safely execute operations and wrap in Result
 */
inline fun <T> safeCall(action: () -> T): Result<T> {
    return try {
        Result.Success(action())
    } catch (e: AppException) {
        Result.Error(e)
    } catch (e: Exception) {
        Result.Error(AppException.UnknownException(e))
    }
}

/**
 * Utility function for safe suspend operations
 */
suspend inline fun <T> safeSuspendCall(crossinline action: suspend () -> T): Result<T> {
    return try {
        Result.Success(action())
    } catch (e: AppException) {
        Result.Error(e)
    } catch (e: Exception) {
        Result.Error(AppException.UnknownException(e))
    }
}

/**
 * Extension to convert throwables to appropriate AppExceptions
 */
fun Throwable.toAppException(): AppException {
    return when (this) {
        is AppException -> this
        is java.net.SocketTimeoutException -> AppException.TimeoutException()
        is java.net.UnknownHostException -> AppException.NetworkException("No internet connection")
        is java.net.ConnectException -> AppException.NetworkException("Connection failed")
        is retrofit2.HttpException -> AppException.ApiException(code(), message())
        is com.google.gson.JsonSyntaxException -> AppException.JsonParsingException("Invalid JSON format", this)
        is android.database.sqlite.SQLiteException -> AppException.DatabaseException("Database operation failed", this)
        else -> AppException.UnknownException(this)
    }
}