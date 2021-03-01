package com.example.bluetoothscan;

import io.flutter.embedding.android.FlutterActivity;


import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;


public class MainActivity extends FlutterActivity {
    ArrayList<String> arrayListDemo = new ArrayList<String>();

    public static final String SCANCHANNEL="scanchannel";
    public static final String BONDCHANNEL="bondchannel";
    private static final String TAG = MainActivity.class.getSimpleName();
    public BluetoothAdapter bluetoothAdapter;
    public ArrayList<BluetoothDevice> mBTDevices=new ArrayList<>();
    public ArrayList<BluetoothDevice> bondedDevices=new ArrayList<>();
    public Set<BluetoothDevice> pairedDevices;
    //ArrayList<String> bondedlist = new ArrayList<String>();
    public ArrayList<String> bondedlist ;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine)
    {
        super.configureFlutterEngine(flutterEngine);
        // Scanning for bluetooth adapter
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        checkBluetoothState();
        // we register a dedicated receiver for some Bluetooth Actions
        registerReceiver(mBroadcastReceiver3,new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(mBroadcastReceiver3,new IntentFilter(bluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(mBroadcastReceiver3,new IntentFilter(bluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), SCANCHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            // Note: this method is invoked on the main thread.
                            // TODO
                            //  Map<String,Object> params=(Map<String,Object>) call.arguments;
                            if(call.method.equals("scanDeviceNativeFunction"))
                            {
                                //ArrayList<String> messageToFlutter=myNativeFunction();
                                 ArrayList<BluetoothDevice> mBTDevices=scanDeviceNativeFunction();
                                //scanDeviceNativeFunction();
                                //result.success(mBTDevices);
                            }else {
                                result.notImplemented();

                            }

                        }
                );

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), BONDCHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            // Note: this method is invoked on the main thread.
                            // TODO
                            //  Map<String,Object> params=(Map<String,Object>) call.arguments;
                            if(call.method.equals("getBondeddevice"))
                            {

                                ArrayList<String> msgToFlutter=getBondeddevice();
                                result.success(msgToFlutter);
                            }else {
                                result.notImplemented();

                            }

                        }
                );





    } // configureFlutter Engine ends




    @RequiresApi(api = Build.VERSION_CODES.M)
    private ArrayList<BluetoothDevice> scanDeviceNativeFunction()
    {

        if(bluetoothAdapter != null && bluetoothAdapter.isEnabled())
        {
            // we check if coarse location must be asked
            checkBTPermission();
            bluetoothAdapter.startDiscovery();





        }else {
            checkBluetoothState();
        }

        return(mBTDevices);
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver3);
    }
    // CheckCoarseLocationPermission function - we check permission a start of the App
    private boolean checkCoarseLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
         /*   ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_ACCESS_COARSE_LOCATION);*/
            return false;
        }else {
            return true;
        }
    }
    private void checkBluetoothState()
    {
        if(bluetoothAdapter == null)
        {
            Log.d(TAG,"EnableDisabledBT:Does not have BT capabilities");
        }
        if(!bluetoothAdapter.isEnabled())
        {
            Log.d(TAG,"enableDisableBtn: enabling BT");
            Intent enabledBTIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enabledBTIntent);
            IntentFilter BTIntent=new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1,BTIntent);
        }
        if(bluetoothAdapter.isEnabled())
        {
            Log.d(TAG,"enableDisableBtn: disabling BT");
            bluetoothAdapter.disable();
            IntentFilter BTIntent=new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1,BTIntent);
        }
    }
    private final BroadcastReceiver mBroadcastReceiver1=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(action.equals(bluetoothAdapter.ACTION_STATE_CHANGED))
            {
                final int state=intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,bluetoothAdapter.ERROR);
                switch ( state)
                {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG,"onReceive :STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG,"onReceive :STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG,"onReceive :STATE TURNING ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG,"onReceive :STATE TURNING OFF");
                        break;
                }
            }

        }
    };


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermission()
    {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            }
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }// end of Permissison


    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                Log.d(TAG,"onReceive_size of the array : "+mBTDevices.size());
                Toast.makeText(getApplicationContext(),device.getName(),Toast.LENGTH_LONG).show();
               /*  DeviceListAdapter mDeviceListAdapter;
                mDeviceListAdapter = new DeviceListAdapter (context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter((ListAdapter) mDeviceListAdapter);*/
            }else {
                Log.d(TAG, "onElse: NO DEVICE FOUND.");
            }
        }
    };


    ArrayList<String> getBondeddevice()
    {

        pairedDevices = bluetoothAdapter.getBondedDevices();
        //return(pairedDevices);
        if (pairedDevices.size() > 0)
        {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices)
            {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
              //  ArrayList bondedlist = new ArrayList();
                bondedlist = new ArrayList();
                bondedlist.add(device.getName());
                Toast.makeText(getApplicationContext(), deviceName, Toast.LENGTH_LONG).show();

            }

        }else {
            Toast.makeText(getApplicationContext(), "NO bonded device", Toast.LENGTH_LONG).show();
        }

        return(bondedlist);


    };




} // end of class File
