package com.example.ankkitabose.dropboxintegrate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.users.FullAccount;
import com.squareup.picasso.Picasso;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST_CODE = 101;
    private String ACCESS_TOKEN;
    private boolean flag=false;
    private StatusDatabase db;
    private String value = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d("Main","OnCreate() call");
        Switch aswitch = (Switch) findViewById(R.id.switch2);
        final ImageView profile = (ImageView) findViewById(R.id.imageView);
        final TextView name = (TextView) findViewById(R.id.name_textView);
        final TextView email = (TextView) findViewById(R.id.email_textView);
        final Button SignInButton = (Button) findViewById(R.id.sign_in_button);

        db = new StatusDatabase(this);

        Cursor cursor = db.getItem("check");
        if(cursor.getCount()>0) {

            cursor.moveToFirst();

            value = cursor.getString(cursor.getColumnIndex("Item_Status"));
            Log.d("Main","Found check !"+value);
        }
        if(value.equalsIgnoreCase("true"))
            aswitch.setChecked(true);
        else
            aswitch.setChecked(false);



        aswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                db.updateItem("check",Boolean.toString(isChecked));

                if (isChecked) {
                    flag=true;
                    SignInButton.setEnabled(true);
                    SignInButton.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            if (!tokenExists()) {
                                System.out.println("This is Auth");
                                Auth.startOAuth2Authentication(getApplicationContext(), getString(R.string.APP_KEY));

                            }
                            else
                                Toast.makeText(getApplicationContext(), "Already Logged in!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    if (!tokenExists())
                        getAccessToken();
                    System.out.println("Dammnnnn");
                    ACCESS_TOKEN = retrieveAccessToken();
                    getUserAccount();
                    profile.setEnabled(true);
                    name.setEnabled(true);
                    email.setEnabled(true);

                    System.out.println("If entry point");

                    Intent serviceIntent = new Intent(getApplicationContext(), CameraCaptureService.class);
                    Log.d("MainAct", "isChecked true!");
                    //intent.setAction("com.example.powerhouse.directupload.action.startforeground");
                    startService(serviceIntent);

                }
                else {
                    flag=false;
                    //toggle disabled
                    profile.setEnabled(false);
                    name.setEnabled(false);
                    email.setEnabled(false);
                    SignInButton.setEnabled(false);
                    System.out.println("If entry point");

                    Intent serviceIntent = new Intent(getApplicationContext(), CameraCaptureService.class);
                    Log.d("MainAct", "isChecked false!");
                    //intent.setAction("com.example.powerhouse.directupload.action.stopforeground");
                    stopService(serviceIntent);
             //       fab.setEnabled(false);
                }
            }
        });


    }

    @Override
    protected void onResume() {

        System.out.println("This is resume!");
        super.onResume();
        getAccessToken();
        ACCESS_TOKEN = retrieveAccessToken();
        getUserAccount();
        final Button SignInButton = (Button) findViewById(R.id.sign_in_button);
        SignInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (tokenExists())
                    Toast.makeText(getApplicationContext(), "Already Logged in!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void getAccessToken() {
        String accessToken = Auth.getOAuth2Token(); //generate Access Token
        if (accessToken != null) {
            //Store accessToken in SharedPreferences
            SharedPreferences prefs = getSharedPreferences("com.example.ankkitabose.dropboxintegrate", Context.MODE_PRIVATE);
            prefs.edit().putString("access-token", accessToken).apply();

            //Proceed to MainActivity
            //   Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            //  startActivity(intent);
        }
    }

    protected void getUserAccount() {
        if (ACCESS_TOKEN == null)return;
        new UserAccountTask(DropboxClient.getClient(ACCESS_TOKEN), new UserAccountTask.TaskDelegate() {
            @Override
            public void onAccountReceived(FullAccount account) {
                //Print account's info
                Log.d("User", account.getEmail());
                Log.d("User", account.getName().getDisplayName());
                Log.d("User", account.getAccountType().name());
                updateUI(account);
            }
            @Override
            public void onError(Exception error) {
                Log.d("User", "Error receiving account details.");
            }
        }).execute();
    }

    private void updateUI(FullAccount account) {
        final ImageView profile = (ImageView) findViewById(R.id.imageView);
        final TextView name = (TextView) findViewById(R.id.name_textView);
        final TextView email = (TextView) findViewById(R.id.email_textView);
        Switch aswitch = (Switch) findViewById(R.id.switch2);
        name.setText(account.getName().getDisplayName());
        email.setText(account.getEmail());
        Picasso.with(this)
                .load(account.getProfilePhotoUrl())
                .resize(200, 200)
                .into(profile);
    }

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

    private boolean tokenExists() {
        SharedPreferences prefs = getSharedPreferences("com.example.ankkitabose.dropboxintegrate", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access-token", null);
        return accessToken != null;
    }

    private String retrieveAccessToken() {
        //check if ACCESS_TOKEN is stored on previous app launches
        SharedPreferences prefs = getSharedPreferences("com.example.ankkitabose.dropboxintegrate", Context.MODE_PRIVATE);
        String accessToken = prefs.getString("access-token", null);
        if (accessToken == null) {
            Log.d("AccessToken Status", "No token found");
            return null;
        } else {
            //accessToken already exists
            Log.d("AccessToken Status", "Token exists");
            return accessToken;
        }
    }
}
