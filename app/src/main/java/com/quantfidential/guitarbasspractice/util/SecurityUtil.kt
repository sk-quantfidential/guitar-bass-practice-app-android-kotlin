package com.quantfidential.guitarbasspractice.util

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import net.sqlcipher.database.SQLiteDatabase
import javax.crypto.KeyGenerator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityUtil @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val encryptedPrefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Gets or creates a secure database passphrase using Android Keystore
     * @return ByteArray suitable for SQLCipher
     */
    fun getDatabasePassphrase(): ByteArray {
        val existingKey = encryptedPrefs.getString(DATABASE_KEY_PREF, null)
        
        return if (existingKey != null) {
            existingKey.toByteArray(Charsets.UTF_8)
        } else {
            // Generate new secure random key
            val newKey = generateSecureKey()
            encryptedPrefs.edit()
                .putString(DATABASE_KEY_PREF, newKey)
                .apply()
            newKey.toByteArray(Charsets.UTF_8)
        }
    }

    /**
     * Generates a cryptographically secure random key
     */
    private fun generateSecureKey(): String {
        return try {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES)
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                "database_key_${System.currentTimeMillis()}",
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
            
            keyGenerator.init(keyGenParameterSpec)
            val secretKey = keyGenerator.generateKey()
            
            // Convert to base64 string for storage
            android.util.Base64.encodeToString(
                secretKey.encoded,
                android.util.Base64.NO_WRAP
            )
        } catch (e: Exception) {
            // Fallback to secure random if Keystore fails
            generateFallbackKey()
        }
    }

    /**
     * Fallback key generation using SecureRandom
     */
    private fun generateFallbackKey(): String {
        val secureRandom = java.security.SecureRandom()
        val keyBytes = ByteArray(32) // 256 bits
        secureRandom.nextBytes(keyBytes)
        return android.util.Base64.encodeToString(keyBytes, android.util.Base64.NO_WRAP)
    }

    /**
     * Creates SQLCipher passphrase from stored secure key
     */
    fun getSQLitePassphrase(): ByteArray {
        return SQLiteDatabase.getBytes(String(getDatabasePassphrase()).toCharArray())
    }

    companion object {
        private const val DATABASE_KEY_PREF = "db_encryption_key"
    }
}