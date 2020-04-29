package com.jos3ocrt3s.appbluetoothme.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jos3ocrt3s.appbluetoothme.R;
import com.jos3ocrt3s.appbluetoothme.activitiesBLE.MainActivity;

public class MainActivityChoose extends AppCompatActivity {

    private Button btnChooseWear, btnChooseMovil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_choose);
        getId();
        onClickButtons();

    }
    private void onClickButtons (){
        btnChooseMovil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivityChoose.this, "Movil", Toast.LENGTH_SHORT).show();
                Intent goMovilSensorActivity =  new Intent(MainActivityChoose.this, SensorMovilActivity.class);
                startActivity(goMovilSensorActivity);
            }
        });

        btnChooseWear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivityChoose.this, "Wearable", Toast.LENGTH_SHORT).show();
                Intent goWearableSensorActivity =  new Intent(MainActivityChoose.this, MainActivity.class);
                startActivity(goWearableSensorActivity);

            }
        });

    }

    private void getId(){
        btnChooseWear = findViewById(R.id.btnChooseWearable);
        btnChooseMovil = findViewById(R.id.btnChooseMovil);
    }
}
