package com.dhruv.arduinobluetooth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.HashMap;

public class BluetoothHome extends AppCompatActivity implements View.OnClickListener , AdapterView.OnItemClickListener {

    Toolbar toolbar;
    private final String TAG = "BluetoothHome";
    public static final int REQUEST_ENABLE_BT = 1;

    private HashMap<String, BTLE_Device> mBTDevicesHashMap;
    private ArrayList<BTLE_Device> mBTDevicesArrayList;
    private ListAdapter_BTLE_Devices adapter;
    private static BluetoothAdapter bTAdapter;
    private Button btn_Scan;

    private BroadcastReceiver_Bluetooth mBTStateUpdateReceiver;
    private Scanner_BTLE mBTLeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            BluetoothUtils.toast(getApplicationContext(), "BLE not supported");
            finish();
        }
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION},1);


        mBTStateUpdateReceiver = new BroadcastReceiver_Bluetooth(getApplicationContext(),this);
        mBTLeScanner = new Scanner_BTLE(this, 7500);

        mBTDevicesHashMap = new HashMap<>();
        mBTDevicesArrayList = new ArrayList<>();

        adapter = new ListAdapter_BTLE_Devices(this, R.layout.btle_device_list_item, mBTDevicesArrayList);

        ListView listView = new ListView(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        BluetoothManager mbluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        bTAdapter=mbluetoothManager.getAdapter();

        btn_Scan = (Button) findViewById(R.id.btn_scan);
        ((ScrollView) findViewById(R.id.scrollView)).addView(listView);
        btn_Scan.setOnClickListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBTStateUpdateReceiver, filter);
        mBTLeScanner.findPairedDevices();
        startScan();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mBTStateUpdateReceiver);
        stopScan();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Utils.toast(getApplicationContext(), "Thank you for turning on Bluetooth");
            }
            else if (resultCode == RESULT_CANCELED) {
                BluetoothUtils.toast(getApplicationContext(), "Please turn on Bluetooth");
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_scan:
                BluetoothUtils.toast(getApplicationContext(), "Scan Button Pressed");

                if (!mBTLeScanner.isScanning()) {
                    startScan();
                } else {
                    stopScan();
                }

                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice device = mBTDevicesArrayList.get(position).getBluetoothDevice();
        Log.d("DEVICELIST", "onItemClick position: " + position +
                " id: " + id + " name: " + device.getName() + "\n");
        Intent i = new Intent(BluetoothHome.this,BluetoothController.class);
        i.putExtra("KEY_DEVICE",device);
        startActivity(i);

    }


    public void addDevice(BluetoothDevice device) {

        String address = device.getAddress();
        if (!mBTDevicesHashMap.containsKey(address)) {
            BTLE_Device btleDevice = new BTLE_Device(device);
            Log.d(TAG, "addDevice: "+btleDevice.getName());

            mBTDevicesHashMap.put(address, btleDevice);
            mBTDevicesArrayList.add(btleDevice);
        }

        adapter.notifyDataSetChanged();
    }

    public void startScan() {
        btn_Scan.setText("Scanning...");
        mBTLeScanner.start();
    }

    public void stopScan() {
        btn_Scan.setText("Scan Again");
        mBTLeScanner.stop();
    }
}