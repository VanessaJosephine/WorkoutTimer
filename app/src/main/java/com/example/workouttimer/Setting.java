package com.example.workouttimer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class Setting extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // findViewById
        Spinner exerciseTimeMinute = findViewById(R.id.spinner2);
        Spinner exerciseTimeSec = findViewById(R.id.spinner3);
        Spinner restTimeMinute = findViewById(R.id.spinner4);
        Spinner restTimeSec = findViewById(R.id.spinner5);
        Button btn = findViewById(R.id.button2);
        Spinner duration = findViewById(R.id.spinner);

        // Spinner
        List<Integer> durations = new ArrayList<Integer>();
        for (int i = 1; i <= 10; i++)
        {
            durations.add(i);
        }
        List<Integer> minutes = new ArrayList<Integer>();
        List<Integer> seconds = new ArrayList<Integer>();
        for (int i = 0; i <= 60; i++) {
            minutes.add(i);
            seconds.add(i);
        }
        // Adapter for Spinners
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, durations);
        adapter.setDropDownViewResource((android.R.layout.simple_spinner_dropdown_item));
        duration.setAdapter((adapter));
        ArrayAdapter<Integer> adapter_min = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, minutes);
        adapter_min.setDropDownViewResource((android.R.layout.simple_spinner_dropdown_item));
        exerciseTimeMinute.setAdapter((adapter_min));
        restTimeMinute.setAdapter((adapter_min));
        ArrayAdapter<Integer> adapter_sec = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, seconds);
        adapter_sec.setDropDownViewResource((android.R.layout.simple_spinner_dropdown_item));
        exerciseTimeSec.setAdapter((adapter_sec));
        restTimeSec.setAdapter((adapter_sec));

        // SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);

        // setOnClickListener
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Setting.this, Timer.class);
                SharedPreferences.Editor myEditor = sharedPreferences.edit();
                if (!exerciseTimeMinute.getSelectedItem().toString().isEmpty() ||
                    !exerciseTimeSec.getSelectedItem().toString().isEmpty() ||
                    !restTimeMinute.getSelectedItem().toString().isEmpty() ||
                    !restTimeSec.getSelectedItem().toString().isEmpty())
                {
                    myEditor.putInt("exerciseMinute", Integer.parseInt(exerciseTimeMinute.getSelectedItem().toString()));
                    myEditor.putInt("exerciseSecond", Integer.parseInt(exerciseTimeSec.getSelectedItem().toString()));
                    myEditor.putInt("restMinute", Integer.parseInt(restTimeMinute.getSelectedItem().toString()));
                    myEditor.putInt("restSecond", Integer.parseInt(restTimeSec.getSelectedItem().toString()));
                    myEditor.putInt("duration", Integer.parseInt(duration.getSelectedItem().toString()));
                    myEditor.apply();
                    startActivity(intent);
                }
                else Toast.makeText(Setting.this, "Please enter the exercise time, rest time, and the duration accordingly!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}