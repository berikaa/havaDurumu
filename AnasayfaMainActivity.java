package com.example.toshi.havadurumuapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


public class AnasayfaMainActivity extends AppCompatActivity {
    ProgressDialog pd;
    TextView sehir, sicaklik, koordinat, nem, ruzgar, basincc;
    EditText sehirIsmi;
    WebView webView;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anasayfa_main);

        pd = new ProgressDialog(AnasayfaMainActivity.this);
        pd.setMessage("İşlem Gerçekleştiriliyor.Lütfen Bekleyiniz..");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        sehir = (TextView) findViewById(R.id.sehirtextView);
        koordinat = (TextView) findViewById(R.id.koordinattextView);
        sicaklik = (TextView) findViewById(R.id.sicakliktextView);
        nem = (TextView) findViewById(R.id.nemtextView);
        ruzgar = (TextView) findViewById(R.id.ruzgarhizitextView);
        basincc = (TextView) findViewById(R.id.basinctextView);
        Button btnGoster = (Button) findViewById(R.id.havaDurumuButton);
        sehirIsmi = (EditText) findViewById(R.id.sehirIsmi);

        Calendar simdi=Calendar.getInstance();
        int saat=(simdi.get(Calendar.HOUR_OF_DAY));

        if(saat < 19) {
            ImageView img = (ImageView) findViewById(R.id.imgBackground);
            img.setBackgroundDrawable(getResources().getDrawable(R.drawable.bacgroundanimation));
            AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();
            //frameAnimation.setCallback(img);
            //frameAnimation.setVisible(true, true);
            frameAnimation.start();
        }
            else{
            ImageView img = (ImageView) findViewById(R.id.imgBackground);
            img.setBackgroundDrawable(getResources().getDrawable(R.drawable.bacgroundanimationgece));
            AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();
            //frameAnimation.setCallback(img);
            //frameAnimation.setVisible(true, true);
            frameAnimation.start();
        }

        btnGoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                HavaDurumu hd = new HavaDurumu();
                String a = sehirIsmi.getText().toString();
                hd.execute(a);
            }
        });

        takeCurrentGPS();

    }

    public void showAlertDialog(View view) {
        AlertDialog.Builder myAlert = new AlertDialog.Builder(this);

        myAlert.setMessage("Işık ALTUNTAŞ" + "\n" + "Ayşe Berika VAROL")
                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setTitle("Geliştiriciler")
                .create();
        myAlert.show();
    }

    public void takeCurrentGPS() {
        MyLocationProvider gpsTracker = new MyLocationProvider(this, MyLocationProvider.LocationType.BOTH);
        gpsTracker.getLocation();
        gpsTracker.locationListener = new MyLocationListener() {

            @Override
            public void providerDisabled(MyLocationProvider.LocationType providerType) {
                // TODO Auto-generated method stub
            }

            @Override
            public void gotLocation(Location location) {
                try {
                    // TODO Auto-generated method stub
                    if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
                        final double latitude = location.getLatitude();
                        final double longitude = location.getLongitude();
                        pd.show();
                        String[] coords = new String[2];
                        coords[0] = latitude + "";
                        coords[1] = longitude + "";
                        HavaDurumu hd = new HavaDurumu();
                        hd.execute(coords);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        };
    }

    public class HavaDurumu extends AsyncTask<String, Void, String> {
        String geticon;
        String name;
        String sicak;
        String enlem, boylam;
        private com.example.toshi.havadurumuapp.JSONParser JSONParser;

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... voids) {
            String weatherUrl = "";
            if (voids != null) {
                if (voids.length == 1) {
                    weatherUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + voids[0] + "&appid=c19377d6e47371f3c9fd047e2e4b39a7&units=metric";
                } else {
                    weatherUrl = "http://api.openweathermap.org/data/2.5/weather?lat=" + voids[0] + "&lon=" + voids[1] + "&appid=c19377d6e47371f3c9fd047e2e4b39a7&units=metric";
                }
            }

            JSONObject jsonObject = null;
            try {

                String json = JSONParser.getJSONFromUrl(weatherUrl);
                try {
                    jsonObject = new JSONObject(json);
                    return json;
                } catch (JSONException e) {
                    Log.e("JSONPARSER", "Error creating Json Object" + e.toString());
                    return "";
                }
            } catch (Exception e) {
                Log.e("json", "doINbackgrond");
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String args) {
            if (args != null && args != "") {
                try {
                    JSONObject jsonObject = new JSONObject(args);
                    name = jsonObject.getString("name");

                    JSONArray weatherArray = jsonObject.optJSONArray("weather");
                    JSONObject weatherObj = weatherArray.optJSONObject(0);
                    geticon = weatherObj.optString("icon");
                    String main = weatherObj.optString("main");
                    String description = weatherObj.optString("description");

                    JSONObject coordsObj = jsonObject.getJSONObject("coord");
                    enlem = coordsObj.optString("lon");
                    boylam = coordsObj.optString("lat");

                    JSONObject mainObj = jsonObject.getJSONObject("main");
                    sicak = mainObj.optString("temp");
                    String sicakMax = mainObj.optString("temp_max");
                    String sicakMin = mainObj.optString("temp_min");
                    String basinc = mainObj.optString("pressure");
                    String nemOrani = mainObj.optString("humidity");

                    JSONObject windObj = jsonObject.optJSONObject("wind");
                    String speed = windObj.optString("speed");
                    String deg = windObj.optString("deg");

                    JSONObject cloudsObj = jsonObject.optJSONObject("clouds");
                    String all = windObj.optString("all");

                    sehir.setText(name.toUpperCase());
                    basincc.setText("BASINÇ: " + basinc + "\n");
                    nem.setText("NEM ORANI: % " + nemOrani);
                    koordinat.setText("KOORDİNAT: " + enlem + "," + boylam);
                    sicaklik.setText("SICAKLIK: " + sicak + "\u2103");
                    ruzgar.setText("RÜZGAR HIZI: " + speed);
                    sethavaicon(geticon);
                    pd.hide();
                    View view = AnasayfaMainActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(AnasayfaMainActivity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                } catch (JSONException jex) {
                    pd.hide();

                }
            }
        }

        public void sethavaicon(String iconCode) {
            final ImageView iconView = (ImageView) findViewById(R.id.immgIcon);

            if (iconCode.equals("01d")) {
                iconView.setImageResource(R.mipmap.weezle_sun);
                iconView.setImageResource(R.mipmap.weezle_cloud_sun);
            } else if (iconCode.equals("03d")) {
                iconView.setImageResource(R.mipmap.weezle_sun_minimal_clouds);
            } else if (iconCode.equals("04d")) {
                iconView.setImageResource(R.mipmap.weezle_max_cloud);
            } else if (iconCode.equals("09d")) {
                iconView.setImageResource(R.mipmap.weezle_rain);
            } else if (iconCode.equals("10d")) {
                iconView.setImageResource(R.mipmap.weezle_sun_and_rain);
            } else if (iconCode.equals("11d")) {
                iconView.setImageResource(R.mipmap.weezle_cloud_thunder_rain);
            } else if (iconCode.equals("13d")) {
                iconView.setImageResource(R.mipmap.weezle_snow);
            } else if (iconCode.equals("01n")) {
                iconView.setImageResource(R.mipmap.weezle_fullmoon);
            } else if (iconCode.equals("02n")) {
                iconView.setImageResource(R.mipmap.weezle_moon_cloud);
            } else if (iconCode.equals("03n")) {
                iconView.setImageResource(R.mipmap.weezle_cloud);
            } else if (iconCode.equals("04n")) {
                iconView.setImageResource(R.mipmap.weezle_max_cloud);
            } else if (iconCode.equals("09n")) {
                iconView.setImageResource(R.mipmap.weezle_night_rain);
            } else if (iconCode.equals("10n")) {
                iconView.setImageResource(R.mipmap.weezle_moon_cloud_medium);
            } else if (iconCode.equals("11n")) {
                iconView.setImageResource(R.mipmap.weezle_night_thunder_rain);
            } else if (iconCode.equals("13n")) {
                iconView.setImageResource(R.mipmap.weezle_night_and_snow);
            } else if (iconCode.equals("50n")) {
                iconView.setImageResource(R.mipmap.weezle_night_fog);
            } else if (iconCode.equals("50d")) {
                iconView.setImageResource(R.mipmap.weezle_fog);
            }
        }
    }

}
