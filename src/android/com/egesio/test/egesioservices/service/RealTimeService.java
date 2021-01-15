package com.egesio.test.egesioservices.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.egesio.test.egesioservices.app.App;
import com.egesio.test.egesioservices.command.CommandManager;
import com.egesio.test.egesioservices.constants.Constans;
import com.egesio.test.egesioservices.utils.CallHelper;
import com.egesio.test.egesioservices.utils.DataHandlerUtils;
import com.egesio.test.egesioservices.utils.SendDataFirebase;
import com.egesio.test.egesioservices.utils.Sharedpreferences;
import com.egesio.test.egesioservices.utils.Utils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class RealTimeService extends Service {


    private CallHelper callHelper;

    private static final String TAG = RealTimeService.class.getSimpleName();
    public static final String ACTION_REALTIME_BROADCAST = RealTimeService.class.getName() + "ConnectBroadcastRealTime";
    public static final String ACTION_GATT_DISCONNECTED_ALL = RealTimeService.class.getName() + "DisconnectBroadcastRealTime";

    private CommandManager manager;
    private SharedPreferences sharedpreferences;

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private NotificationChannel notificationChannel;
    private String NOTIFICATION_CHANNEL_ID = "17";

    @Override
    public void onCreate()
    {
        super.onCreate();
        try{
            creaNotificacion();
            manager = CommandManager.getInstance(this);
        }catch (Exception e){
            Utils.isServiceStart = false;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try{
            callHelper = new CallHelper(this);
            callHelper.start();
            creaAlarmaRecurrenteBluetooth();
            LocalBroadcastManager.getInstance(this).registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            Sharedpreferences.getInstance(this).escribeValorString(Constans.LAST_TIME_GENERAL, String.valueOf(System.currentTimeMillis()));
            //sendMessageToUI(this);
            if(Utils.isServiceStart && Utils.isDeviceConnect){
                Intent intentDevice = new Intent(RealTimeService.ACTION_REALTIME_BROADCAST);
                LocalBroadcastManager.getInstance(this).sendBroadcastSync(intentDevice);
            }
        }catch (Exception e){
            try{
                if(App.mConnected)
                    App.mBluetoothLeService.disconnect();
                LocalBroadcastManager.getInstance(this).unregisterReceiver(mGattUpdateReceiver);
                Intent intentGeoRatioMonitoring = new Intent(getApplicationContext(), RealTimeService.class);
                getApplicationContext().startService(intentGeoRatioMonitoring);
            }catch (Exception e2){
            }
        }
        return super.onStartCommand(intent, flags, startId); //START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        callHelper.stop();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        try{
            if(App.mConnected)
                App.mBluetoothLeService.disconnect();
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mGattUpdateReceiver);
            Intent intentGeoRatioMonitoring = new Intent(getApplicationContext(), RealTimeService.class);
            getApplicationContext().startService(intentGeoRatioMonitoring);
        }catch (Exception e){
        }
    }

    /*private void sendMessageToUI(Context context) {
        try{
            Timer mTimer = new Timer(true);
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(App.mBluetoothLeService != null){
                        Intent intent = new Intent(RealTimeService.ACTION_REALTIME_BROADCAST);
                        LocalBroadcastManager.getInstance(context).sendBroadcastSync(intent);
                        mTimer.cancel();
                    }
                }
            }, 100, 100);
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
    }*/


    private IntentFilter makeGattUpdateIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(RealTimeService.ACTION_REALTIME_BROADCAST);
        intentFilter.addAction(RealTimeService.ACTION_GATT_DISCONNECTED_ALL);
        return intentFilter;
    }

    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (RealTimeService.ACTION_REALTIME_BROADCAST.equals(action)) {
                //if(!App.mConnected)
                App.mBluetoothLeService.connect(Sharedpreferences.getInstance(context).obtenValorString(Constans.MACADDRESS, "00:00:00:00:00:00"));
                //else
                //    App.mBluetoothLeService.disconnect();
                new SendDataFirebase(context).execute("{\"action\": \"ACTION_MEDICION_BROADCAST - " + Utils.getHora() + "\"}");
            } else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                new SendDataFirebase(context).execute("{\"action\": \"ACTION_GATT_CONNECTED - " +  Utils.getHora() + "\"}");
                App.mBluetoothLeService.initialize();
                //manager.heartRateSensor(1);
                //manager.temperatureSensor(1);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action) && !Utils.isDeviceDisconnectManual) {
                new SendDataFirebase(context).execute("{\"action\": \"DISCONECT - " + Utils.getHora() + "\"}");
                Sharedpreferences.getInstance(context).escribeValorString(Constans.LAST_TIME_GENERAL, String.valueOf(System.currentTimeMillis()));
                //LocalBroadcastManager.getInstance(context).unregisterReceiver(mGattUpdateReceiver);
                if(!App.mConnected){
                    //LocalBroadcastManager.getInstance(context).registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
                    App.mBluetoothLeService.initialize();
                    App.mBluetoothLeService.connect(Sharedpreferences.getInstance(context).obtenValorString(Constans.MACADDRESS, "00:00:00:00:00:00"));
                }
            }else if (RealTimeService.ACTION_GATT_DISCONNECTED_ALL.equals(action)) {
                new SendDataFirebase(context).execute("{\"action\": \"DISCONNECT MANUAL - " + Utils.getHora() + "\"}");
                //LocalBroadcastManager.getInstance(context).unregisterReceiver(mGattUpdateReceiver);
                App.mBluetoothLeService.disconnect();
                //App.mConnected = false;
                Utils.isDeviceDisconnect = true;
                Utils.isDeviceConnect = false;
                Utils.isDeviceDisconnectManual = true;

            }else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                final byte[] txValue = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                List<Integer> data = DataHandlerUtils.bytesToArrayList(txValue);
                //new SendDataFirebase(context).execute("{\"action\": \"DATA_AVAILABLE - " + data + " - " + Utils.getHora() + "\"}");
                //manager.temperatureSensor(1);
                if (data.get(4) == 0X91) {
                    manager.findBracelet();
                    Utils.isDeviceConnect = true;
                    Utils.isDeviceDisconnect = false;
                    Utils.isDeviceDisconnectManual = false;
                    manager.heartRateSensor(1);
                }

                if (data.get(0) == 0xAB && data.get(4) == 0x31) {
                    switch (data.get(5)) {
                        case 0X0A:
                            //Heart Rate（Real-time）
                            int heartRate = data.get(6);
                            //new SendDataFirebase(context).execute("{\"action\": \"HEART - " + data + " - " + Utils.getHora() + "\"}");
                            //manager.getOneClickMeasurementCommand(1);
                            manager.temperatureSensor(1);
                            manager.bloodOxygenSensor(1);
                            Utils.guardaDato(context, Constans.HEART_KEY, String.valueOf(heartRate));
                            Utils.validaLecturas(context, manager);
                            break;
                        case 0x12:
                            //Blood Oxygen（Real-time）
                            int bloodOxygen = data.get(6);
                            //new SendDataFirebase(context).execute("{\"action\": \"OXYGEN - " + data + " - " + Utils.getHora() + "\"}");
                            manager.temperatureSensor(1);
                            manager.bloodPressureSensor(1);
                            Utils.guardaDato(context, Constans.BLOOD_OXYGEN_KEY, String.valueOf(bloodOxygen));
                            Utils.validaLecturas(context, manager);
                            break;
                        case 0x22:
                            //Blood Pressure（Real-time）
                            int bloodPressureHypertension = data.get(6);
                            int bloodPressureHypotension = data.get(7);
                            //new SendDataFirebase(context).execute("{\"action\": \"PRESSURE - " + data + " - " + Utils.getHora() + "\"}");
                            manager.temperatureSensor(1);
                            manager.heartRateSensor(1);
                            Utils.guardaDato(context, Constans.BLOOD_PRESSURE_KEY, bloodPressureHypertension + "/" + bloodPressureHypotension);
                            Utils.validaLecturas(context, manager);
                            break;
                    }
                }

                if (data.get(0) == 0xAB && data.get(4) == 0x86) {
                    //new SendDataFirebase(context).execute("{\"action\": \"TEMP - " + Utils.getHora() + "\"}");
                    int entero = data.get(6);
                    int decimal = data.get(7);
                    Utils.guardaDato(context, Constans.TEMPERATURE_KEY, entero + "." + decimal);
                    Utils.validaLecturas(context, manager);

                }

                if (data != null && data.size() == 13 && data.get(4) == 0x32) {
                    //new SendDataFirebase(context).execute("{\"action\": \"REGRESO TODAAAAAS - " + Utils.getHora() + "\"}");

                    Integer heartRate = data.get(6);
                    Integer bloodOxygen = data.get(7);
                    Integer bloodPressureHypertension = data.get(8);
                    Integer bloodPressureHypotension = data.get(9);


                    Utils.guardaDato(context, Constans.HEART_KEY, String.valueOf(heartRate));
                    Utils.guardaDato(context, Constans.BLOOD_OXYGEN_KEY, String.valueOf(bloodOxygen));
                    Utils.guardaDato(context, Constans.BLOOD_PRESSURE_KEY, bloodPressureHypertension + "/" + bloodPressureHypotension);
                    Utils.guardaDato(context, Constans.TEMPERATURE_KEY, data.get(11) + "." + data.get(12));

                }
            }
        }
    };







    void creaAlarmaRecurrenteBluetooth(){
        final int requestCode = 1337;
        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000 , pendingIntent );

    }


    @SuppressLint("WrongConstant")
    public void creaNotificacion(){
        mNotifyManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this, null);
        mBuilder.setContentTitle("Egesio Servicios Activos")
                .setContentText("Servicios en completa ejecución")
                .setTicker("Servicios en completa ejecución")
                //.setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setAutoCancel(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Egesio Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Egesio Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            mNotifyManager.createNotificationChannel(notificationChannel);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            startForeground(17, mBuilder.build());
        }
        else
        {
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotifyManager.notify(17, mBuilder.build());
        }
    }






}
