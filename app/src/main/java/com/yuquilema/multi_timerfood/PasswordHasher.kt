package com.yuquilema.multi_timerfood

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Utilidad para hashear y verificar contraseñas usando PBKDF2WithHmacSHA256.
 * No requiere dependencias externas (usa javax.crypto del SDK de Android).
 */
object PasswordHasher {

    private const val ITERATIONS = 10_000
    private const val KEY_LENGTH = 256 // bits
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val SALT_LENGTH = 16 // bytes

    /**
     * Genera un hash seguro de la contraseña.
     * Formato de salida: "iteraciones:salt_base64:hash_base64"
     * Guardar este string completo en la columna de contraseña.
     */
    fun hashPassword(password: String): String {
        val salt = generateSalt()
        val hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP)
        val hashBase64 = Base64.encodeToString(hash, Base64.NO_WRAP)
        return "$ITERATIONS:$saltBase64:$hashBase64"
    }

    /**
     * Verifica si una contraseña en texto plano coincide con un hash almacenado.
     */
    fun verifyPassword(password: String, storedHash: String): Boolean {
        return try {
            val parts = storedHash.split(":")
            if (parts.size != 3) return false

            val iterations = parts[0].toInt()
            val salt = Base64.decode(parts[1], Base64.NO_WRAP)
            val expectedHash = Base64.decode(parts[2], Base64.NO_WRAP)

            val actualHash = pbkdf2(password.toCharArray(), salt, iterations, expectedHash.size * 8)

            // Comparación en tiempo constante para evitar timing attacks
            actualHash.contentEquals(expectedHash)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Detecta si un valor almacenado ya está hasheado (para migración de usuarios existentes).
     */
    fun isHashed(storedValue: String): Boolean {
        val parts = storedValue.split(":")
        return parts.size == 3 && parts[0].toIntOrNull() != null
    }

    private fun generateSalt(): ByteArray {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)
        return salt
    }

    private fun pbkdf2(password: CharArray, salt: ByteArray, iterations: Int, keyLength: Int): ByteArray {
        val spec = PBEKeySpec(password, salt, iterations, keyLength)
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        return factory.generateSecret(spec).encoded
    }
}