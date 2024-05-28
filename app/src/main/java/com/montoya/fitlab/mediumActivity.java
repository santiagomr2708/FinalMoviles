package com.montoya.fitlab;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class mediumActivity extends AppCompatActivity {

    private TextView timerText, exerciseDescription;
    private ImageView exerciseImage;
    private ProgressBar progressBar;
    private Button startButton;
    private int currentPhase = 1;
    private long startTime, timeLeftInMillis = 120000; // 2 minutos en milisegundos
    private boolean timerRunning = false;
    private CountDownTimer countDownTimer;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medium);

        // Referencia a los elementos de la interfaz de usuario
        exerciseImage = findViewById(R.id.exerciseImage);
        exerciseDescription = findViewById(R.id.exerciseDescription);
        timerText = findViewById(R.id.timerText);
        progressBar = findViewById(R.id.progressBar);
        startButton = findViewById(R.id.startButton);

        // Inicialización de Firebase Auth y Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Inicialización del reproductor de sonido
        mediaPlayer = MediaPlayer.create(this, R.raw.changesound); // Asegúrate de tener este archivo en res/raw
        if (mediaPlayer == null) {
            Toast.makeText(this, "Error al cargar el archivo de sonido.", Toast.LENGTH_SHORT).show();
        }

        startButton.setOnClickListener(v -> {
            if (timerRunning) {
                pauseTimer();
                saveExerciseData();
                // Redirigir a MainActivity
                startActivity(new Intent(mediumActivity.this, MainActivity.class));
                finish(); // Finaliza la actividad actual para evitar que el usuario vuelva atrás
            } else {
                startTimer();
            }
        });
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                int minutes = (int) (timeLeftInMillis / 1000) / 60;
                int seconds = (int) (timeLeftInMillis / 1000) % 60;
                String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                timerText.setText(timeFormatted);
                progressBar.setProgress((int) (timeLeftInMillis / 1000));
            }

            @Override
            public void onFinish() {
                currentPhase++;
                if (currentPhase <= 2) { // Asumiendo 2 fases
                    resetTimer();
                    updateExercisePhase();
                    startTimer();
                } else {
                    startButton.setText("Iniciar");
                    timerRunning = false;
                }
            }
        }.start();

        startButton.setText("Terminar");
        timerRunning = true;
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        timerRunning = false;
        startButton.setText("Iniciar");
    }

    private void resetTimer() {
        timeLeftInMillis = 120000; // 2 minutos
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerText.setText(timeFormatted);
        progressBar.setProgress((int) (timeLeftInMillis / 1000));
    }

    private void updateExercisePhase() {
        if (currentPhase == 2) {
            exerciseImage.setImageResource(R.drawable.fase2_2);
            exerciseDescription.setText("1. Acuéstate boca arriba\\n2. Posición de brazos: Mantén los brazos a los lados o con los codos hacia dentro para estabilizar el torso.\\n3. Levanta la cadera hacia el techo\\n4. Rodillas y muslos en paralelo\\n5. Mantén la postura durante 5 respiraciones\\n6. Haz repeticiones de 10 levantamientos: Repite 3 veces para obtener beneficios");
            // Reproducir sonido al cambiar de fase
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        }
    }

    private void saveExerciseData() {
        String userId = mAuth.getCurrentUser().getUid();
        String difficulty = "Medio";
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        easyActivity.ExerciseData exerciseData = new easyActivity.ExerciseData(currentDate, difficulty, currentPhase, totalTime);

        // Guardar datos bajo una clave única
        mDatabase.child("users").child(userId).child("userData").child("exercise").push().setValue(exerciseData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(mediumActivity.this, "Datos guardados correctamente.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mediumActivity.this, "Error al guardar los datos.", Toast.LENGTH_SHORT).show();
                    }
                });
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
