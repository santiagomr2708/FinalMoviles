package com.montoya.fitlab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private ImageView profileImage, logoutImage;
    private LinearLayout easyOptions, mediumOptions, hardOptions;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Referencias a los elementos de la UI
        profileImage = findViewById(R.id.profile_image);
        logoutImage = findViewById(R.id.logout_image);
        easyOptions = findViewById(R.id.easy_options);
        mediumOptions = findViewById(R.id.medium_options);
        hardOptions = findViewById(R.id.hard_options);

        // Configurar el botón de imagen de perfil
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ir a la pantalla de perfil
                startActivity(new Intent(MainActivity.this, profileActivity.class));
            }
        });

        // Configurar el botón de cerrar sesión
        logoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cerrar sesión del usuario
                mAuth.signOut();
                // Ir a la pantalla de inicio de sesión
                startActivity(new Intent(MainActivity.this, loginActivity.class));
                // Finalizar la actividad actual
                finish();
            }
        });

        // Configurar los botones de niveles
        findViewById(R.id.image_easy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Alternar la visibilidad de las opciones de nivel fácil
                toggleVisibility(easyOptions);
            }
        });

        findViewById(R.id.image_medium).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Alternar la visibilidad de las opciones de nivel medio
                toggleVisibility(mediumOptions);
            }
        });

        findViewById(R.id.image_hard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Alternar la visibilidad de las opciones de nivel difícil
                toggleVisibility(hardOptions);
            }
        });

        // Configurar botones de iniciar en cada nivel
        findViewById(R.id.button_easy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ir a la pantalla de facil
                startActivity(new Intent(MainActivity.this, easyActivity.class));
                // Iniciar el nivel fácil
                startLevel("fácil");
            }
        });

        findViewById(R.id.button_medium).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ir a la pantalla de medio
                startActivity(new Intent(MainActivity.this, mediumActivity.class));
                // Iniciar el nivel medio
                startLevel("medio");
            }
        });

        findViewById(R.id.button_hard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ir a la pantalla de dificil
                startActivity(new Intent(MainActivity.this, hardActivity.class));
                // Iniciar el nivel difícil
                startLevel("difícil");
            }
        });
    }

    private void toggleVisibility(View view) {
        if (view.getVisibility() == View.GONE) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private void startLevel(String level) {
        Toast.makeText(this, "Iniciar nivel " + level, Toast.LENGTH_SHORT).show();
        // Aquí puedes añadir la funcionalidad para iniciar el nivel correspondiente
    }
}