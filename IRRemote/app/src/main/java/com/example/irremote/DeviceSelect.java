package com.example.irremote;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class DeviceSelect extends AppCompatActivity {

    BluetoothHandler bth;
    public DeviceSelect(BluetoothHandler bth)
    {
        this.bth=bth;
    }
    public void on(View View)
    {
        bth.write("nec 61184 3");
    }
    public void off(View view)
    {
        bth.write("nec 61184 2");
    }
    public void turnRed(View view)
    {
//        bth.write("nec 61184 2");
    }
    public void turnBlue(View view)
    {
//        bth.write("nec 61184 2");
    }
    public void turnGreen(View view)
    {
//        bth.write("nec 61184 2");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_select);
    }
}