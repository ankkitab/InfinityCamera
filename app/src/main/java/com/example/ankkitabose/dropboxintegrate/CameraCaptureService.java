package com.example.ankkitabose.dropboxintegrate;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class CameraCaptureService extends Service {


    BroadcastReceiver receiver;

    @Override
    public void onCreate() {
        //String data = intent.getDataString();
        super.onCreate();
        Log.d("CameraService", "Inside Service onCreate!");


        //Log.d(.toString()," ");



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int i = super.onStartCommand(intent, flags, startId);
        Log.d("CameraService", "Inside Service onStartCommand!");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.hardware.action.NEW_PICTURE");
        try {
            filter.addDataType("image/*");
        }
        catch(IntentFilter.MalformedMimeTypeException e) {
            Log.d("Camera Service", "Ye to wahi baat ho gyi!!");
        }
        receiver = new CameraReceiver();

        try {
            this.registerReceiver(receiver, filter);
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d(e.getMessage(),"exception message!!!!");
        }
        return i;

        //return 0;
    }

    @Override
    public void onDestroy() {

        Log.d("CameraService", "Inside onDestroy!");
        unregisterReceiver(receiver);
        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
