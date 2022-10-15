package com.dzac.childmath;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class LesserGreater extends AppCompatActivity {

    public int nyerobuttonid;
    public int[] ids = {R.id.button1, R.id.button2, R.id.button3};
    public int total = 0;
    public int hit = 0;
    public int a_min = 0;
    public int a_max = 100;
    public int b_min = 0;
    public int b_max = 99;
    public int minutes = 5;
    public Calendar startTime;
    public int colorsave;
    public boolean errorInQuest = false;
    public int solution = 0;
    public boolean nextgame = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesser_greater);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Bundle b = getIntent().getExtras();
        int timeToPlay = -1; // or other values
        if(b != null)
            timeToPlay = b.getInt("timeToPlay");
        TextView text = (TextView) findViewById(R.id.winstate);
        text.setText("");
        TextView results = (TextView) findViewById(R.id.results);
        results.setOnLongClickListener(this::onLongClick);
        results.setText("0 / 0\n0%");
        TextView button1 = (TextView) findViewById(R.id.button1);
        TextView button2 = (TextView) findViewById(R.id.button2);
        TextView button3 = (TextView) findViewById(R.id.button3);
        colorsave = button1.getCurrentTextColor();
        button1.setTypeface(null, Typeface.BOLD);
        button2.setTypeface(null, Typeface.BOLD);
        button3.setTypeface(null, Typeface.BOLD);
        if (timeToPlay == -1)
            showTimeDialog();
        else {
            minutes = timeToPlay;
            startTime = Calendar.getInstance();
            timeUpdater();
            nextgame = true;
        }
        newQuest();
    }

    @Override
    public void onBackPressed() {

    }

    private void timeUpdater() {
        TextView remaining = (TextView) findViewById(R.id.remaining);

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    Calendar nowTime = Calendar.getInstance();
                    long milliseconds1 = startTime.getTimeInMillis();
                    long milliseconds2 = nowTime.getTimeInMillis();

                    long diff = milliseconds2 - milliseconds1;
                    long remain = TimeUnit.MINUTES.toMillis(minutes) - diff;
                    long minutes = (remain / 1000)  / 60;
                    int seconds = (int)((remain / 1000) % 60);
                    if (seconds >= 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                remaining.setText(String.valueOf(minutes) + ":" + String.format("%02d", seconds));
                            }
                        });
                    } else {
                        return;
                    }
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        }).start();



    }

    private void showTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog;

        builder.setTitle("Playing time (minutes)");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setKeyListener(new DigitsKeyListener());
        builder.setView(input);

// Set up the buttons

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String entered_string = input.getText().toString();
                if (entered_string.length() < 1) {
                    entered_string = "5";
                }
                minutes = Integer.valueOf(entered_string);
                startTime = Calendar.getInstance();
                timeUpdater();
            }
        });
        dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
        input.requestFocus();

    }

    private void reEnable() {
        TextView button1 = (TextView) findViewById(R.id.button1);
        TextView button2 = (TextView) findViewById(R.id.button2);
        TextView button3 = (TextView) findViewById(R.id.button3);

        //button1.setEnabled(true);
        //button2.setEnabled(true);
        //button3.setEnabled(true);
        button1.setVisibility(View.VISIBLE);
        button2.setVisibility(View.VISIBLE);
        button3.setVisibility(View.VISIBLE);
        button1.setOnClickListener(this::onClick);
        button2.setOnClickListener(this::onClick);
        button3.setOnClickListener(this::onClick);
        button1.setTextColor(colorsave);
        button2.setTextColor(colorsave);
        button3.setTextColor(colorsave);
        button1.setAlpha((float)1);
        button2.setAlpha((float)1);
        button3.setAlpha((float)1);

    }

    private void disable() {
        TextView button1 = (TextView) findViewById(R.id.button1);
        TextView button2 = (TextView) findViewById(R.id.button2);
        TextView button3 = (TextView) findViewById(R.id.button3);

        //button1.setEnabled(false);
        //button2.setEnabled(false);
        //button3.setEnabled(false);
        button1.setOnClickListener(null);
        button2.setOnClickListener(null);
        button3.setOnClickListener(null);
    }

    private Boolean onLongClick(View v) {
        newQuest();
        return true;
    }


    private void onClick(View v) {
        TextView text = (TextView) findViewById(R.id.winstate);
        TextView button = (TextView) findViewById(v.getId());
        Handler handler = new Handler();
        TextView button1 = (TextView) findViewById(R.id.button1);
        TextView button2 = (TextView) findViewById(R.id.button2);
        TextView button3 = (TextView) findViewById(R.id.button3);

        for (int i = 0; i < 3; i++) {
            if (v.getId() != ids[i]) {
                ((TextView) findViewById(ids[i])).setAlpha((float)0.3);
            }
        }

        if (v.getId() == ids[nyerobuttonid]) {
            text.setText("YES!");
            if (!errorInQuest) {
                hit++;
            }
            total++;
            errorInQuest = false;
            disable();
            button.setTextColor(Color.GREEN);



            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    text.setText("");

                    Calendar nowTime = Calendar.getInstance();
                    long milliseconds1 = startTime.getTimeInMillis();
                    long milliseconds2 = nowTime.getTimeInMillis();

                    long diff = milliseconds2 - milliseconds1;
                    //long diffminutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                    if (diff > TimeUnit.MINUTES.toMillis(minutes)) {
                        button1.setVisibility(View.GONE);
                        button2.setVisibility(View.GONE);
                        button3.setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.haha1)).setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.remaining)).setVisibility(View.GONE);
                        text.setText("END");
                        if (nextgame) {
                            text.setText("NEXT");
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(LesserGreater.this, Capitals.class);
                                    Bundle b = new Bundle();
                                    b.putInt("timeToPlay", minutes); //Your id
                                    intent.putExtras(b); //Put your id to your next Intent
                                    startActivity(intent);
                                    finish();
                                }
                            }, 4000);
                        } else {
                            text.setText("END");
                        }                    } else {
                        button.setTextColor(colorsave);

                        reEnable();
                        newQuest();
                    }
                }
            }, 4000);

        } else {
            disable();
            text.setText("NO NO!");
            errorInQuest = true;

            button.setTextColor(Color.RED);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    button.setTextColor(colorsave);
                    text.setText("");
                    reEnable();


                }
            }, 2000);

        }

        TextView results = (TextView) findViewById(R.id.results);
        results.setText(String.valueOf(hit) + " / " + String.valueOf(total)+"\n"+String.valueOf((int) Math.round((double)hit/total*100)) + "%");
    }

    private int getRandNotEqual(ArrayList<Integer> e) {
        Random rand = new Random();

        while(true) {
            int a = rand.nextInt(a_max - a_min + 1) + a_min;
            if (!e.contains(a)) {
                return a;
            }
        }
    }

    private void newQuest() {
        Handler handler = new Handler();
        CheckBox game_mode = (CheckBox) findViewById(R.id.afrikacheck);
        CheckBox lesser_mode = (CheckBox) findViewById(R.id.lesserMode);

        if (game_mode.isChecked()) {
            a_min = 0;
            a_max = 1000;
        } else {
            a_min = 0;
            a_max = 100;
        }
        Random rand = new Random();
        int a = 0;
        int b = 0;
        int c = 0;
        ArrayList<Integer> eddigiek = new ArrayList<Integer>();

        a = getRandNotEqual(eddigiek);
        eddigiek.add(a);
        b = getRandNotEqual(eddigiek);
        eddigiek.add(b);
        c = getRandNotEqual(eddigiek);
        eddigiek.add(c);
        Boolean bigger = true;
        TextView text = (TextView) findViewById(R.id.haha1);
        int decide = rand.nextInt(1 - 0 + 1) + 0;
        if (!lesser_mode.isChecked()) {
            decide = 1;
        }
        if (decide == 0) {
            bigger = false;
            text.setText("⬇︎");
            solution = Collections.min(Arrays.asList(a, b, c));
        } else {
            text.setText("⬆︎");
            bigger = true;
            solution = Collections.max(Arrays.asList(a, b, c));
        }
        nyerobuttonid = eddigiek.indexOf(solution);

        TextView button1 = (TextView) findViewById(R.id.button1);
        TextView button2 = (TextView) findViewById(R.id.button2);
        TextView button3 = (TextView) findViewById(R.id.button3);
        button1.setText(String.valueOf(a));
        button2.setText(String.valueOf(b));
        button3.setText(String.valueOf(c));

        button1.setVisibility(View.GONE);
        button2.setVisibility(View.GONE);
        button3.setVisibility(View.GONE);
        button1.setOnClickListener(null);
        button3.setOnClickListener(null);
        button2.setOnClickListener(null);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                reEnable();

            }
        }, 500);

    }
}