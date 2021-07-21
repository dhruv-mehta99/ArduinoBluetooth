package com.innoventx.bluetoothcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class KeyBindings extends AppCompatActivity {

    EditText up,down,left,right,circle,square,triangle,cross,start,stop;
    Button done;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_bindings);
        sharedPref = getSharedPreferences(getPackageName(),Context.MODE_PRIVATE);
        up = findViewById(R.id.edt_up);
        down = findViewById(R.id.edt_down);
        left = findViewById(R.id.edt_left);
        right = findViewById(R.id.edt_right);
        square = findViewById(R.id.edt_pink_square);
        cross = findViewById(R.id.edt_blue_cross);
        triangle = findViewById(R.id.edt_green_arrow);
        circle = findViewById(R.id.edt_red_circle);
        start = findViewById(R.id.edt_start_btn);
        stop = findViewById(R.id.edt_stop_btn);

        up.setText(sharedPref.getString(getString(R.string.key_binding_up),""));
        down.setText(sharedPref.getString(getString(R.string.key_binding_down),""));
        left.setText(sharedPref.getString(getString(R.string.key_binding_left),""));
        right.setText(sharedPref.getString(getString(R.string.key_binding_right),""));
        square.setText(sharedPref.getString(getString(R.string.key_binding_square),""));
        circle.setText(sharedPref.getString(getString(R.string.key_binding_circle),""));
        cross.setText(sharedPref.getString(getString(R.string.key_binding_cross),""));
        triangle.setText(sharedPref.getString(getString(R.string.key_binding_triangle),""));
        start.setText(sharedPref.getString(getString(R.string.key_binding_start),""));
        stop.setText(sharedPref.getString(getString(R.string.key_binding_stop),""));


        findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.key_binding_up),up.getText().toString() );
                editor.putString(getString(R.string.key_binding_down),down.getText().toString() );
                Log.e("TAG", "onClick: down value is"+ down.getText().toString());
                editor.putString(getString(R.string.key_binding_left),left.getText().toString() );
                editor.putString(getString(R.string.key_binding_right),right.getText().toString() );
                editor.putString(getString(R.string.key_binding_cross),cross.getText().toString() );
                editor.putString(getString(R.string.key_binding_triangle),triangle.getText().toString() );
                editor.putString(getString(R.string.key_binding_square),square.getText().toString() );
                editor.putString(getString(R.string.key_binding_circle),circle.getText().toString() );
                editor.putString(getString(R.string.key_binding_stop),stop.getText().toString() );
                editor.putString(getString(R.string.key_binding_start),start.getText().toString() );
                editor.commit();
                KeyBindings.super.onBackPressed();
            }
        });

    }
}