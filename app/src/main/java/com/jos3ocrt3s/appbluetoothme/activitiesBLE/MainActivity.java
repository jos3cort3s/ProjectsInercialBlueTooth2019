package com.jos3ocrt3s.appbluetoothme.activitiesBLE;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.jos3ocrt3s.appbluetoothme.R;
import com.jos3ocrt3s.appbluetoothme.activities.HistoryListActivity;
import com.jos3ocrt3s.appbluetoothme.adapters.AdapterList;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    // Elements UI variables
    private FloatingActionButton fabtn_scan;
    private ListView lv_listDevice;

    // Basic Adapter variables
    private AdapterList mListAdapter;

    // Bluetooth variables
    private BluetoothAdapter mBluetoothAdapter;
    private static final long SCAN_PERIOD = 5000;
    private boolean mScanning;
    private Handler mHandler;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothDevice mBluetoothDevice;
    private List<BluetoothDevice> mListBluetoothDevices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        blueToothAvailable();
        callId();
        eventsButtons();
        eventsLists();

        mHandler =  new Handler();
        //Context mContext = getBaseContext();
        BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mListBluetoothDevices =  new ArrayList<>();

    }


    private void scanLeDevice(final boolean enable) {

        bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .build();
        List<ScanFilter> filters = new ArrayList<ScanFilter>();

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            bluetoothLeScanner.startScan(filters, settings, mLeScanCallback);
        } else {
            mScanning = false;
            bluetoothLeScanner.stopScan(mLeScanCallback);
        }
    }

    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if(!mListBluetoothDevices.contains(result.getDevice())){
                mListBluetoothDevices.add(result.getDevice());}
            mListAdapter =  new AdapterList(MainActivity.this, mListBluetoothDevices);
            lv_listDevice.setAdapter(mListAdapter);

        }
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Toast.makeText(MainActivity.this, "There isn't any device", Toast.LENGTH_SHORT).show();
        }
    };

    private void callId(){
        fabtn_scan =  findViewById(R.id.fabtn_scan);
        lv_listDevice =  findViewById(R.id.lv_listDevice);
    }

    private void eventsButtons(){

        fabtn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initializes list view adapter.
                scanLeDevice(true);



            }
        });

    }

    private void eventsLists(){
        lv_listDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent goActivityConnect = new Intent(MainActivity.this, DeviceControlActivity.class);
                /*String nameDevice = mListAdapter.getDevice(i).getName();
                String adressDevice = mListAdapter.getDevice(i).getAddress();*/
                goActivityConnect.putExtra("deviceCheck", mListBluetoothDevices.get(i));
                startActivity(goActivityConnect);
            }
        });
    }

    private void blueToothAvailable(){

        BluetoothManager mbluetoothManager =
                (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mbluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }

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
                Intent goHistory =  new Intent(MainActivity.this, HistoryListActivity.class);
                startActivity(goHistory);
                return true;
             default:
                 return super.onOptionsItemSelected(item);
        }


    }

    ////////-------Other Library for Scanning device BLE--------------

   /* private void ScanDeviceBleNordicLibrary(){
        BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(5000)
                .build();
        List<ScanFilter> filters = new ArrayList<>();
        filters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(UUID.randomUUID())).build());
        scanner.startScan(filters, scanner, );
    }*/
}
