package com.example.eartrainer;

import android.content.AbstractThreadedSyncAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import net.mabboud.android_tone_player.OneTimeBuzzer;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Quiz extends AppCompatActivity {
    int numberOfQuestions;
    TextView result, answer;
    ArrayList<Integer> intervalsPositions = new ArrayList<>();
    ArrayList<Integer> chordsPositions = new ArrayList<>();
    Interval [] intervals;
    Chord [] chords;
    double [] frequencies = {880.000, 830.609, 783.991, 739.989, 698.456, 659.255, 622.254, 587.330, 554.365, 523.251, 493.883, 466.164,
            440.000, 415.305, 391.995, 369.994, 349.228, 329.628, 311.127, 293.665, 277.183, 261.626, 246.942, 233.082, 220.000};

    NamedSequence ans;
    int correctAnswers = 0;
    int allAnswers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        GridLayout ll = (GridLayout) findViewById(R.id.ll);
        result = (TextView) findViewById(R.id.points);
        result.setText("0/0");
        answer = (TextView) findViewById(R.id.answer);
        intervals = new Interval[14];
        setIntervals();
        chords = new Chord[8];
        setChords();

        Intent intent = getIntent();
        intervalsPositions = intent.getIntegerArrayListExtra("selectedIntervals");
        chordsPositions = intent.getIntegerArrayListExtra("selectedChords");
        numberOfQuestions = intent.getIntExtra("numberOfQuestionsS", numberOfQuestions);

        final Thread thread = new Thread(new Runnable() {
            public void run() {
                ans = getOne();
            }
        });
        thread.start();

        for(int i = 0; i < intervalsPositions.size(); i++) {
            final Button btn = new Button(this);
            btn.setId(i);
            btn.setAllCaps(false);
            btn.setText(intervals[intervalsPositions.get(i)].getShortName());
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(btn.getText() == ans.getShortName()) {
                        correctAnswers++;
                        answer.setTextColor(Color.GREEN);
                    }
                    else {
                        answer.setTextColor(Color.RED);
                    }
                    allAnswers++;
                    answer.setText(ans.getFullName());
                    result.setText(correctAnswers+"/"+allAnswers);
                    if(allAnswers == numberOfQuestions) {
                        AlertDialog alertDialog = new AlertDialog.Builder(Quiz.this).create();
                        alertDialog.setTitle("End of training");
                        alertDialog.setMessage("Your score: " + correctAnswers + "/" + allAnswers);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                        //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        //startActivity(intent);
                                    }
                                });
                        alertDialog.show();
                    }
                    else {
                        final Thread thread = new Thread(new Runnable() {
                            public void run() {
                                ans = getOne();
                            }
                        });
                        thread.start();
                    }
                }
            });
            ll.addView(btn);
        }

        for(int i = 0; i < chordsPositions.size(); i++) {
            final Button btn = new Button(this);
            btn.setId(intervalsPositions.size()+i);
            btn.setAllCaps(false);
            btn.setText(chords[chordsPositions.get(i)].getShortName());

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(btn.getText() == ans.getShortName()) {
                        correctAnswers++;
                        answer.setTextColor(Color.GREEN);
                    }
                    else {
                        answer.setTextColor(Color.RED);
                    }
                    allAnswers++;
                    answer.setText(ans.getFullName());
                    result.setText(correctAnswers+"/"+allAnswers);

                    if(allAnswers == numberOfQuestions) {
                        AlertDialog alertDialog = new AlertDialog.Builder(Quiz.this).create();
                        alertDialog.setTitle("End of training");
                        alertDialog.setMessage("Your score: " + correctAnswers + "/" + allAnswers);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                        alertDialog.show();
                    }
                    else {

                        final Thread thread = new Thread(new Runnable() {
                            public void run() {
                                ans = getOne();
                            }
                        });
                        thread.start();
                    }
                }
            });
            ll.addView(btn);
        }

    }

    private void setIntervals() {
        intervals[0] = new Interval("Perfect unison", "P1", 0);
        intervals[1] = new Interval("Minor second", "m2", 1);
        intervals[2] = new Interval("Major second", "M2", 2);
        intervals[3] = new Interval("Minor third", "m3", 3);
        intervals[4] = new Interval("Major third", "M3", 4);
        intervals[5] = new Interval("Perfect fourth", "P4", 5);
        intervals[6] = new Interval("Augmented fourth", "A4", 6);
        intervals[7] = new Interval("Diminished fifth", "d5", 6);
        intervals[8] = new Interval("Perfect fifth", "P5", 7);
        intervals[9] = new Interval("Minor sixth", "m6", 8);
        intervals[10] = new Interval("Major sixth", "M6", 9);
        intervals[11] = new Interval("Minor seventh", "m7", 10);
        intervals[12] = new Interval("Major seventh", "M7", 11);
        intervals[13] = new Interval("Perfect octave", "P8", 12);
    }

    private void setChords() {
        chords[0] = new Chord("Major Tonica", "C:MT", intervals[4], intervals[3]);
        chords[1] = new Chord("Major First", "C:M1", intervals[3], intervals[5]);
        chords[2] = new Chord("Major Second", "C:M2", intervals[5], intervals[4]);
        chords[3] = new Chord("Minor Tonica", "C:mT", intervals[3], intervals[4]);
        chords[4] = new Chord("Minor First", "C:m1", intervals[4], intervals[5]);
        chords[5] = new Chord("Minor Second", "C:m2", intervals[5], intervals[3]);
        chords[6] = new Chord("Diminished", "C:d", intervals[3], intervals[3]);
        chords[7] = new Chord("Augmented", "C:A", intervals[4], intervals[4]);
    }

    NamedSequence getOne() {
        Random rand = new Random();

        int random = rand.nextInt(intervalsPositions.size() + chordsPositions.size());

        if ((chordsPositions.size() == 0 && intervalsPositions.size() > 0) || random < intervalsPositions.size()) {
            int elem = rand.nextInt(intervalsPositions.size());
            ArrayList <Integer> notes = new ArrayList<>();
            int tone = rand.nextInt(8);
            notes.add(tone);
            notes.add(intervals[intervalsPositions.get(elem)].getDistance() + tone);
            System.out.println(intervals[intervalsPositions.get(elem)].getShortName());
            play(notes);
            return intervals[intervalsPositions.get(elem)];

        } else {
            int elem = rand.nextInt(chordsPositions.size());
            ArrayList <Integer> notes = new ArrayList<>();
            int tone = rand.nextInt(8);
            notes.add(tone);
            notes.add(chords[chordsPositions.get(elem)].intervals.get(0).getDistance() + notes.get(0));
            notes.add(chords[chordsPositions.get(elem)].intervals.get(1).getDistance() + notes.get(1));
            System.out.println(chords[chordsPositions.get(elem)].getShortName());
            play(notes);
            return chords[chordsPositions.get(elem)];
        }
    }

    void play(final ArrayList<Integer> lastNotes) {
        OneTimeBuzzer buzzer = new OneTimeBuzzer();
        buzzer.setDuration(0.5);
        buzzer.setToneFreqInHz(frequencies[lastNotes.get(lastNotes.size()-1)]);
        buzzer.play();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                OneTimeBuzzer buzzer2 = new OneTimeBuzzer();
                buzzer2.setDuration(0.5);
                buzzer2.setToneFreqInHz(frequencies[lastNotes.get(lastNotes.size()-2)]);
                buzzer2.play();
            }
        },  500);
        if(lastNotes.size()>2) {
            Timer timer2 = new Timer();
            timer2.schedule(new TimerTask() {
                @Override
                public void run() {
                    OneTimeBuzzer buzzer3 = new OneTimeBuzzer();
                    buzzer3.setDuration(0.5);
                    buzzer3.setToneFreqInHz(frequencies[lastNotes.get(lastNotes.size()-3)]);
                    buzzer3.play();
                }
            },  1000);
        }
    }
}
