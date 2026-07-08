package com.yuquilema.multi_timerfood;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.yuquilema.multi_timerfood.util.AuthValidator;

import android.widget.TextView;

public class LoginActivity extends Activity {

    private TextInputLayout tilCorreo, tilPassword;

    private TextInputEditText etCorreo, etPassword;

    private Button btnLogin;
    private TextView tvIrRegistro;

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);

        db = AppDatabase.getInstance(this);

        tilCorreo = findViewById(R.id.tilCorreo);
        tilPassword = findViewById(R.id.tilPassword);

        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        tvIrRegistro = findViewById(R.id.tvIrRegistro);

        btnLogin.setOnClickListener(v -> {

            if (validateInputs()) {

                loginUser();

            }

        });

        tvIrRegistro.setOnClickListener(v -> {

            Intent intent = new Intent(LoginActivity.this,
                    RegisterActivity.class);

            startActivity(intent);

        });

    }

    private boolean validateInputs() {

        tilCorreo.setError(null);
        tilPassword.setError(null);

        if (etCorreo.getText() == null || etPassword.getText() == null) return false;
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (!AuthValidator.validateEmail(tilCorreo, correo)) return false;
        if (!AuthValidator.validatePassword(tilPassword, password)) return false;

        return true;
    }

    private void loginUser() {

        if (etCorreo.getText() == null || etPassword.getText() == null) return;
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString();

        Usuario usuario = db.usuarioDao().login(correo, password);

        if (usuario != null) {

            Toast.makeText(
                    this,
                    "Bienvenido " + usuario.getNombre(),
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

        } else {

            tilPassword.setError("Correo o contraseña incorrectos");

        }

    }

}