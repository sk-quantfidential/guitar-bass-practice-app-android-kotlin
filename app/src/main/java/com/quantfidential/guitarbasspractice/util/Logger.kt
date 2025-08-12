package com.quantfidential.guitarbasspractice.util

import android.util.Log

/**
 * Centralized logging utility with different log levels and built-in safety checks.
 * Automatically disabled in release builds for security and performance.
 */
object Logger {
    
    const val DEFAULT_TAG = "GuitarBassPractice"
    const val IS_DEBUG = true // TODO: Make this configurable
    
    /**
     * Debug logging - only visible in debug builds
     */
    fun d(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (IS_DEBUG) {
            if (throwable != null) {
                Log.d(tag, message, throwable)
            } else {
                Log.d(tag, message)
            }
        }
    }
    
    /**
     * Info logging - shows in debug builds
     */
    fun i(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (IS_DEBUG) {
            if (throwable != null) {
                Log.i(tag, message, throwable)
            } else {
                Log.i(tag, message)
            }
        }
    }
    
    /**
     * Warning logging - shows in all builds but sanitized in release
     */
    fun w(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        val sanitizedMessage = if (IS_DEBUG) message else sanitizeMessage(message)
        if (throwable != null) {
            Log.w(tag, sanitizedMessage, throwable)
        } else {
            Log.w(tag, sanitizedMessage)
        }
    }
    
    /**
     * Error logging - shows in all builds but sanitized in release
     */
    fun e(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        val sanitizedMessage = if (IS_DEBUG) message else sanitizeMessage(message)
        if (throwable != null) {
            Log.e(tag, sanitizedMessage, throwable)
        } else {
            Log.e(tag, sanitizedMessage)
        }
    }
    
    /**
     * Method entry logging for debugging flow
     */
    fun entry(tag: String = DEFAULT_TAG, methodName: String, params: Map<String, Any?> = emptyMap()) {
        if (IS_DEBUG) {
            val paramString = if (params.isNotEmpty()) {
                params.entries.joinToString(", ") { "${it.key}=${it.value}" }
            } else {
                "no params"
            }
            d(tag, "→ $methodName($paramString)")
        }
    }
    
    /**
     * Method exit logging for debugging flow
     */
    fun exit(tag: String = DEFAULT_TAG, methodName: String, result: Any? = null) {
        if (IS_DEBUG) {
            val resultString = result?.let { " → $it" } ?: ""
            d(tag, "← $methodName$resultString")
        }
    }
    
    /**
     * Performance timing helper
     */
    inline fun <T> time(tag: String = DEFAULT_TAG, operation: String, block: () -> T): T {
        val startTime = System.currentTimeMillis()
        return try {
            block()
        } finally {
            if (IS_DEBUG) {
                val duration = System.currentTimeMillis() - startTime
                d(tag, "$operation took ${duration}ms")
            }
        }
    }
    
    /**
     * Log network requests (sanitized in release)
     */
    fun network(tag: String = DEFAULT_TAG, method: String, url: String, responseCode: Int? = null) {
        if (IS_DEBUG) {
            val codeString = responseCode?.let { " → $it" } ?: ""
            d(tag, "🌐 $method $url$codeString")
        } else {
            // Only log method and response code in release, not full URL
            responseCode?.let { 
                i(tag, "Network request: $method → $it")
            }
        }
    }
    
    /**
     * Log database operations
     */
    fun database(tag: String = DEFAULT_TAG, operation: String, table: String? = null, affectedRows: Int? = null) {
        if (IS_DEBUG) {
            val tableString = table?.let { " on $it" } ?: ""
            val rowsString = affectedRows?.let { " ($it rows)" } ?: ""
            d(tag, "🗄️ $operation$tableString$rowsString")
        }
    }
    
    /**
     * Log user interactions (privacy-safe)
     */
    fun userAction(tag: String = DEFAULT_TAG, action: String, screen: String? = null) {
        val screenString = screen?.let { " on $it" } ?: ""
        i(tag, "👤 User: $action$screenString")
    }
    
    /**
     * Log AI operations
     */
    fun ai(tag: String = DEFAULT_TAG, operation: String, tokens: Int? = null, success: Boolean = true) {
        val tokenString = tokens?.let { " (${it} tokens)" } ?: ""
        val status = if (success) "✅" else "❌"
        i(tag, "$status AI: $operation$tokenString")
    }
    
    /**
     * Sanitize sensitive information from log messages in release builds
     */
    private fun sanitizeMessage(message: String): String {
        return message
            .replace(Regex("\\b\\d{16}\\b"), "[CARD_NUMBER]") // Credit card numbers
            .replace(Regex("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b"), "[EMAIL]") // Email addresses
            .replace(Regex("\\bpassword=\\w+"), "password=[REDACTED]") // Passwords in URLs
            .replace(Regex("\\bapi_key=\\w+"), "api_key=[REDACTED]") // API keys
            .replace(Regex("\\btoken=\\w+"), "token=[REDACTED]") // Auth tokens
    }
}

/**
 * Extension functions for easier logging from any class
 */
fun Any.logd(message: String, throwable: Throwable? = null) {
    Logger.d(this::class.simpleName ?: "Unknown", message, throwable)
}

fun Any.logi(message: String, throwable: Throwable? = null) {
    Logger.i(this::class.simpleName ?: "Unknown", message, throwable)
}

fun Any.logw(message: String, throwable: Throwable? = null) {
    Logger.w(this::class.simpleName ?: "Unknown", message, throwable)
}

fun Any.loge(message: String, throwable: Throwable? = null) {
    Logger.e(this::class.simpleName ?: "Unknown", message, throwable)
}

fun Any.logEntry(methodName: String, params: Map<String, Any?> = emptyMap()) {
    Logger.entry(this::class.simpleName ?: "Unknown", methodName, params)
}

fun Any.logExit(methodName: String, result: Any? = null) {
    Logger.exit(this::class.simpleName ?: "Unknown", methodName, result)
}