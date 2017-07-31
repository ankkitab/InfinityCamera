package com.example.ankkitabose.dropboxintegrate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by POWERHOUSE on 7/30/2017.
 */

public class CameraReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Receiver", "Camera Clicked!!");
        Toast.makeText(context, "Clicked!!", Toast.LENGTH_SHORT).show();
    }

}
