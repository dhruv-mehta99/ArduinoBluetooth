package com.innoventx.bluetoothcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class BluetoothController extends AppCompatActivity implements View.OnClickListener {

    Button start,stop,key_bind;
    ConnectThread mConnectThread = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_controller);

        Log.e("TAG", "onCreate: ");

        findViewById(R.id.mainlayout).setVisibility(View.INVISIBLE);
        findViewById(R.id.progress_circular).setActivated(true);
        BluetoothDevice bluetoothDevice = (BluetoothDevice) getIntent().getParcelableExtra("KEY_DEVICE");
        BluetoothManager mbluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bTAdapter=mbluetoothManager.getAdapter();

        TextView deviceName = findViewById(R.id.tv_device_name);
        deviceName.setText(bluetoothDevice.getName());

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        BroadcastReceiver_Bluetooth mBTStateUpdateReceiver = new BroadcastReceiver_Bluetooth(getApplicationContext(),this);
        registerReceiver(mBTStateUpdateReceiver, filter);


        try {
            mConnectThread = new ConnectThread(bluetoothDevice,bTAdapter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mConnectThread.start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("TAG", "onStart: ");

        findViewById(R.id.btn_key_binding).setOnClickListener(this);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        findViewById(R.id.btn_up).setOnClickListener(this);
        findViewById(R.id.btn_down).setOnClickListener(this);
        findViewById(R.id.btn_left).setOnClickListener(this);
        findViewById(R.id.btn_right).setOnClickListener(this);
        findViewById(R.id.btn_circle).setOnClickListener(this);
        findViewById(R.id.btn_square).setOnClickListener(this);
        findViewById(R.id.btn_cross).setOnClickListener(this);
        findViewById(R.id.btn_triangle).setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mConnectThread.cancel();
    }

    @Override
    public void onClick(View v) {

        SharedPreferences sharedPref = getSharedPreferences(getPackageName(),Context.MODE_PRIVATE);

        switch (v.getId()){
            case R.id.btn_start:
                Log.d("BluetoothController", "start button clicked");
                mConnectThread.write(sharedPref.getString(getString(R.string.key_binding_start),""));
                break;
            case R.id.btn_stop:
                mConnectThread.write(sharedPref.getString(getString(R.string.key_binding_stop),""));
                break;
            case R.id.btn_up:
                mConnectThread.write(sharedPref.getString(getString(R.string.key_binding_up),""));
                break;
            case R.id.btn_down:
                Log.d("BluetoothController", "down button clicked"+sharedPref.contains(getString(R.string.key_binding_down)));
                mConnectThread.write(sharedPref.getString(getString(R.string.key_binding_down),""));
                break;
            case R.id.btn_left:
                mConnectThread.write(sharedPref.getString(getString(R.string.key_binding_left),""));
                break;
            case R.id.btn_right:
                mConnectThread.write(sharedPref.getString(getString(R.string.key_binding_right),""));
                break;
            case R.id.btn_square:
                mConnectThread.write(sharedPref.getString(getString(R.string.key_binding_square),""));
                break;
            case R.id.btn_circle:
                mConnectThread.write(sharedPref.getString(getString(R.string.key_binding_circle),""));
                break;
            case R.id.btn_triangle:
                mConnectThread.write(sharedPref.getString(getString(R.string.key_binding_triangle),""));
                break;
            case R.id.btn_cross:
                mConnectThread.write(sharedPref.getString(getString(R.string.key_binding_cross),""));
                break;
            case R.id.btn_key_binding:
                Intent i = new Intent(BluetoothController.this,KeyBindings.class);
                startActivity(i);
                break;




        }
    }
}