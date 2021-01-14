package com.egesio.cordova.plugin;

import android.app.Activity;
import android.content.Intent;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import android.content.Intent;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import android.widget.Toast;
import de.greenrobot.event.EventBus;

public class Egesionativeandroid extends CordovaPlugin {

    private static CordovaInterface cordovaInterface;
    private Context context;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        context = this.cordova.getActivity().getApplicationContext();
        cordovaInterface = cordova;
        super.initialize(cordova, webView);
        Toast.makeText(context, "Inicio Plugin", Toast.LENGTH_LONG).show();
    }
	
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

		if(action.equals("test")) {

            Toast.makeText(context, "Inicio test", Toast.LENGTH_LONG).show();

		}


		return false;
	}

}