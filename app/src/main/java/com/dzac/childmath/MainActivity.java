package com.dzac.childmath;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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


    }

}