package com.example.deepa.ufsocial;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;

import static android.support.test.InstrumentationRegistry.getContext;

public class MyService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private Socket socket;
    private static final int SERVERPORT = 8000;
    private static final String SERVER_IP = "70.171.55.225";

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

 /*   @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }*/

    class connectSocket extends Thread {
        GPSManager gpsManager = new GPSManager(getContext());
        JSONObject obj;
        JSONObject obj1;
        connectSocket() {
            obj = new JSONObject();
            try {
                obj.put("header", "none");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        connectSocket(JSONObject object) {
            obj = object;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run() {
            try{
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVERPORT);
                switch (obj.getString("header")) {
                    case "testAuth": {
                        PrintWriter os = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        os.println(obj.toString());
                        try {
                            String response = in.readLine();
                            JSONObject jObject = new JSONObject(response);
                            sendMessage("signin", jObject.getString("response"));
                            os.close();
                            in.close();
                            break;
                        } catch (IOException e) {
                            break;
                        }
                    }
                    case "forgotPassword": {
                        PrintWriter os = new PrintWriter(socket.getOutputStream(), true);
                        os.println(obj.toString());
                        os.close();
                        break;
                    }
                    case "resetPassword": {
                        PrintWriter os = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        os.println(obj.toString());
                        try {
                            String response = in.readLine();
                            JSONObject jObject = new JSONObject(response);
                            sendMessage("resetpass", jObject.getString("response"));
                            os.close();
                            in.close();
                            break;
                        } catch (IOException e) {
                            break;
                        }
                    }
                    case "createAccount": {
                        PrintWriter os = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        os.println(obj.toString());
                        try {
                            String response = in.readLine();
                            JSONObject jObject = new JSONObject(response);
                            sendMessage("signup", jObject.getString("response"));
                            os.close();
                            in.close();
                            break;
                        } catch (IOException e) {
                            break;
                        }
                    }
                    case "enterCode": {
                        PrintWriter os = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        os.println(obj.toString());
                        try {
                            String response = in.readLine();
                            JSONObject jObject = new JSONObject(response);
                            sendMessage("verify", jObject.getString("response"));
                            os.close();
                            in.close();
                            break;
                        } catch (IOException e) {
                            break;
                        }
                    }
                    default: {
                        PrintWriter os = new PrintWriter(socket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        obj1.put("header", "GPSData");
                        obj1.put("latitude", Double.toString(gpsManager.getLatitude()));
                        obj1.put("longitude", Double.toString(gpsManager.getLongitude()));
                        os.println(obj1.toString());
                        try {
                            String response = in.readLine();
                            JSONObject jObject = new JSONObject(response);
                            sendMessage("home", jObject.getString("response"));
                            os.close();
                            in.close();
                            break;
                        } catch (IOException e) {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(String inte, String data) {
        Intent intent = new Intent(inte);
        intent.putExtra("message", data);
        Log.d("msdf", data);
        LocalBroadcastManager.getInstance(this).sendBroadcastSync(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        socket = null;
    }

    public class LocalBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void trivialActions(JSONObject obj) throws InterruptedException {
        connectSocket connect1 = new connectSocket(obj);
        connect1.start();
        try {
            connect1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

/*    public void homeActions() {
        connectSocket connect = new connectSocket();
        connect.start();
        try {
            connect.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/
}
