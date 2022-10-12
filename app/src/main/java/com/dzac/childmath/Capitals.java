package com.dzac.childmath;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Capitals extends AppCompatActivity {

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
    public HashMap<String, String> europaMap;
    public HashMap<String, String> asiaMap;
    public HashMap<String, String> afrikaMap;
    public ArrayList<String> europaRemain;
    public ArrayList<String> asiaRemain;
    public ArrayList<String> afrikaRemain;
    public ArrayList<String> kontinensek = new ArrayList<String> ();
    public HashMap<String, HashMap> dbk = new HashMap<String, HashMap>();
    public HashMap<String, ArrayList> remainsDbk = new HashMap<String, ArrayList>();
    public ArrayList<Integer> idslist = new ArrayList<Integer>();
    public HashMap<String, CheckBox> kontiChecks = new HashMap<String, CheckBox>();
    public HashMap<String, String> kontiNevek = new HashMap<String, String>();
    public String lastKonti;
    public String lastOrszag;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capitals);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        try {
            europaMap = getCountries(R.raw.europa);
            asiaMap = getCountries(R.raw.asia);
            afrikaMap = getCountries(R.raw.afrika);
        } catch (IOException e) {
            e.printStackTrace();
        }

        europaRemain = new ArrayList<String>(europaMap.keySet());
        asiaRemain = new ArrayList<String>(asiaMap.keySet());
        afrikaRemain = new ArrayList<String>(afrikaMap.keySet());

        kontiChecks.put("europa", findViewById(R.id.europacheck));
        kontiChecks.put("asia", findViewById(R.id.asiacheck));
        kontiChecks.put("afrika", findViewById(R.id.afrikacheck));

        kontinensek.add("europa");
        kontinensek.add("asia");
        kontinensek.add("afrika");
        kontiNevek.put("europa", "Európa");
        kontiNevek.put("asia", "Ázsia");
        kontiNevek.put("afrika", "Afrika");

        dbk.put("europa", europaMap);
        remainsDbk.put("europa", europaRemain);
        dbk.put("asia", asiaMap);
        remainsDbk.put("asia", asiaRemain);
        dbk.put("afrika", afrikaMap);
        remainsDbk.put("afrika", afrikaRemain);

        CheckBox europeCheck = (CheckBox) findViewById(R.id.europacheck);
        CheckBox asiaCheck = (CheckBox) findViewById(R.id.asiacheck);
        CheckBox afrikaCheck = (CheckBox) findViewById(R.id.afrikacheck);
        europeCheck.setText("Európa " + remainsDbk.get("europa").size());
        asiaCheck.setText("Ázsia " + remainsDbk.get("asia").size());
        afrikaCheck.setText("Afrika " + remainsDbk.get("afrika").size());

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
        showTimeDialog();
        try {
            newQuest();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {

    }

    public HashMap<String, String> getCountries(int res) throws IOException {
        String line =  null;
        Context context = getApplicationContext();

        InputStream raw = getResources().openRawResource(res);

        Reader is = new BufferedReader(new InputStreamReader(raw, "UTF8"));
        HashMap<String,String> map = new HashMap<String, String>();
        BufferedReader br = new BufferedReader(is);

        while((line = br.readLine()) != null) {
            String str[] = line.split(",");
            map.put(str[0], str[1]);
        }
        return map;
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
        try {
            newQuest();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    private void onClick(View v) {
        TextView text = (TextView) findViewById(R.id.winstate);
        TextView button = (TextView) findViewById(v.getId());
        Handler handler = new Handler();
        TextView button1 = (TextView) findViewById(R.id.button1);
        TextView button2 = (TextView) findViewById(R.id.button2);
        TextView button3 = (TextView) findViewById(R.id.button3);
        CheckBox europeCheck = (CheckBox) findViewById(R.id.europacheck);
        CheckBox asiaCheck = (CheckBox) findViewById(R.id.asiacheck);
        CheckBox afrikaCheck = (CheckBox) findViewById(R.id.afrikacheck);


        for (int i = 0; i < 3; i++) {
            if (v.getId() != ids[i]) {
                ((TextView) findViewById(ids[i])).setAlpha((float)0.3);
            }
        }

        if (v.getId() == ids[nyerobuttonid]) {
            text.setText("YES!");
            if (!errorInQuest) {
                remainsDbk.get(lastKonti).remove(lastOrszag);
                hit++;
            }
            total++;
            errorInQuest = false;
            disable();
            button.setTextColor(Color.GREEN);

            europeCheck.setText("Európa " + remainsDbk.get("europa").size());
            asiaCheck.setText("Ázsia " + remainsDbk.get("asia").size());
            afrikaCheck.setText("Afrika " + remainsDbk.get("afrika").size());

            for (String konti : kontinensek) {
                if (remainsDbk.get(konti).size() == 0) {
                    CheckBox thisbox = kontiChecks.get(konti);
                    thisbox.setEnabled(false);
                    thisbox.setChecked(false);
                }
            }


            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    text.setText("");

                    Calendar nowTime = Calendar.getInstance();
                    long milliseconds1 = startTime.getTimeInMillis();
                    long milliseconds2 = nowTime.getTimeInMillis();

                    long diff = milliseconds2 - milliseconds1;
                    //long diffminutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                    if (diff > TimeUnit.MINUTES.toMillis(minutes) || noMoreCountries()) {
                        button1.setVisibility(View.GONE);
                        button2.setVisibility(View.GONE);
                        button3.setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.haha1)).setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.remaining)).setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.kontiText)).setVisibility(View.GONE);
                        text.setText("END");
                    } else {
                        button.setTextColor(colorsave);

                        reEnable();
                        try {
                            newQuest();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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

    private boolean noMoreCountries() {
        for (String konti : kontinensek) {
            if (remainsDbk.get(konti).size() > 0) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<String> getRandNotEqual(String from) {

        Random rand = new Random();
        ArrayList<String> tmp = remainsDbk.get(from);
        Log.d("meretem", String.valueOf(tmp.size()));
        ArrayList<String> ret = new ArrayList<String>();
        ArrayList<String> osszes_orszag_kont = new  ArrayList<String>(dbk.get(from).keySet());
        String valasztott_orszag = tmp.get(rand.nextInt(tmp.size()));

        Log.d("valasztott orszag", valasztott_orszag);
        osszes_orszag_kont.remove(valasztott_orszag);
        ret.add(valasztott_orszag);

        for (int i = 0; i < 2; i++) {
            String orszag = osszes_orszag_kont.get(rand.nextInt(osszes_orszag_kont.size()));
            osszes_orszag_kont.remove(orszag);
            ret.add(orszag);
        }
        return ret;
    }

    private void newQuest() throws IOException {
        Handler handler = new Handler();
        TextView text = (TextView) findViewById(R.id.haha1);
        TextView kontiText = (TextView) findViewById(R.id.kontiText);

        CheckBox europeCheck = (CheckBox) findViewById(R.id.europacheck);
        CheckBox asiaCheck = (CheckBox) findViewById(R.id.asiacheck);
        CheckBox afrikaCheck = (CheckBox) findViewById(R.id.afrikacheck);


        Random rand = new Random();
        ArrayList<String> eddigiek = new ArrayList<String>();

        idslist.add(R.id.button1);
        idslist.add(R.id.button2);
        idslist.add(R.id.button3);



        ArrayList<String> enableds = getEnableds();

        String valasztott_kont = enableds.get(rand.nextInt(enableds.size()));

        eddigiek = getRandNotEqual(valasztott_kont);

        lastKonti = valasztott_kont;
        lastOrszag = eddigiek.get(0);

        int nyeroszam = rand.nextInt(idslist.size());
        int nyeroid = idslist.remove(nyeroszam);

        TextView button1 = (TextView) findViewById(nyeroid);
        TextView button2 = (TextView) findViewById(idslist.remove(rand.nextInt(idslist.size())));
        TextView button3 = (TextView) findViewById(idslist.remove(rand.nextInt(idslist.size())));

        nyerobuttonid = nyeroszam;

        HashMap<String, String> valasztott_db = dbk.get(valasztott_kont);
        button1.setText(valasztott_db.get(eddigiek.get(0)));
        button2.setText(valasztott_db.get(eddigiek.get(1)));
        button3.setText(valasztott_db.get(eddigiek.get(2)));

        text.setText(eddigiek.get(0));
        kontiText.setText(kontiNevek.get(valasztott_kont));
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

    private ArrayList<String> getEnableds() {
        ArrayList<String> ret = new ArrayList<String>();
        for (String konti : kontinensek) {
            CheckBox thisbox = kontiChecks.get(konti);
            if (thisbox.isChecked()) {
                ret.add(konti);
            }
        }
        if (ret.size() == 0) {
            for (String konti : kontinensek) {
                if (remainsDbk.get(konti).size() > 0) {
                    CheckBox thisbox = kontiChecks.get(konti);
                    thisbox.setChecked(true);
                    ret.add(konti);
                    break;
                }
            }
        }
        return ret;
    }
}