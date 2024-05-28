package com.montoya.fitlab;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class profileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView profileName, profileAgeValue, profileWeightValue, profileHeightValue;
    private ListView exerciseListView;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ExerciseAdapter exerciseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Referencia a los elementos de la interfaz de usuario
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        profileImage = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);
        profileAgeValue = findViewById(R.id.profileAgeValue);
        profileWeightValue = findViewById(R.id.profileWeightValue);
        profileHeightValue = findViewById(R.id.profileHeightValue);
        exerciseListView = findViewById(R.id.exerciseListView);

        // Inicialización de Firebase Auth y Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Inicializar el adaptador de ejercicios
        exerciseAdapter = new ExerciseAdapter(this, R.layout.exercise_list_item, new ArrayList<ExerciseData>());

        // Configurar adaptador en la ListView
        exerciseListView.setAdapter(exerciseAdapter);

        // Cargar perfil del usuario
        loadUserProfile();

        // Configurar clic en la imagen de perfil
        profileImage.setOnClickListener(v -> showProfileImageDialog());
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("username").getValue(String.class);
                        String age = snapshot.child("userData").child("edad").getValue(String.class);
                        String weight = snapshot.child("userData").child("peso").getValue(String.class);
                        String height = snapshot.child("userData").child("estatura").getValue(String.class);

                        profileName.setText(name != null ? name : "");

                        // Si los valores son nulos, establecer un valor predeterminado
                        profileAgeValue.setText(age != null ? age : "");
                        profileWeightValue.setText(weight != null ? weight + " Kg" : "");
                        profileHeightValue.setText(height != null ? height + " cm" : "");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(profileActivity.this, "Failed to load profile data", Toast.LENGTH_SHORT).show();
                }
            });

            // Cargar datos de ejercicios
            mDatabase.child("users").child(userId).child("userData").child("exercise")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<ExerciseData> exerciseDataList = new ArrayList<>();
                            for (DataSnapshot exerciseSnapshot : snapshot.getChildren()) {
                                ExerciseData exerciseData = exerciseSnapshot.getValue(ExerciseData.class);
                                if (exerciseData != null) {
                                    exerciseDataList.add(exerciseData);
                                }
                            }
                            // Actualizar el adaptador con los datos de los ejercicios
                            exerciseAdapter.addAll(exerciseDataList);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(profileActivity.this, "Failed to load exercise data", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.d("FirebaseAuth", "Usuario no autenticado");
        }
    }

    private void showProfileImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una imagen de perfil");
        builder.setItems(new CharSequence[]{"Perfil 1", "Perfil 2", "Perfil 3"},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                profileImage.setImageResource(R.drawable.perfil1);
                                break;
                            case 1:
                                profileImage.setImageResource(R.drawable.perfil2);
                                break;
                            case 2:
                                profileImage.setImageResource(R.drawable.perfil3);
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    // Clase para los datos de ejercicio
    public static class ExerciseData {
        public String date;
        public String difficulty;
        public int phase;
        public long time;

        public ExerciseData() {
        }

        public ExerciseData(String date, String difficulty, int phase, long time) {
            this.date = date;
            this.difficulty = difficulty;
            this.phase = phase;
            this.time = time;
        }
    }
}



