/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jos3ocrt3s.appbluetoothme.activitiesBLE;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jos3ocrt3s.appbluetoothme.R;
import com.jos3ocrt3s.appbluetoothme.activities.HistoryListActivity;
import com.jos3ocrt3s.appbluetoothme.model.ModelDataWearable;
import com.jos3ocrt3s.appbluetoothme.model.ModelRegistroUser;
import com.jos3ocrt3s.appbluetoothme.utils.SampleGattAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends AppCompatActivity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView tv_connectionState;
    private TextView tv_deviceAddress;
    private TextView tv_dataField;
    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private FloatingActionButton floatingRight, floatingLeft;
    private BluetoothDevice mBluetoothDevice;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private String unknownServiceString;
    private String unknownCharaString;

    //*******************************
    List<String> plain = new ArrayList<String>();
    List<String> plainF = new ArrayList<String>();
    //*******************************


    //Alert Dialog Variables

    private EditText etBoxTittle;
    private ImageButton ibtnBoxAceptBoard, ibtnBoxCancelBoard;

    //Flag floating button Save Data
    private boolean flagFbtn = false;

    //Flag menu connect and disconnect
    private boolean flagMenu = false;

    //Data base Realm Variables
    private Realm mRealm;
    private ModelDataWearable mDataWearable;
    private ModelRegistroUser mRegistroUser;
    private RealmList<ModelDataWearable> mListRealmDataWearable  = new RealmList<>();


    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mBluetoothDevice);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();

                } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));

                if (intent.getStringArrayListExtra(BluetoothLeService.EXTRA_DATA) != null ) {
                    if (intent.getStringArrayListExtra(BluetoothLeService.EXTRA_DATA).size()>0) {
                        plain.clear();
                        plain.addAll(intent.getStringArrayListExtra(BluetoothLeService.EXTRA_DATA));
                        /*tv_dataField.setText(plain.get(0) + ", " + plain.get(3) + ", " + plain.get(6) + "\n" +
                                            ", " + plain.get(1) + ", " + plain.get(4) + ", " + plain.get(7) + "\n" +
                                            ", " + plain.get(2) + ", " + plain.get(5) + ", " + plain.get(8));*/ // muestra el pantalla las 3 muestra al mismo tiempo
                        tv_dataField.setText("Eje X: " + plain.get(0) + "\nEje Y: " + plain.get(3) + "\nEje Z: " + plain.get(6));
                        if(flagFbtn)stringToDataWearable(plain);

                    }
                }
            }
        }
    };


    private void displayData(String data) {
        if (data != null && (tv_dataField.getText().toString().isEmpty())) {
            tv_dataField.setText(data);
        }else tv_dataField.setText(tv_dataField.getText()+", "+data);
    }



    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);

                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                            //mBluetoothLeService.readCustomCharacteristic();
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(
                                    characteristic, true);
                        }
                        return true;
                    }
                    return false;
                }
    };

    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        tv_dataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_second);
        callId();
        mRealm =  Realm.getDefaultInstance();
        getIntentExtras();
        eventsFloatingBtn();

       /* final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);*/


        //getActionBar().setTitle(mDeviceName);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(DeviceControlActivity.this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void callId(){
        // Sets up UI references.
        tv_deviceAddress = findViewById(R.id.tv_address);
        tv_connectionState = findViewById(R.id.tv_state);
        tv_dataField = findViewById(R.id.tv_data);
        mGattServicesList = findViewById(R.id.expande_list_service);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);

        floatingRight = findViewById(R.id.fab);
        //floatingLeft =  findViewById(R.id.fab_read);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mBluetoothDevice);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connet:
                if(flagMenu){
                   mBluetoothLeService.disconnect();
                   mBluetoothLeService.close();
                   flagMenu = false;}
                else{
                    mBluetoothLeService.connect(mBluetoothDevice);
                    flagMenu = true;}

                return true;

            case R.id.menu_historyData:
                Intent goHistory =  new Intent(DeviceControlActivity.this, HistoryListActivity.class);
                startActivity(goHistory);
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private RealmList<ModelDataWearable> stringToDataWearable(List<String> data){
            int i = 0;
            for(int j=0; j<3; j++){
                mDataWearable = new ModelDataWearable(i++, data.get(j), data.get(j+3), data.get(j+6));
                mListRealmDataWearable.add(mDataWearable);
            }
        return mListRealmDataWearable;
    }

    private void eventsFloatingBtn(){

        floatingRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flagFbtn){
                    flagFbtn=false;
                    floatingRight.setImageIcon(Icon.createWithResource(DeviceControlActivity.this, R.drawable.ic_play));
                    showBoxAlertDialog();



                }else {
                    flagFbtn=true;
                    floatingRight.setImageIcon(Icon.createWithResource(DeviceControlActivity.this, R.drawable.ic_stop));

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
                    Toast.makeText(DeviceControlActivity.this, "Tittle is empty", Toast.LENGTH_SHORT).show();}
                else{
                    createNewUser(etBoxTittle.getText().toString(),mListRealmDataWearable);
                    boxAddNoteDialog.dismiss();
                    mListRealmDataWearable.clear();
                    Toast.makeText(DeviceControlActivity.this, "New Register Created", Toast.LENGTH_SHORT).show();}


            }
        });

        boxAddNoteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        boxAddNoteDialog.show();

    }

    private void getIntentExtras () {
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mBluetoothDevice = (BluetoothDevice) mBundle.get("deviceCheck");
            tv_deviceAddress.setText(mBluetoothDevice.getName());
        }
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_connectionState.setText(resourceId);
            }
        });
    }



    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        unknownServiceString = getResources().getString(R.string.unknown_service);
        unknownCharaString = getResources().getString(R.string.unknown_chara);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mGattServicesList.setAdapter(gattServiceAdapter);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    //CRUD Realm DataBase

    private void createNewUser(String nameUser, RealmList<ModelDataWearable> allDataWearable) {
        mRealm.beginTransaction();
        mRegistroUser =  new ModelRegistroUser(nameUser, allDataWearable);
        mRealm.copyToRealm(mRegistroUser);
        mRealm.commitTransaction();
    }






}
