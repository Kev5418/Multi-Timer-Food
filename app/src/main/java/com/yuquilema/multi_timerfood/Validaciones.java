package com.yuquilema.multi_timerfood;

import android.util.Patterns;
import java.util.regex.Pattern;

public class Validaciones {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public static boolean validarCorreo(String correo) {

        if (correo == null || correo.trim().isEmpty()) {
            return false;
        }

        return EMAIL_PATTERN.matcher(correo).matches();
    }

    public static boolean validarPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean validarNombre(String nombre) {
        return nombre != null && !nombre.trim().isEmpty();
    }
}