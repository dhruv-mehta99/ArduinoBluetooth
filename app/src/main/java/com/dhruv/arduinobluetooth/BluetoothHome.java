package com.dhruv.arduinobluetooth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

public class BluetoothHome extends AppCompatActivity implements View.OnClickListener , AdapterView.OnItemClickListener {

    Toolbar toolbar;
    private final String TAG = "BluetoothHome";
    public static final int REQUEST_ENABLE_BT = 1;
    public static final int AVAILABLE_DEVICE_LIST = 0;
    public static final int PAIRED_DEVICE_LIST = 1;

    private HashMap<String, BTLE_Device> mBTDevicesHashMap;
    private ArrayList<BTLE_Device> mPairedBTDevicesArrayList,mAvailableBTDevicesArrayList;
    private ListAdapter_BTLE_Devices adapter_paired_device,adapter_avaialble_device;
    private static BluetoothAdapter bTAdapter;
    private ImageButton btn_Scan;

    private BroadcastReceiver_Bluetooth mBTStateUpdateReceiver;
    private Scanner_BTLE mBTLeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            BluetoothUtils.toast(getApplicationContext(), "BLE not supported");
            finish();
        }
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION},1);


        mBTStateUpdateReceiver = new BroadcastReceiver_Bluetooth(getApplicationContext(),this);
        mBTLeScanner = new Scanner_BTLE(this, 7500);

        mBTDevicesHashMap = new HashMap<>();
        mPairedBTDevicesArrayList = new ArrayList<>();
        mAvailableBTDevicesArrayList = new ArrayList<>();

        adapter_avaialble_device = new ListAdapter_BTLE_Devices(this, R.layout.btle_list_adapter_layout, mAvailableBTDevicesArrayList);

        ListView availableDevice = findViewById(R.id.listView_available_device);
        availableDevice.setAdapter(adapter_avaialble_device);
        availableDevice.setOnItemClickListener(this);

        adapter_paired_device = new ListAdapter_BTLE_Devices(this, R.layout.btle_list_adapter_layout, mPairedBTDevicesArrayList);

        ListView pairedDevice = findViewById(R.id.listView_paired_device);
        pairedDevice.setAdapter(adapter_paired_device);
        ConstraintLayout.LayoutParams list = (ConstraintLayout.LayoutParams) pairedDevice.getLayoutParams();
        list.height = 55*(getResources().getDisplayMetrics().heightPixels)/100;
        pairedDevice.setLayoutParams(list);
        pairedDevice.setOnItemClickListener(this);

        BluetoothManager mbluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        bTAdapter=mbluetoothManager.getAdapter();

        btn_Scan = findViewById(R.id.btn_scan);
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
        BluetoothDevice device = mPairedBTDevicesArrayList.get(position).getBluetoothDevice();
        Log.d("DEVICELIST", "onItemClick position: " + position +
                " id: " + id + " name: " + device.getName() + "\n");
        Intent i = new Intent(BluetoothHome.this,BluetoothController.class);
        i.putExtra("KEY_DEVICE",device);
        startActivity(i);

    }


    public void addDevice(BluetoothDevice device,int listName) {

        String address = device.getAddress();
        if (!mBTDevicesHashMap.containsKey(address)) {
            BTLE_Device btleDevice = new BTLE_Device(device);
            Log.d(TAG, "addDevice: "+btleDevice.getName());

            mBTDevicesHashMap.put(address, btleDevice);
            if(listName==PAIRED_DEVICE_LIST){
                mPairedBTDevicesArrayList.add(btleDevice);
                adapter_paired_device.notifyDataSetChanged();
            }else if(listName==AVAILABLE_DEVICE_LIST){
                mAvailableBTDevicesArrayList.add(btleDevice);
                adapter_avaialble_device.notifyDataSetChanged();
            }
        }


    }

    public void startScan() {
        BluetoothUtils.toast(this,"scan started");
        mBTLeScanner.start();
    }

    public void stopScan() {
        BluetoothUtils.toast(this,"scan stopped");
        mBTLeScanner.stop();
    }
}