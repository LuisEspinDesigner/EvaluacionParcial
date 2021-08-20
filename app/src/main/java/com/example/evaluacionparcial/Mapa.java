package com.example.evaluacionparcial;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Mapa extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mapa;
    String url, name, cap, pref;
    TextView paisName, capital, prefijo, punto;
    Double nor, oeste, este, sur, lat, lon;
    JSONObject cordenadas;
    JSONArray centro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        ImageView imageView = (ImageView) findViewById(R.id.imabandera);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        try {
            cordenadas = new JSONObject(getIntent().getStringExtra("coordenadas"));
            centro = new JSONArray(getIntent().getStringExtra("centro"));
            name = getIntent().getExtras().getString("nombre");
            cap = getIntent().getExtras().getString("capital");
            pref = getIntent().getExtras().getString("telPref");
            url = getIntent().getExtras().getString("url");
            Glide.with(this.getApplicationContext()).load(url).into(imageView);
            paisName = findViewById(R.id.pais);
            capital = findViewById(R.id.capital);
            prefijo = findViewById(R.id.Pref);
            punto = findViewById(R.id.txtpunto);
            paisName.setText(name);
            capital.setText(cap);
            prefijo.setText(pref);
            punto.setText("Lat: " + centro.getString(0) + "  Log: " + centro.getString(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;
        try {
            lat = Double.parseDouble(centro.getString(0));
            lon = Double.parseDouble(centro.getString(1));
            nor = Double.parseDouble(cordenadas.getString("North"));
            oeste = Double.parseDouble(cordenadas.getString("West"));
            este = Double.parseDouble(cordenadas.getString("East"));
            sur = Double.parseDouble(cordenadas.getString("South"));
            mapa.getUiSettings().setZoomControlsEnabled(true);
            CameraUpdate camUpd1 = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 5);
            mapa.moveCamera(camUpd1);
            PolylineOptions lineas = new PolylineOptions()
                    .add(new LatLng(nor, oeste))
                    .add(new LatLng(nor, este))
                    .add(new LatLng(sur, este))
                    .add(new LatLng(sur, oeste))
                    .add(new LatLng(nor, oeste));
            lineas.width(8);
            lineas.color(Color.RED);
            mapa.addPolyline(lineas);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}