package com.example.irremote;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class BluetoothHandler extends AppCompatActivity implements Parcelable {

    public static String bluetoothKey="com.android.IRremoteApp.savedBluetoothKey";
    public static String dataName="com.adroid.IRremoteApp.sharedPreference";
    BluetoothSocket sock=null;
    SharedPreferences shrd = getSharedPreferences(dataName,MODE_PRIVATE);
    SharedPreferences.Editor editor = shrd.edit();

    public boolean isBluetoothEnabled()
    {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        return btAdapter.isEnabled();
    }
    public void getAllDeviceAddress()
    {
        if(!isBluetoothEnabled())
        {
            Toast.makeText(this,"Please Turn On Bluetooth",Toast.LENGTH_SHORT).show();
        }
        else
        {
            ArrayList<String> deviceStrs = new ArrayList<>();
            final ArrayList<String> devices = new ArrayList<>();

            BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    deviceStrs.add(device.getName() + "\n" + device.getAddress());
                    devices.add(device.getAddress());
                    Log.d("namaste", "Name = " + device.getName() + "\t Address = " + device.getAddress());
                }
            }
            showDeviceSelecterDialog(deviceStrs, devices);
        }
    }
    public void showDeviceSelecterDialog(ArrayList<String> deviceStrs, ArrayList<String> devices)
    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_singlechoice,
                deviceStrs.toArray(new String[deviceStrs.size()]));

        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                int position = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                String deviceAddress = (String)devices.get(position);
                editor.putString(bluetoothKey,deviceAddress);
                startConnection(deviceAddress);
            }
        });

        alertDialog.setTitle("Choose Bluetooth Device");
        alertDialog.show();
    }
    public void startConnection(String deviceAddress)
    {
        if(deviceAddress==null || deviceAddress.equals(""))
        {
            Log.d("namaste","No Bluetooth Device has been selected");
            Toast.makeText(this,"No BT device selected",Toast.LENGTH_SHORT).show();
        }
        else
        {
            final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice dev = btAdapter.getRemoteDevice(deviceAddress);

            Log.d("namaste","Stopping BT discovery");
            btAdapter.cancelDiscovery();

            try
            {
                ParcelUuid[] uuids = dev.getUuids();
                sock = dev.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                sock.connect();

                Log.d("namaste","Connected");
                Toast.makeText(this,"Connected",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this,DeviceSelect.class);
                intent.putExtra(MainActivity.BTH,this);
                startActivity(intent);
            }
            catch(Exception e)
            {
                Toast.makeText(this,"Error occured",Toast.LENGTH_SHORT).show();
                Log.d("namaste",e.toString());
            }
        }
    }


    public void write(String data)
    {
        try
        {
            sock.getOutputStream().write(data.getBytes());
        }
        catch(Exception e)
        {
            Log.d("namaste","Could not send data");
        }
    }
    public String readRawData(InputStream in) throws IOException
    {
        byte b=0;
        StringBuilder res = new StringBuilder();
        while(true)
        {
            b=(byte)in.read();
            if(b==-1)
                break;
            char c=(char)b;
            if(c=='>')
                break;
            res.append(c);
        }
        return res.toString();
    }
}
