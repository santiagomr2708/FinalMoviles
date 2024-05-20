package com.montoya.fitlab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class registerActivity extends AppCompatActivity {

    private EditText emailEditText, usernameEditText, passwordEditText;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Inicializar Firebase Realtime Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        emailEditText = findViewById(R.id.emailEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                registerUser(email, username, password);
            }
        });

        // Agregar OnClickListener para el TextView "¿Ya tienes una cuenta? Iniciar Sesión"
        TextView signupTextView = findViewById(R.id.signinTextView);
        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad de inicio de sesión
                Intent intent = new Intent(registerActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser(String email, final String username, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registro exitoso, actualizar la interfaz de usuario con la información del usuario registrado
                            Toast.makeText(registerActivity.this, "Registro correcto.",
                                    Toast.LENGTH_SHORT).show();

                            // Guardar el nombre de usuario en Realtime Database
                            String userId = mAuth.getCurrentUser().getUid();
                            mDatabase.child("users").child(userId).child("username").setValue(username);

                            // Redirigir a MainActivity
                            Intent intent = new Intent(registerActivity.this, bodyActivity.class);
                            startActivity(intent);
                            finish(); // Finalizar la actividad actual
                        } else {
                            // Si el registro falla, mostrar un mensaje al usuario.
                            Toast.makeText(registerActivity.this, "Registro fallido.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
