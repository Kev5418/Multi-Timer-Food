package com.yuquilema.multi_timerfood;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.yuquilema.multi_timerfood.Validaciones;

import java.util.Objects;

public class RegisterActivity extends Activity {

    private TextInputLayout tilNombre, tilCorreo, tilPassword, tilConfirmarPassword;

    private TextInputEditText etNombre, etCorreo, etPassword, etConfirmarPassword;

    private Button btnRegistrar;
    private TextView tvIrLogin;

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = AppDatabase.getInstance(this);

        tilNombre = findViewById(R.id.tilNombre);
        tilCorreo = findViewById(R.id.tilCorreo);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmarPassword = findViewById(R.id.tilConfirmarPassword);

        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        etConfirmarPassword = findViewById(R.id.etConfirmarPassword);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        tvIrLogin = findViewById(R.id.tvIrLogin);

        btnRegistrar.setOnClickListener(v -> {

            if (validateInputs()) {

                Usuario existente = db.usuarioDao().buscarPorCorreo(
                        etCorreo.getText().toString().trim());

                if (existente != null) {
                    tilCorreo.setError("Este correo ya está registrado");
                    return;
                }

                Usuario usuario = new Usuario();

                usuario.setNombre(etNombre.getText().toString().trim());
                usuario.setCorreo(etCorreo.getText().toString().trim());
                usuario.setPassword(etPassword.getText().toString());

                db.usuarioDao().insertarUsuario(usuario);

                Toast.makeText(this,
                        "Usuario registrado correctamente",
                        Toast.LENGTH_SHORT).show();

                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }

        });

        tvIrLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

    }

    private boolean validateInputs() {

        tilNombre.setError(null);
        tilCorreo.setError(null);
        tilPassword.setError(null);
        tilConfirmarPassword.setError(null);

        String nombre = Objects.requireNonNull(etNombre.getText()).toString().trim();
        String correo = Objects.requireNonNull(etCorreo.getText()).toString().trim();
        String password = Objects.requireNonNull(etPassword.getText()).toString();
        String confirmar = Objects.requireNonNull(etConfirmarPassword.getText()).toString();

        if (!Validaciones.validarNombre(nombre)) {
            tilNombre.setError("Ingrese su nombre");
            return false;
        }

        if (!Validaciones.validarCorreo(correo)) {
            tilCorreo.setError("Ingrese un correo válido");
            return false;
        }

        if (!Validaciones.validarPassword(password)) {
            tilPassword.setError("La contraseña debe tener al menos 6 caracteres");
            return false;
        }

        if (confirmar.isEmpty()) {
            tilConfirmarPassword.setError("Confirme la contraseña");
            return false;
        }

        if (!password.equals(confirmar)) {
            tilConfirmarPassword.setError("Las contraseñas no coinciden");
            return false;
        }

        return true;
    }
}