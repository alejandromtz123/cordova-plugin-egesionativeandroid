package com.egesio.cordova.plugin;

import android.app.Activity;
import android.content.Intent;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.net.Uri;
import android.Manifest;
import android.telecom.Connection;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;

import com.egesio.test.egesioservices.utils.Sharedpreferences;
import com.egesio.test.egesioservices.constants.Constans;
import com.egesio.test.egesioservices.bean.BaseEvent;
import com.egesio.test.egesioservices.service.BluetoothLeService;
import com.egesio.test.egesioservices.service.RealTimeService;
import com.egesio.test.egesioservices.utils.Utils;

import de.greenrobot.event.EventBus;

public class Egesionativeandroid extends CordovaPlugin {

    private static CordovaInterface cordovaInterface;
    private Context context;
    private String TAG = "EGESIO";
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        context = this.cordova.getActivity().getApplicationContext();
        cordovaInterface = cordova;
        super.initialize(cordova, webView);
        Toast.makeText(context, "Inicio Plugin y valores de prueba", Toast.LENGTH_LONG).show();
        Utils.guardaValores(context);
    }
	
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {    
		if(action.equals("iniciaServicioackgroudAndroid")) {
                  Toast.makeText(context, "Inicio 1 " + action, Toast.LENGTH_LONG).show();
                  cordovaInterface.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                              Utils.iniciaServicio(context);

                              /*if (message != null && message.length() > 0) {
                                    callbackContext.success(message);
                              } else {
                                    callbackContext.error("Expected one non-empty string argument.");
                              }*/
                        }
                    });
                  //Log.d(TAG, "--" + args);
		}else if(action.equals("detenServicioackgroudAndroid")) {
                  Toast.makeText(context, "Inicio 2 " + action, Toast.LENGTH_LONG).show();
                  cordovaInterface.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                              Utils.detenServicio(context);
                        }
                    });
		}else if(action.equals("conectaPulseraAndroid")) {
                  Toast.makeText(context, "Inicio 3 " + action, Toast.LENGTH_LONG).show();
                  cordovaInterface.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                              Utils.iniciaProcesoLectura(context);
                        }
                    });
		}else if(action.equals("desconectaPulseraAndroid")) {
                  Toast.makeText(context, "Inicio 4 " + action, Toast.LENGTH_LONG).show();
                  cordovaInterface.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                              Utils.detenProcesoLectura(context);
                        }
                    });
		}else if(action.equals("obtenValoresRegistrados")) {
                  Toast.makeText(context, "Inicio 5 " + action, Toast.LENGTH_LONG).show();
                  cordovaInterface.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                              Utils.obtenTodosValoresJSON(context);
                        }
                    });
            }
            else if(action.equals("enviaValoresRegistradosEgesioDB")) {
                  Toast.makeText(context, "Inicio 6 " + action, Toast.LENGTH_LONG).show();
                  cordovaInterface.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                              Utils.enviaDatosEgesioDB(context);
                        }
                    });
		}
            else if(action.equals("obtenLecturaHeartJSON")) {
                  Toast.makeText(context, "Inicio 7 " + action, Toast.LENGTH_LONG).show();
                  cordovaInterface.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                              Utils.obtenLecturaHeartJSON(context);
                        }
                    });
		}
            else if(action.equals("obtenLecturaOxygeJSON")) {
                  Toast.makeText(context, "Inicio 8 " + action, Toast.LENGTH_LONG).show();
                  cordovaInterface.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                              Utils.obtenLecturaOxygeJSON(context);
                        }
                    });
		}
            else if(action.equals("obtenLecturaPressureJSON")) {
                  Toast.makeText(context, "Inicio 9 " + action, Toast.LENGTH_LONG).show();
                  cordovaInterface.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                              Utils.obtenLecturaPressureJSON(context);
                        }
                    });
		}
            else if(action.equals("obtenLecturaTemperatureJSON")) {
                  Toast.makeText(context, "Inicio 10 " + action, Toast.LENGTH_LONG).show();
                  cordovaInterface.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                              Utils.obtenLecturaTemperatureJSON(context);
                        }
                    });
		}
		return false;
      }
      


      private static int periodo = 100;
      private static int sumPeriodoStartService = 0;
      private static int sumPeriodoStopService = 0;
      private static int sumDeviceConnect = 0;
      private static int sumDeviceDisconnect = 0;
  
      public void validaDeviceDisconnect(Context context, int awaitTime) {
          try{
              Timer mTimer = new Timer(true);
              mTimer.schedule(new TimerTask() {
                  @Override
                  public void run() {
                      sumDeviceDisconnect += periodo;
                      if ( Utils.isDeviceDisconnect ) {
                          Log.d("Egesio", "Pulsera SI Desconectada y NO mitiendo");
                          //textViewValores.setText("Servicio SI Iniciado");
                          sumDeviceDisconnect = 0;
                          mTimer.cancel();
                      }else if(sumDeviceDisconnect > awaitTime){
                          sumDeviceDisconnect = 0;
                          mTimer.cancel();
                      }
                      Log.d("Egesio", "Pulsera NO Desconectada y SI emitiendo - " + sumDeviceDisconnect + " - " + Utils.isServiceStop + " - " + awaitTime);
                  }
              }, 100, periodo);
          }catch (Exception e){
              Log.d("Egesio", e.getMessage());
          }
      }
  
      public void validaDeviceConnect(Context context, int awaitTime) {
          try{
              Timer mTimer = new Timer(true);
              mTimer.schedule(new TimerTask() {
                  @Override
                  public void run() {
  
                      sumDeviceConnect += periodo;
                      if ( Utils.isDeviceConnect ) {
                          Log.d("Egesio", "Pulsera SI Conectada y SI mitiendo");
                          //textViewValores.setText("Servicio SI Iniciado");
                          sumDeviceConnect = 0;
                          mTimer.cancel();
                      }else if(sumDeviceConnect > awaitTime){
                          sumDeviceConnect = 0;
                          mTimer.cancel();
                      }
                      Log.d("Egesio", "Pulsera NO Conectada y NO emitiendo - " + sumDeviceConnect + " - " + Utils.isServiceStop + " - " + awaitTime);
                  }
              }, 100, periodo);
          }catch (Exception e){
              Log.d("Egesio", e.getMessage());
          }
      }
  
      public void validaStopServicio(Context context, int awaitTime) {
          try{
              Timer mTimer = new Timer(true);
              mTimer.schedule(new TimerTask() {
                  @Override
                  public void run() {
  
                      sumPeriodoStopService += periodo;
                      if ( Utils.isServiceStop ) {
                          Log.d("Egesio", "Servicio SI Detenido");
                          //textViewValores.setText("Servicio SI Iniciado");
                          sumPeriodoStopService = 0;
                          mTimer.cancel();
                      }else if(sumPeriodoStopService > awaitTime){
                          sumPeriodoStopService = 0;
                          mTimer.cancel();
                      }
                      Log.d("Egesio", "Servicio NO Deteido - " + sumPeriodoStopService + " - " + Utils.isServiceStop + " - " + awaitTime);
                  }
              }, 100, periodo);
          }catch (Exception e){
              Log.d("Egesio", e.getMessage());
          }
      }
  
      public void validaIniciaServicio(Context context, int awaitTime) {
          try{
              Timer mTimer = new Timer(true);
              mTimer.schedule(new TimerTask() {
                  @Override
                  public void run() {
  
                      sumPeriodoStartService += periodo;
                      if ( Utils.isServiceStart ) {
                          Log.d("Egesio", "Servicio SI Iniciado");
                          //textViewValores.setText("Servicio SI Iniciado");
                          sumPeriodoStartService = 0;
                          mTimer.cancel();
                      }else if(sumPeriodoStartService > awaitTime){
                          sumPeriodoStartService = 0;
                          mTimer.cancel();
                      }
                      Log.d("Egesio", "Servicio NO Iniciado - " + sumPeriodoStartService + " - " + Utils.isServiceStart + " - " + awaitTime);
                  }
              }, 100, periodo);
          }catch (Exception e){
              Log.d("Egesio", e.getMessage());
          }
      }


}