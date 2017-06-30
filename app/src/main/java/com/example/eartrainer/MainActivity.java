package com.example.eartrainer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    Button selectIntervalsButton, selectChordsButton, quizButton, statsButton;
    CheckBox upCheckBox, downCheckBox;
    TextView trainingSummary, numberOfQuestions;
    SeekBar questionsSeekBar;
    int questions;
    boolean[] checkedIntervals;
    boolean[] checkedChords;
    ArrayList<Integer> selectedIntervalsPositions = new ArrayList<>();
    ArrayList<Integer> selectedChordsPositions = new ArrayList<>();
    String[] intervalsLabels = {"Perfect unison (P1)", "Minor second (m2)", "Major second (M2)", "Minor third (m3)", "Major third (M3)", "Perfect fourth (P4)",
            "Augmented fourth (A4)", "Diminished fifth (D4)", "Perfect fifth (P4)", "Minor sixth (m6)", "Major sixth (M6)", "Minor seventh (m7)", "Major seventh (M7)", "Perfect octave (P8)"};
    String[] chordsLabels = {"Major Tonica (MTC)", "Major First (M1C)", "Major Second (M2C)", "Minor Tonica (mTC)", "Minor First (m1C)", "Minor Second (m2C)", "Diminished (DC)", "Augmented (AC)", };
    String[] intervalsLabelsShort = {"P1", "m2", "M2", "m3", "M4", "P4", "A4", "D5", "P5", "m6", "M6", "m7", "M7", "P8"};
    String[] chordsLabelsShort = {"MTC", "M1C", "M2C", "mTC", "m1C", "m2C", "DC", "AC", };
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        selectIntervalsButton = (Button) findViewById(R.id.btnOrder);
        selectChordsButton = (Button) findViewById(R.id.btnOrderChords);
        quizButton = (Button) findViewById(R.id.quizButton);
        statsButton = (Button) findViewById(R.id.statsButton);
        upCheckBox = (CheckBox) findViewById(R.id.upCheckBox);
        downCheckBox = (CheckBox) findViewById(R.id.downCheckBox);
        trainingSummary = (TextView) findViewById(R.id.tvItemSelected);
        numberOfQuestions = (TextView) findViewById(R.id.numberOfQuestions);
        questionsSeekBar = (SeekBar) findViewById(R.id.seekBar);
        checkedIntervals = new boolean[intervalsLabels.length];
        checkedChords = new boolean[chordsLabels.length];
        trainingSummary.setText(prepareSummary());

        selectIntervalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                mBuilder.setTitle("Choose intervals:");
                mBuilder.setMultiChoiceItems(intervalsLabels, checkedIntervals, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {

                        if(isChecked){
                            selectedIntervalsPositions.add(position);
                        }else{
                            selectedIntervalsPositions.remove((Integer.valueOf(position)));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        trainingSummary.setText(prepareSummary());
                    }
                });

                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedIntervals.length; i++) {
                            checkedIntervals[i] = false;
                            selectedIntervalsPositions.clear();
                            trainingSummary.setText(prepareSummary());
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

        selectChordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                mBuilder.setTitle("Choose chords:");
                mBuilder.setMultiChoiceItems(chordsLabels, checkedChords, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if(isChecked){
                            selectedChordsPositions.add(position);
                        }else{
                            selectedChordsPositions.remove((Integer.valueOf(position)));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        trainingSummary.setText(prepareSummary());
                    }
                });

                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedChords.length; i++) {
                            checkedChords[i] = false;
                            selectedChordsPositions.clear();
                            trainingSummary.setText(prepareSummary());
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

        quizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(questions == 0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setMessage("Number of questions have to be greater than 0");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else if(selectedChordsPositions.size() == 0 && selectedIntervalsPositions.size() ==0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setMessage("You have to choose more than one interval or chord.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else if(upCheckBox.isChecked() == false && downCheckBox.isChecked() == false) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setMessage("Number of playing directions have to be greater than 0");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    Intent i = new Intent(getApplicationContext(), Quiz.class);
                    i.putExtra("selectedIntervals", selectedIntervalsPositions);
                    i.putExtra("selectedChords", selectedChordsPositions);
                    i.putExtra("numberOfQuestionsS", questions);
                    i.putExtra("upDirection", upCheckBox.isChecked());
                    i.putExtra("downDirection", downCheckBox.isChecked());
                    startActivity(i);
                }
            }
        });

        statsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Stats.class);
                startActivity(i);
            }
        });

        questionsSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                numberOfQuestions.setText(" " + i);
                questions = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //
            }
        });
    }

    String prepareSummary() {

        if(selectedIntervalsPositions.size() == 0 && selectedChordsPositions.size() == 0) {
            return "You haven't checked any chords and intervals yet.";
        }
        else {

            String summary = "";
            if(selectedIntervalsPositions.size() > 0) {
                summary = summary + "Selected intervals: \n";
                for (int i = 0; i < selectedIntervalsPositions.size(); i++) {
                    summary = summary + intervalsLabelsShort[selectedIntervalsPositions.get(i)];
                    if (i != selectedIntervalsPositions.size() - 1) {
                        summary = summary + ", ";
                    }
                }
                summary = summary + "\n\n";
            }

            if(selectedChordsPositions.size() > 0) {
                summary = summary + "Selected chords: \n";
                for (int i = 0; i < selectedChordsPositions.size(); i++) {
                    summary = summary + chordsLabelsShort[selectedChordsPositions.get(i)];
                    if (i != selectedChordsPositions.size() - 1) {
                        summary = summary + ", ";
                    }
                }
            }

            return summary;
        }
    }
}
