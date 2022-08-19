package com.dzac.childmath;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.LruCacheKt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnPlusMinus = (Button)findViewById(R.id.buttonPlusMinus);
        Button btnLesserGreater = (Button)findViewById(R.id.buttonLesserGreater);

        btnPlusMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlusMinus.class);
                startActivity(intent);

            }
        });

        btnLesserGreater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LesserGreater.class);
                startActivity(intent);

            }
        });

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if (!sharedPref.contains("autopin")) {
            editor.putBoolean("autopin", false);
            editor.commit();
        }
        Boolean autopin = sharedPref.getBoolean("autopin", false);

        CheckBox autopin_cbox = (CheckBox) findViewById(R.id.autoPin);
        autopin_cbox.setChecked(autopin);

        autopin_cbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("autopin", autopin_cbox.isChecked());
                editor.commit();
                if (autopin_cbox.isChecked()) {
                    startLockTask();
                }
            }
        });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        if (autopin_cbox.isChecked()) {
            startLockTask();
        }
    }

}