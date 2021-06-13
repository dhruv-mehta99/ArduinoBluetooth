package com.dhruv.arduinobluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class BluetoothController extends AppCompatActivity {

    Button start,stop;
    ConnectThread mConnectThread = null;
    private final BroadcastReceiver BTReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Do something if connected
                Toast.makeText(context, "BT Connected", Toast.LENGTH_SHORT).show();
                start.setEnabled(true);
                stop.setEnabled(true);

            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Do something if disconnected
                Toast.makeText(context, "BT Disconnected", Toast.LENGTH_SHORT).show();
                start.setEnabled(false);
                stop.setEnabled(false);
            }
            //else if...
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_controller);
        findViewById(R.id.linear_layout1).setVisibility(View.INVISIBLE);
        findViewById(R.id.progress_bar).setActivated(true);
        BluetoothDevice bluetoothDevice = (BluetoothDevice) getIntent().getParcelableExtra("KEY_DEVICE");
        BluetoothManager mbluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bTAdapter=mbluetoothManager.getAdapter();

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

        start=findViewById(R.id.btn_start);
        stop=findViewById(R.id.btn_stop);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BluetoothController", "start button clicked");
                mConnectThread.write("a");
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BluetoothController", "stop button clicked");
                mConnectThread.write("b");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mConnectThread.cancel();
    }
}