package com.yuquilema.multi_timerfood;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UsuarioDaoTest {

    private AppDatabase db;
    private UsuarioDao usuarioDao;

    @Before
    public void crearBaseDatos() {

        Context context = ApplicationProvider.getApplicationContext();

        db = Room.inMemoryDatabaseBuilder(
                context,
                AppDatabase.class
        ).allowMainThreadQueries().build();

        usuarioDao = db.usuarioDao();
    }

    @After
    public void cerrarBaseDatos() {
        db.close();
    }

    @Test
    public void insertarYLeerUsuario() {

        // Arrange
        Usuario usuario = new Usuario(
                "Kevin",
                "kevin@gmail.com",
                "123456"
        );

        // Act
        usuarioDao.insertarUsuario(usuario);

        Usuario resultado =
                usuarioDao.buscarPorCorreo("kevin@gmail.com");

        // Assert
        assertNotNull(resultado);
        assertEquals("Kevin", resultado.getNombre());
        assertEquals("kevin@gmail.com", resultado.getCorreo());
        assertEquals("123456", resultado.getPassword());
    }

    @Test
    public void loginCorrectoEIncorrecto() {

        // Arrange
        Usuario usuario = new Usuario(
                "Kevin",
                "kevin@gmail.com",
                "123456"
        );

        usuarioDao.insertarUsuario(usuario);

        // Act
        Usuario loginCorrecto =
                usuarioDao.login("kevin@gmail.com","123456");

        Usuario passwordIncorrecta =
                usuarioDao.login("kevin@gmail.com","654321");

        Usuario correoIncorrecto =
                usuarioDao.login("otro@gmail.com","123456");

        // Assert
        assertNotNull(loginCorrecto);
        assertEquals("Kevin", loginCorrecto.getNombre());

        assertNull(passwordIncorrecta);
        assertNull(correoIncorrecto);
    }

}
