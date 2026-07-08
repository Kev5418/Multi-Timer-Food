package com.yuquilema.multi_timerfood.util;

import android.util.Patterns;

import com.google.android.material.textfield.TextInputLayout;

/**
 * Validaciones de formularios de autenticación compartidas por las pantallas
 * de inicio de sesión y registro. Cada método coloca el mensaje de error en el
 * {@link TextInputLayout} correspondiente y devuelve {@code true} si el campo
 * es válido.
 */
public final class AuthValidator {

    public static final int MIN_PASSWORD_LENGTH = 6;

    private AuthValidator() {
    }

    /** Valida que el correo no esté vacío y tenga un formato válido. */
    public static boolean validateEmail(TextInputLayout til, String email) {
        if (email.isEmpty()) {
            til.setError("El correo es obligatorio");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            til.setError("Ingrese un correo válido");
            return false;
        }
        return true;
    }

    /** Valida que la contraseña no esté vacía y cumpla el largo mínimo. */
    public static boolean validatePassword(TextInputLayout til, String password) {
        if (password.isEmpty()) {
            til.setError("La contraseña es obligatoria");
            return false;
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            til.setError("La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres");
            return false;
        }
        return true;
    }
}
