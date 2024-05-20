package com.montoya.fitlab;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class bodyActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private EditText editTextEdad, editTextPeso, editTextEstatura;
    private Button buttonIniciar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body);

        // Inicializar Firebase Auth y Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Obtener el nombre del usuario desde la intención
        String username = getIntent().getStringExtra("username");

        // Referencias a los elementos de la UI
        welcomeTextView = findViewById(R.id.textView);
        editTextEdad = findViewById(R.id.editTextEdad);
        editTextPeso = findViewById(R.id.editTextPeso);
        editTextEstatura = findViewById(R.id.editTextEstatura);
        buttonIniciar = findViewById(R.id.button);

        // Mostrar el nombre del usuario en el TextView
        welcomeTextView.setText("Bienvenido, " + username + ". Conozcamos un poco más de ti");

        buttonIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });
    }

    private void saveUserData() {
        String edad = editTextEdad.getText().toString().trim();
        String peso = editTextPeso.getText().toString().trim();
        String estatura = editTextEstatura.getText().toString().trim();

        // Validar los datos ingresados
        if (TextUtils.isEmpty(edad) || TextUtils.isEmpty(peso) || TextUtils.isEmpty(estatura)) {
            Toast.makeText(bodyActivity.this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener el usuario actual
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Crear un objeto con los datos del usuario
            UserData userData = new UserData(edad, peso, estatura);

            // Guardar los datos en la base de datos bajo el nodo del usuario
            mDatabase.child("users").child(userId).child("userData").setValue(userData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(bodyActivity.this, "Datos guardados correctamente.", Toast.LENGTH_SHORT).show();
                                // Redirigir a MainActivity
                                Intent intent = new Intent(bodyActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(bodyActivity.this, "Error al guardar los datos.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(bodyActivity.this, "Error: Usuario no autenticado.", Toast.LENGTH_SHORT).show();
        }
    }

    // Clase para almacenar los datos del usuario
    public static class UserData {
        public String edad;
        public String peso;
        public String estatura;

        public UserData() {
            // Constructor vacío necesario para Firebase
        }

        public UserData(String edad, String peso, String estatura) {
            this.edad = edad;
            this.peso = peso;
            this.estatura = estatura;
        }
    }
}
