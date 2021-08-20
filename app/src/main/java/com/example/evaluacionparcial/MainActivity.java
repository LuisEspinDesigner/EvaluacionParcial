package com.example.evaluacionparcial;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "mvm";
    Button Carga, Procesa, VerInfo;
    ImageView imagen;
    TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    TextView resultado;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Carga = findViewById(R.id.btncarga);
        imagen = (ImageView) findViewById(R.id.Imagen);
        VerInfo = findViewById(R.id.VerInfo);

        resultado = findViewById(R.id.editTextTextMultiLine);
        Allpaises();
        Carga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openArchivos();
            }
        });
        Procesa = findViewById(R.id.btnprocesa);
        Procesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Convertir(MIpath);
            }
        });
        imagen = (ImageView) findViewById(R.id.Imagen);
        VerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!resultado.getText().toString().equals("")) {
                    Pais ResPais = RecorrePais(resultado.getText().toString());
                    if (!(ResPais == null)) {
                        Intent ActMapa = new Intent(v.getContext(), Mapa.class)
                                .putExtra("nombre", ResPais.getNombre())
                                .putExtra("coordenadas",ResPais.getCoordenadas())
                                .putExtra("url",ResPais.getUrl())
                                .putExtra("telPref",ResPais.getTelf())
                                .putExtra("capital",ResPais.getCapital())
                                .putExtra("centro",ResPais.getCenter());
                        startActivity(ActMapa);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No hay texto", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    ArrayList<Pais> Listapais;

    private Pais RecorrePais(String nombre) {
        for (Pais pais : Listapais) {
            if (pais.getNombre().toUpperCase().equals(nombre.toUpperCase())) {
                return pais;
            }
        }
        return null;
    }

    public void Allpaises() {
        Listapais = new ArrayList<>();
        String UrlRegions = "http://www.geognos.com/api/en/countries/info/all.json";
        StringRequest llamado = new StringRequest(Request.Method.GET, UrlRegions, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONObject jresults = jsonObject.getJSONObject("Results");
                        Iterator<?> iterator = jresults.keys();
                        while (iterator.hasNext()) {
                            String key = (String) iterator.next();
                            JSONObject paisJson = jresults.getJSONObject(key);
                            Pais pais = new Pais();
                            pais.setNombre(paisJson.getString("Name"));
                            pais.setCoordenadas(paisJson.getString("GeoRectangle"));
                            try {
                                JSONObject capital = paisJson.getJSONObject("Capital");
                                pais.setCapital(capital.getString("Name"));
                            } catch (Exception e) {
                                pais.setCapital("Sin datos de Capital");
                            }
                            pais.setCenter(paisJson.getString("GeoPt"));
                            pais.setTelf(paisJson.getString("TelPref"));
                            pais.setUrl("http://www.geognos.com/api/en/countries/flag/" + key + ".png");
                            Listapais.add(pais);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error is ", "" + error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                //params.put("Authorization", "Bearer " + AccessToken);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        RequestQueue ejecVolley = Volley.newRequestQueue(this);
        ejecVolley.add(llamado);
    }

    public void openArchivos() {
        final CharSequence[] opciones = {"Buscar Imagen"};
        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(this);
        alertOpciones.setTitle("Seleccione...");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (opciones[which].toString()) {
                    case "Tomar Foto":
                        break;
                    case "Buscar Imagen":
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent, "Seleccione la app"), 10);
                        break;
                }
            }
        });
        alertOpciones.show();
    }

    Uri MIpath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 10:
                    MIpath = data.getData();
                    imagen.setImageURI(MIpath);
                    break;
                case 1:
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imagen.setImageBitmap(imageBitmap);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + requestCode);
            }
        }
    }

    InputImage inputImage;

    private void Convertir(Uri image) {
        try {
            inputImage = InputImage.fromFilePath(getApplicationContext(), image);
            Task<Text> result = recognizer.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(@NonNull Text text) {
                            resultado.setText(text.getText().replace("\n", ""));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            resultado.setText("Error: " + e.getMessage());
                            Log.d(TAG, "Error: " + e.getMessage());
                        }
                    });
        } catch (Exception e) {
            Log.d(TAG, "error:" + e.getMessage());
        }
    }

}