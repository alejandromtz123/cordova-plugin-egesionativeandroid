package com.egesio.test.egesioservices.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.egesio.test.egesioservices.command.CommandManager;
import com.egesio.test.egesioservices.constants.Constans;
import com.egesio.test.egesioservices.service.RealTimeService;

import static android.content.Context.ACTIVITY_SERVICE;
import static java.lang.Double.isNaN;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class Utils {

    public static boolean isServiceStart = false;
    public static boolean isServiceStop = false;
    public static boolean isDeviceConnect = false;
    public static boolean isDeviceDisconnect = false;
    public static boolean isDeviceDisconnectManual = false;

    public static boolean isOpenLocationService(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context _ctx) {
        final ActivityManager activityManager = (ActivityManager)_ctx.getSystemService(ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClass.getName())){
                return true;
            }
        }
        return false;
    }

    public static String getHora(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String fecha = dtf.format(now);
        return fecha;
    }

    public static void guardaDato(Context context, String key, String valor){
        try{
            //new SendDataFirebase(context).execute("{\"action\": \"" + key + " - " + valor + " - " + Utils.getHora() + "\"}");
            if(key.equals(Constans.TEMPERATURE_KEY) && valor != null){
                Double temperature = Double.valueOf(valor);
                if (temperature != null && !isNaN(temperature) && temperature > 35 && temperature < 43)
                    Sharedpreferences.getInstance(context).escribeValorString(Constans.TEMPERATURE_KEY, String.valueOf(temperature));
            }else if(key.equals(Constans.HEART_KEY) && valor != null){
                Integer heartRate = Integer.valueOf(valor);
                if (heartRate != null && !isNaN(heartRate) && heartRate > 40 && heartRate < 226)
                    Sharedpreferences.getInstance(context).escribeValorString(Constans.HEART_KEY, String.valueOf(heartRate));
            }else if(key.equals(Constans.BLOOD_OXYGEN_KEY) && valor != null){
                Integer bloodOxygen = Integer.valueOf(valor);
                if (bloodOxygen != null && !isNaN(bloodOxygen) && bloodOxygen > 70 && bloodOxygen <= 100)
                    Sharedpreferences.getInstance(context).escribeValorString(Constans.BLOOD_OXYGEN_KEY, String.valueOf(bloodOxygen));
            }else if(key.equals(Constans.BLOOD_PRESSURE_KEY) && valor != null){
                Integer bloodPressureHypertension = Integer.valueOf(valor.split("/")[0]);
                Integer bloodPressureHypotension = Integer.valueOf(valor.split("/")[1]);
                if (bloodPressureHypertension != null && !isNaN(bloodPressureHypertension) &&
                        bloodPressureHypertension > 70 && bloodPressureHypertension < 200 &&
                        !isNaN(bloodPressureHypotension) && bloodPressureHypotension > 40 && bloodPressureHypotension < 130)
                    Sharedpreferences.getInstance(context).escribeValorString(Constans.BLOOD_PRESSURE_KEY, bloodPressureHypertension + "/" + bloodPressureHypotension);
            }
        }catch (Exception e){

        }
    }

    public static boolean isLecturasCompletas(Context context){
        boolean r = false;
        try{
            int theCountGeneral = Integer.valueOf(Sharedpreferences.getInstance(context).obtenValorString(Constans.COUNT_GENERAL, "0"));
            if(theCountGeneral < Constans.LECTURAS){
                Sharedpreferences.getInstance(context).escribeValorString(Constans.COUNT_GENERAL, String.valueOf(theCountGeneral + 1));
            }else{
                r = true;
                Sharedpreferences.getInstance(context).escribeValorString(Constans.COUNT_GENERAL, "0");
            }
        }catch (Exception e){
            new SendDataFirebase(context).execute("{\"action\": \"ERROR  - " + e.getMessage() + " - " + Utils.getHora() + "\"}");
        }
        return r;
    }

    public static boolean isPeriodoCompleto(Context context){
        boolean r = false;
        Long theLastGeneral = Long.valueOf(Sharedpreferences.getInstance(context).obtenValorString(Constans.LAST_TIME_GENERAL, "0"));
        Long generalNow = System.currentTimeMillis();

        Long millseg = generalNow - theLastGeneral;
        //new SendDataFirebase(context).execute("{\"action\": \"TIME: -" + millseg  + "-" +  Utils.getHora() + "\"}");
        Long periodo = Long.valueOf(Sharedpreferences.getInstance(context).obtenValorString(Constans.PERIODO, "0")) * 1000;
        if(millseg > periodo)
            r = true;
        return r;
    }

    public static void enviaDatosEgesioDB(Context context){
        String _heart = Sharedpreferences.getInstance(context).obtenValorString(Constans.HEART_KEY, "0");
        String _oxygen = Sharedpreferences.getInstance(context).obtenValorString(Constans.BLOOD_OXYGEN_KEY, "0");
        String _pressure = Sharedpreferences.getInstance(context).obtenValorString(Constans.BLOOD_PRESSURE_KEY, "0/0");
        String _temperature = Sharedpreferences.getInstance(context).obtenValorString(Constans.TEMPERATURE_KEY, "0");
        //new SendDataFirebase(context).execute("{\"action\": \"HEART  - " + _heart + " - " + Utils.getHora() + "\"}");
        //new SendDataFirebase(context).execute("{\"action\": \"BLOOD_OXYGEN - " + _oxygen + " - " + Utils.getHora() + "\"}");
        //new SendDataFirebase(context).execute("{\"action\": \"BLOOD_PRESSURE - " + _pressure + " - " + Utils.getHora() + "\"}");
        //new SendDataFirebase(context).execute("{\"action\": \"TEMPERATURE - " + _temperature + " - " + Utils.getHora() + "\"}");
        new SendDataFirebase(context).execute("{\"action\": \"INVOCANDO DATABASE: "  +  Utils.getHora() + "\"}");

        SendDataEgesio sendDataEgesio = new SendDataEgesio(context);
        sendDataEgesio.enviaDatosEgesioDB();
        Sharedpreferences.getInstance(context).escribeValorString(Constans.LAST_TIME_GENERAL, String.valueOf(System.currentTimeMillis()));
    }

    public static void validaLecturas(Context context, CommandManager manager){
        if(Utils.isLecturasCompletas(context)) {
            //validaDatos(data, "TEMPERATURE", context);
            new SendDataFirebase(context).execute("{\"action\": \"APAGO TODAS - " + Utils.getHora() + "\"}");
            manager.getOneClickMeasurementCommand(0);

        }else{
            if(Integer.valueOf(Sharedpreferences.getInstance(context).obtenValorString(Constans.COUNT_GENERAL, "0")) == 3){
                new SendDataFirebase(context).execute("{\"action\": \"PRENDO TODAS - " + Utils.getHora() + "\"}");
                manager.getOneClickMeasurementCommand(1);
            }
        }

        if(Utils.isPeriodoCompleto(context)){
            Utils.enviaDatosEgesioDB(context);
        }
    }

    public static String obtenLecturaHeartJSON(Context context){
        String r = "";
        try{
            String _heart = Sharedpreferences.getInstance(context).obtenValorString(Constans.HEART_KEY, "0");
            r += "{";
            r += "\"" + Constans.HEART_KEY + "\":" + "\"" + _heart + "\"";
            r += "}";
        }catch (Exception e){
            Log.d("Egesio", e.getMessage());
        }
        return r;
    }

    public static String obtenLecturaOxygeJSON(Context context){
        String r = "";
        try{
            String _oxygen = Sharedpreferences.getInstance(context).obtenValorString(Constans.BLOOD_OXYGEN_KEY, "255");
            r += "{";
            r += "\"" + Constans.BLOOD_OXYGEN_KEY + "\":" + "\"" + _oxygen + "\"";
            r += "}";
        }catch (Exception e){
            Log.d("Egesio", e.getMessage());
        }
        return r;
    }

    public static String obtenLecturaPressureJSON(Context context){
        String r = "";
        try{
            String _pressure = Sharedpreferences.getInstance(context).obtenValorString(Constans.BLOOD_PRESSURE_KEY, "255/255");
            r += "{";
            r += "\"" + Constans.BLOOD_PRESSURE_KEY + "\":" + "\"" + _pressure + "\"";
            r += "}";
        }catch (Exception e){
            Log.d("Egesio", e.getMessage());
        }
        return r;
    }

    public static String obtenLecturaTemperatureJSON(Context context){
        String r = "";
        try{
            String _temperature = Sharedpreferences.getInstance(context).obtenValorString(Constans.TEMPERATURE_KEY, "255");
            r += "{";
            r += "\"" + Constans.TEMPERATURE_KEY + "\":" + "\"" + _temperature + "\"";
            r += "}";
        }catch (Exception e){
            Log.d("Egesio", e.getMessage());
        }
        return r;
    }

    public static String obtenTodosValoresJSON(Context context){
        String r = "";
        try{
            String _heart = Sharedpreferences.getInstance(context).obtenValorString(Constans.HEART_KEY, "255");
            String _oxygen = Sharedpreferences.getInstance(context).obtenValorString(Constans.BLOOD_OXYGEN_KEY, "255");
            String _pressure = Sharedpreferences.getInstance(context).obtenValorString(Constans.BLOOD_PRESSURE_KEY, "255/255");
            String _temperature = Sharedpreferences.getInstance(context).obtenValorString(Constans.TEMPERATURE_KEY, "255");
            r += "{";
            r += "\"" + Constans.HEART_KEY + "\":" + "\"" + _heart + "\",";
            r += "\"" + Constans.BLOOD_OXYGEN_KEY + "\":" + "\"" + _oxygen + "\",";
            r += "\"" + Constans.BLOOD_PRESSURE_KEY + "\":" + "\"" + _pressure + "\",";
            r += "\"" + Constans.TEMPERATURE_KEY + "\":" + "\"" + _temperature + "\"";
            r += "}";
        }catch (Exception e){
            Log.d("Egesio", e.getMessage());
        }
        return r;
    }

    public static void detenProcesoLectura(Context context){
        try{
            //validaDeviceDisconnect(context, 60000);
            if(Utils.isDeviceConnect){
                Intent intent = new Intent(RealTimeService.ACTION_GATT_DISCONNECTED_ALL);
                LocalBroadcastManager.getInstance(context).sendBroadcastSync(intent);
            }else{
                Log.d("Egesio", "Pulsera NO conectada");
            }

        }catch (Exception e){
            Log.d("Egesio", e.getMessage());
        }
    }

    public static void iniciaProcesoLectura(Context context){
        try{
            //validaDeviceConnect(context, 60000);
            if(Utils.isServiceStart){
                Intent intent = new Intent(RealTimeService.ACTION_REALTIME_BROADCAST);
                LocalBroadcastManager.getInstance(context).sendBroadcastSync(intent);
            }else{
                Log.d("Egesio", "Servicio NO iniciado");
            }

        }catch (Exception e){
            Log.d("Egesio", e.getMessage());
        }
    }

    public static void detenServicio(Context context){
        try{
            //validaStopServicio(context, 10000);
            if (Utils.isMyServiceRunning(RealTimeService.class, context)) {
                Intent intentRealTimeMonitoring = new Intent(context, RealTimeService.class);
                context.stopService(intentRealTimeMonitoring);
                Utils.isServiceStop = true;
            }
        }catch (Exception e){
            Log.d("Egesio", e.getMessage());
        }
    }

    public static void iniciaServicio(Context context){
        try{
            //validaIniciaServicio(context, 10000);
            if (!Utils.isMyServiceRunning(RealTimeService.class, context)) {
                Intent intentRealTimeMonitoring = new Intent(context, RealTimeService.class);
                context.startService(intentRealTimeMonitoring);
                Utils.isServiceStart = true;
            }
        }catch (Exception e){
            Log.d("Egesio", e.getMessage());
        }
    }


    public static void guardaValores(Context context){
        Sharedpreferences.getInstance(context).escribeValorString(Constans.MACADDRESS, "EE:BF:60:20:A9:D9");
        Sharedpreferences.getInstance(context).escribeValorString(Constans.IDPULSERA, "117");
        Sharedpreferences.getInstance(context).escribeValorString(Constans.PERIODO, "300");
        Sharedpreferences.getInstance(context).escribeValorString(Constans.USER_KEY, "adminDev");
        Sharedpreferences.getInstance(context).escribeValorString(Constans.PASSWORD_KEY, "admin123456");

        Sharedpreferences.getInstance(context).escribeValorString(Constans.TEMPERATURE_KEY, "255");
        Sharedpreferences.getInstance(context).escribeValorString(Constans.HEART_KEY, "255");
        Sharedpreferences.getInstance(context).escribeValorString(Constans.BLOOD_OXYGEN_KEY, "255");
        Sharedpreferences.getInstance(context).escribeValorString(Constans.BLOOD_PRESSURE_KEY, "255/255");

        Sharedpreferences.getInstance(context).escribeValorString(Constans.COUNT_GENERAL, "0");
        Sharedpreferences.getInstance(context).escribeValorString(Constans.LAST_TIME_GENERAL, "0");

        Sharedpreferences.getInstance(context).escribeValorString(Constans.LAST_TIME_GENERAL, "0");
        Sharedpreferences.getInstance(context).escribeValorString(Constans.LAST_TIME_GENERAL, "0");

        Sharedpreferences.getInstance(context).escribeValorString(Constans.IDIOMA_SEND, "es");
        Sharedpreferences.getInstance(context).escribeValorString(Constans.TOKEN_SEND, Constans.TOKEN_KEY);
        Sharedpreferences.getInstance(context).escribeValorString(Constans.REFRESH_TOKEN_SEND, "0");

        Sharedpreferences.getInstance(context).escribeValorString(Constans.URL_SERVICE_EGESIO, Constans.URL_DEV_SERV);


        //textViewValores.setText("Sin valores disponibles");
    }
}