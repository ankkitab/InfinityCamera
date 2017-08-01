package com.example.ankkitabose.dropboxintegrate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.core.v2.users.FullAccount;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * Created by POWERHOUSE on 7/30/2017.
 */

public class CameraReceiver extends BroadcastReceiver{

    StatusDatabase db;
    private String ACCESS_TOKEN;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Receiver", "Camera Clicked!!");

        db = new StatusDatabase(context);
        Cursor cursor = db.getItem("token");
        cursor.moveToFirst();
        ACCESS_TOKEN = cursor.getString(cursor.getColumnIndex("Item_Status"));
        //Toast.makeText(context, "Clicked!! "+ACCESS_TOKEN, Toast.LENGTH_SHORT).show();
        cursor = context.getContentResolver().query(intent.getData(), null, null, null, null);
        cursor.moveToFirst();
        String image_path = cursor.getString(cursor.getColumnIndex("_data"));
        Toast.makeText(context, "Clicked!! "+image_path, Toast.LENGTH_SHORT).show();

        File file = new File(image_path);
        if (file != null) {
            //Initialize UploadTask
            new UploadTask(DropboxClient.getClient(ACCESS_TOKEN), file, context).execute();
        }




    }
/*

    private void upload() {
        System.out.println("This is upload");
        if (ACCESS_TOKEN == null)return;
        //Select image to upload
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent,
                "Upload to Dropbox"), IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("This is Activity Result");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;
        // Check which request we're responding to
        if (requestCode == IMAGE_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                //Image URI received
                File file = new File(URI_to_Path.getPath(getApplication(), data.getData()));
                if (file != null) {
                    //Initialize UploadTask
                    new UploadTask(DropboxClient.getClient(ACCESS_TOKEN), file, getApplicationContext()).execute();
                }
            }
        }
    }
*/
}
