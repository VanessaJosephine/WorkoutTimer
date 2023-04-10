package com.example.workouttimer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;
import java.util.TimerTask;

public class Timer extends AppCompatActivity {
    private static Integer progressStatus, exerciseMinute, exerciseSecond, restMinute, restSecond, duration, getDuration;
    private static Double time;
    private static ProgressBar progressBar;
    private static TextView state, timerText, set;
    private static Button btnSetting, btnStart, btnReset;
    private static CountDownTimer timer;
    private static Boolean timerStarted = false;
    private Integer timeLeftExercise, timeLeftRest, timePauseLeft, count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        // findViewById
        btnSetting = findViewById(R.id.button3);
        btnStart = findViewById(R.id.button4);
        btnReset = findViewById(R.id.button5);
        set = findViewById(R.id.textView9);
        state = findViewById(R.id.textView8);
        timerText = findViewById(R.id.textView7);
        progressBar = findViewById(R.id.progressBar);

        // SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
        exerciseMinute = sharedPreferences.getInt("exerciseMinute", 0);
        exerciseSecond = sharedPreferences.getInt("exerciseSecond", 0);
        restMinute = sharedPreferences.getInt("restMinute", 0);
        restSecond = sharedPreferences.getInt("restSecond", 0);
        duration = sharedPreferences.getInt("duration", 0);
        getDuration = duration;

        // OnClickListener
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentBack = new Intent(Timer.this, Setting.class);
                startActivity(intentBack);
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pauses the timer
                if (btnStart.getText().equals("Pause")) pauseTimer();
                // Starts the timer for the first time
                else if (btnStart.getText().equals("Start") && state.getText().equals("Start!"))
                {
                    set.setText("Set " + Integer.toString(count) + " of " + Integer.toString(getDuration));
                    timeLeftExercise = (exerciseMinute * 60) + exerciseSecond;
                    startTimerExercise(timeLeftExercise);
                }
                // Resume the timer when the timer is on the exercise phase
                else if (btnStart.getText().equals("Start") && state.getText().equals("Exercise!"))
                {
                    resumeTimer("exercise");
                }
                // Resume the timer when the timer is on the rest phase
                else if (btnStart.getText().equals("Start") && state.getText().equals("Rest!"))
                {
                    resumeTimer("rest");
                }
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();
            }
        });
    }
    private void startTimerExercise(Integer timeLeft) {
        // Changes the background color to white
        ConstraintLayout bgElement = (ConstraintLayout) findViewById(R.id.container);
        bgElement.setBackgroundColor(Color.WHITE);
        state.setText("Exercise!");
        progressStatus = 1;
        progressBar.setMax(timeLeft);
        timer = new CountDownTimer(timeLeft*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timePauseLeft = (int) (millisUntilFinished + 1000) / 1000;
                timerText.setText(Long.toString((millisUntilFinished+1000)/1000));
                progressBar.setProgress(progressStatus++);
            }
            @Override
            public void onFinish() {
                timerStarted = false;
                updateButtons();
                // If there is a rest phase, create a new timer for it
                if (restMinute != 0 || restSecond != 0)
                {
                    timeLeftRest = (restMinute * 60) + restSecond;
                    startTimerRest(timeLeftRest);
                }
            }
        }.start();

        timerStarted = true;
        updateButtons();
    }
    private void startTimerRest (Integer timeLeft) {
        // Changes the background color to make the rest phase more visible
        ConstraintLayout bgElement = (ConstraintLayout) findViewById(R.id.container);
        bgElement.setBackgroundColor(Color.rgb(233, 237, 201));
        
        state.setText("Rest!");
        progressStatus = 1;
        progressBar.setMax(timeLeft);
        timer = new CountDownTimer(timeLeft*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timePauseLeft = (int) (millisUntilFinished + 1000) / 1000;
                timerText.setText(Long.toString((millisUntilFinished+1000)/1000));
                progressBar.setProgress(progressStatus++);
            }
            @Override
            public void onFinish() {
                timerStarted = false;
                updateButtons();
                // If there's multiple sets, create a new timer for the exercise phase until all the sets have been iterated
                if (duration > 1 && duration <= 10)
                {
                    timeLeftExercise = (exerciseMinute * 60) + exerciseSecond;
                    startTimerExercise(timeLeftExercise);
                    count++;
                    set.setText("Set " + Integer.toString(count) + " of " + Integer.toString(getDuration));
                    duration -= 1;
                }
                // If there's no more sets
                else
                {
                    ConstraintLayout bgElement = (ConstraintLayout) findViewById(R.id.container);
                    bgElement.setBackgroundColor(Color.WHITE);
                    timerText.setText("Finished!");
                    state.setText("Completed");
                }
            }
        }.start();

        timerStarted = true;
        updateButtons();
    }
    private void pauseTimer() {
        timer.cancel();
        timerStarted = false;
        updateButtons();
    }

    private void resetTimer() {
        timeLeftExercise = 0;
        timeLeftRest = 0;
        timePauseLeft = 0;
        timerText.setText("0");
        updateButtons();
    }
    private void resumeTimer(String state)
    {
        timerStarted = true;
        if (state == "exercise") startTimerExercise(timePauseLeft);
        else if (state == "rest") startTimerRest(timePauseLeft);
    }
    private void updateButtons() {
        if (timerStarted) {
            btnReset.setEnabled(false);
            btnStart.setText("Pause");
            btnStart.setBackgroundColor(Color.RED);
        } else {
            btnReset.setEnabled(true);
            btnStart.setText("Start");
            btnStart.setBackgroundColor(Color.GREEN);
        }
    }
}