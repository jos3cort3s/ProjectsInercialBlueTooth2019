package com.jos3ocrt3s.appbluetoothme.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Icon;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jos3ocrt3s.appbluetoothme.R;
import com.jos3ocrt3s.appbluetoothme.activitiesBLE.DeviceControlActivity;
import com.jos3ocrt3s.appbluetoothme.model.ModelDataWearable;
import com.jos3ocrt3s.appbluetoothme.model.ModelRegistroUser;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

public class SensorMovilActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensorGravity, mSensorLinear, mSensorRotation;
    private FloatingActionButton btnFloatingReadSensor;
    private TextView tvBoxDataSensor;
    private boolean onClickF = false;
    private double ax, ay, az;

    private EditText etBoxTittle;
    private ImageButton ibtnBoxAceptBoard, ibtnBoxCancelBoard;




    //Data base Realm Variables
    private Realm mRealm;
    private ModelDataWearable mDataWearable;
    private ModelRegistroUser mRegistroUser;
    private RealmList<ModelDataWearable> mListRealmDataWearable  = new RealmList<>();
    List<String> plain = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_movil);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        addId();
        sensorActivate();
        mRealm =  Realm.getDefaultInstance();
        onClickBtnFloating();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_historyData_scan:
                Intent goHistory =  new Intent(SensorMovilActivity.this, HistoryListActivity.class);
                startActivity(goHistory);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void addId(){
        btnFloatingReadSensor =  findViewById(R.id.fabMovilSensor);
        tvBoxDataSensor = findViewById(R.id.tv_BoxDataMovil);
    }



    private void sensorActivate(){

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorLinear = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME); //Modifico la frecuencia de muestreo del acelelerometro

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            ax=event.values[0];
            ay=event.values[1];
            az=event.values[2];
            DecimalFormat mFormat =  new DecimalFormat("#.####");
            if(onClickF)stringToDataWearable(mFormat.format(ax), mFormat.format(ay), mFormat.format(az));
            tvBoxDataSensor.setText("Eje X: "+mFormat.format(ax)+"\nEje Y: "+mFormat.format(ay)+"\nEje Z: "+ mFormat.format(az));

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    private void onClickBtnFloating(){
        btnFloatingReadSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickF){
                    onClickF=false;
                    onPause();
                    btnFloatingReadSensor.setImageIcon(Icon.createWithResource(SensorMovilActivity.this, R.drawable.ic_play));
                    showBoxAlertDialog();
                }
                else{
                    onClickF=true;
                    btnFloatingReadSensor.setImageIcon(Icon.createWithResource(SensorMovilActivity.this, R.drawable.ic_stop));
                    }
            }
        });

    }


    private void  showBoxAlertDialog(){

        final Dialog boxAddNoteDialog =  new Dialog(this);
        boxAddNoteDialog.setContentView(R.layout.layout_card_box_add_board);
        etBoxTittle = boxAddNoteDialog.findViewById(R.id.etBoxTittleBoard);
        ibtnBoxCancelBoard = boxAddNoteDialog.findViewById(R.id.btnBoxCalcel);
        ibtnBoxAceptBoard =  boxAddNoteDialog.findViewById(R.id.btnBoxAcept);


        ibtnBoxCancelBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boxAddNoteDialog.cancel();

            }
        });

        ibtnBoxAceptBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etBoxTittle.getText().toString().trim().isEmpty()){
                    Toast.makeText(SensorMovilActivity.this, "Tittle is empty", Toast.LENGTH_SHORT).show();}
                else{
                    createNewUser(etBoxTittle.getText().toString(),mListRealmDataWearable);
                    boxAddNoteDialog.dismiss();
                    Toast.makeText(SensorMovilActivity.this, "New Register Created", Toast.LENGTH_SHORT).show();}


            }
        });

        boxAddNoteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        boxAddNoteDialog.show();

    }



    private RealmList<ModelDataWearable> stringToDataWearable(String dataX, String dataY, String dataZ){
        int i = 0;
        mDataWearable = new ModelDataWearable(i++, dataX, dataY, dataZ);
        mListRealmDataWearable.add(mDataWearable);
        return mListRealmDataWearable;
    }


    //CRUD Realm DataBase

    private void createNewUser(String nameUser, RealmList<ModelDataWearable> allDataWearable) {
        mRealm.beginTransaction();
        mRegistroUser =  new ModelRegistroUser(nameUser, allDataWearable);
        mRealm.copyToRealm(mRegistroUser);
        mRealm.commitTransaction();
    }





}
