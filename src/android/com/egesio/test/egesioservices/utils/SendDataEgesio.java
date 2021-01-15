package com.egesio.test.egesioservices.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.egesio.test.egesioservices.constants.Constans;
import com.egesio.test.egesioservices.model.Medida;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SendDataEgesio extends AsyncTask<String, Void, String> {

    private Exception exception;
    private Context context;

    public SendDataEgesio(Context context){
        this.context = context;
    }

    public void enviaDatosEgesioDB(){

        Medida temperatureParam = new Medida();
        Medida     heartRateParam = new Medida();
        Medida   oxygenationParam = new Medida();
        Medida         bloodParam = new Medida();

        int idPulsera = Integer.valueOf(Sharedpreferences.getInstance(context).obtenValorString(Constans.IDPULSERA, "0"));

        temperatureParam.setDispositivo_id(idPulsera);
        temperatureParam.setValor(Sharedpreferences.getInstance(context).obtenValorString(Constans.TEMPERATURE_KEY, "0"));
        temperatureParam.setDispositivo_parametro_id(1);
        temperatureParam.setFecha(Utils.getHora());
        temperatureParam.setIdioma(Sharedpreferences.getInstance(context).obtenValorString(Constans.IDIOMA_SEND, "es"));

        heartRateParam.setDispositivo_id(idPulsera);
        heartRateParam.setValor(Sharedpreferences.getInstance(context).obtenValorString(Constans.HEART_KEY, "0"));
        heartRateParam.setDispositivo_parametro_id(2);
        heartRateParam.setFecha(Utils.getHora());
        heartRateParam.setIdioma(Sharedpreferences.getInstance(context).obtenValorString(Constans.IDIOMA_SEND, "es"));

        oxygenationParam.setDispositivo_id(idPulsera);
        oxygenationParam.setValor(Sharedpreferences.getInstance(context).obtenValorString(Constans.BLOOD_OXYGEN_KEY, "0"));
        oxygenationParam.setDispositivo_parametro_id(3);
        oxygenationParam.setFecha(Utils.getHora());
        oxygenationParam.setIdioma(Sharedpreferences.getInstance(context).obtenValorString(Constans.IDIOMA_SEND, "es"));

        bloodParam.setDispositivo_id(idPulsera);
        bloodParam.setValor(Sharedpreferences.getInstance(context).obtenValorString(Constans.BLOOD_PRESSURE_KEY, "0/0"));
        bloodParam.setDispositivo_parametro_id(4);
        bloodParam.setFecha(Utils.getHora());
        bloodParam.setIdioma(Sharedpreferences.getInstance(context).obtenValorString(Constans.IDIOMA_SEND, "es"));

        String json = "[" + temperatureParam.toJSON() + "," + heartRateParam.toJSON() + "," + oxygenationParam.toJSON() + "," + heartRateParam.toJSON() + "," + bloodParam.toJSON() +  "]";
        new SendDataEgesio(context).execute(json);


    }

    protected String doInBackground(String... json) {

        try {
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(json[0], JSON);
            Request request = new Request.Builder()
                    .header("Content-Type","")
                    .header("responseType","")
                    .header("Access-Control-Allow-Methods","")
                    .header("Access-Control-Allow-Origin","")
                    .header("Access-Control-Allow-Credentials","")
                    .header("Authorization", Sharedpreferences.getInstance(context).obtenValorString(Constans.TOKEN_SEND, "0"))
                    .header("idioma",Sharedpreferences.getInstance(context).obtenValorString(Constans.IDIOMA_SEND, "es"))
                    .url(Sharedpreferences.getInstance(context).obtenValorString(Constans.URL_SERVICE_EGESIO, "0"))
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    new SendDataFirebase(context).execute("{\"action\": \"ERROR: " +  e.getMessage() +  Utils.getHora() + "\"}");
                    e.printStackTrace();
                }
                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        new SendDataFirebase(context).execute("{\"action\": \"ERROR: " + response.message() +  Utils.getHora() + "\"}");
                        throw new IOException("Unexpected code " + response);
                    } else {
                        Log.d("Response", response.body().toString());
                        new SendDataFirebase(context).execute("{\"action\": \"ESCRIBIO EN BD  - " + Utils.getHora() + "\"}");
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";

    }

    protected void onPostExecute(String feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
    }

}