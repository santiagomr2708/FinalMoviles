package com.montoya.fitlab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class ExerciseAdapter extends ArrayAdapter<profileActivity.ExerciseData> {

    private final int resourceLayout;
    private final Context mContext;

    public ExerciseAdapter(Context context, int resource, List<profileActivity.ExerciseData> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            convertView = vi.inflate(resourceLayout, null);
        }

        profileActivity.ExerciseData exerciseData = getItem(position);

        if (exerciseData != null) {
            TextView dateTextView = convertView.findViewById(R.id.dateTextView);
            TextView difficultyTextView = convertView.findViewById(R.id.difficultyTextView);
            TextView phaseTextView = convertView.findViewById(R.id.phaseTextView);
            TextView timeTextView = convertView.findViewById(R.id.timeTextView);

            dateTextView.setText(exerciseData.date);
            difficultyTextView.setText("Dificultad:" + exerciseData.difficulty);
            phaseTextView.setText(String.valueOf("Fase:" + exerciseData.phase));
            timeTextView.setText(String.valueOf("Tiempo:" + exerciseData.time));
        }

        return convertView;
    }
    // Método para convertir milisegundos a minutos y segundos
    private String millisToMinutesSeconds(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}

