package com.yuquilema.multi_timerfood;

import static org.junit.Assert.*;

import com.yuquilema.multi_timerfood.Validaciones;

import org.junit.Test;

public class ValidacionesTest {

    // =============================
    // PRUEBA 1
    // =============================

    @Test
    public void validarCorreo_FormatoCorrecto_RetornaTrue() {

        // Arrange
        String correo = "usuario@gmail.com";

        // Act
        boolean resultado = Validaciones.validarCorreo(correo);

        // Assert
        assertTrue(resultado);
    }

    @Test
    public void validarCorreo_FormatoIncorrecto_RetornaFalse() {

        // Arrange
        String correo = "usuariogmail.com";

        // Act
        boolean resultado = Validaciones.validarCorreo(correo);

        // Assert
        assertFalse(resultado);
    }

    // Caso de borde
    @Test
    public void validarCorreo_DosArrobas_RetornaFalse() {

        String correo = "usuario@@gmail.com";

        boolean resultado = Validaciones.validarCorreo(correo);

        assertFalse(resultado);
    }

    // =============================
    // PRUEBA 2
    // =============================

    @Test
    public void validarPassword_Valida_RetornaTrue() {

        String password = "123456";

        boolean resultado = Validaciones.validarPassword(password);

        assertTrue(resultado);
    }

    @Test
    public void validarPassword_Corta_RetornaFalse() {

        String password = "12345";

        boolean resultado = Validaciones.validarPassword(password);

        assertFalse(resultado);
    }

    // Caso de borde
    @Test
    public void validarPassword_Vacia_RetornaFalse() {

        String password = "";

        boolean resultado = Validaciones.validarPassword(password);

        assertFalse(resultado);
    }

    // =============================
    // PRUEBA 3
    // =============================

    @Test
    public void validarNombre_Valido_RetornaTrue() {

        String nombre = "Kevin";

        boolean resultado = Validaciones.validarNombre(nombre);

        assertTrue(resultado);
    }

    @Test
    public void validarNombre_Vacio_RetornaFalse() {

        String nombre = "";

        boolean resultado = Validaciones.validarNombre(nombre);

        assertFalse(resultado);
    }

    // Caso de borde
    @Test
    public void validarNombre_Espacios_RetornaFalse() {

        String nombre = "      ";

        boolean resultado = Validaciones.validarNombre(nombre);

        assertFalse(resultado);
    }

}