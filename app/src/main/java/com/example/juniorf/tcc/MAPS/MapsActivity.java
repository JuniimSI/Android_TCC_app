package com.example.juniorf.tcc.MAPS;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.os.AsyncTask;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.juniorf.tcc.CONFIG.AppController;
import com.example.juniorf.tcc.CONSTANTS.Functions;
import com.example.juniorf.tcc.DAO.MyLocationDAO;
import com.example.juniorf.tcc.DetalhesActivity;
import com.example.juniorf.tcc.MODEL.MyLocation;
import com.example.juniorf.tcc.R;
import com.example.juniorf.tcc.TUTORIAL.TutorialActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.YELLOW;

public class MapsActivity extends FragmentActivity implements  OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, Functions {

    private GoogleMap mMap;
    private GoogleApiClient apiClient;
    private Location mLastLocation;
    private ProgressDialog progressPontos;
    private ProgressDialog progressRota;
    private ProgressDialog progressInsert;
    private ProgressDialog progressDetails;
    private ArrayList<MyLocation> lista;
    private List<LatLng> rotas;
    private Polyline polyline;

    ////////Emails
    public String email;
    public String emailDestinos = "";


    ////////Types
    public Spinner types;
    public String typeToken;
    public ImageButton helpButton;

    ////////URL
    private String urlJsonEmailLocation = "http://grainmapey.pe.hu/GranMapey/find_email_location_by_id.php?id=";


    String[] Languages = { "Search por Tipo", "Restaurante", "Banco", "Bar","Eventos","Oficina", "Hospital/Postos", "Posto de Gasolina","Troca", "Venda", "Museus","LavaJato" };
    Integer[] images = { 0, R.drawable.restaurante, R.drawable.banco, R.drawable.bar, R.drawable.eventos, R.drawable.oficina,R.drawable.hospital_posto, R.drawable.posto_gasolina,R.drawable.troca,  R.drawable.venda, R.drawable.museum, R.drawable.lavajato};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Tutorial help button
        helpButton = (ImageButton) findViewById(R.id.help);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TutorialActivity.class);
                startActivity(i);
                finish();
            }
        });

        ///Verificando google services
        ///TYPES SPINNER
        types = (Spinner) findViewById(R.id.types);

        types.setAdapter(new TypesAdapter(MapsActivity.this, R.layout.spinner_item, Languages));
        //Método do Spinner para capturar o item selecionado
        types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                if (posicao == 0 && verificaGPS()==true)
                    Toast.makeText(MapsActivity.this, "Selecione um Tipo", Toast.LENGTH_SHORT).show();
                else if(verificaGPS() == false){
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }else {
                    String nome = parent.getItemAtPosition(posicao).toString();
                    if (nome == "Restaurante") {
                        eraseMarkers();
                        adicionaMarkerGoogle("restaurant");
                    } else if (nome == "Banco") {
                        eraseMarkers();
                        adicionaMarkerGoogle("bank");
                    } else if (nome == "Bar") {
                        eraseMarkers();
                        adicionaMarkerGoogle("bar");
                    } else if (nome == "LavaJato") {
                        eraseMarkers();
                        adicionaMarkerGoogle("car_wash");
                    }
                    else if (nome == "Eventos") {
                        eraseMarkers();
                        serviceSearch("evento");
                    } else if (nome == "Oficina") {
                        eraseMarkers();
                        adicionaMarkerGoogle("car_repair");
                    } else if (nome == "Venda") {
                        eraseMarkers();
                        serviceSearch("venda");
                    } else if (nome == "Troca") {
                        eraseMarkers();
                        serviceSearch("troca");
                    } else if (nome == "Hospital/Postos") {
                        eraseMarkers();
                        adicionaMarkerGoogle("hospital");
                    } else if (nome == "Posto de Gasolina") {
                        eraseMarkers();
                        adicionaMarkerGoogle("gas_station");
                    } else if (nome == "Museus") {
                        eraseMarkers();
                        adicionaMarkerGoogle("museum");
                    } else {
                        eraseMarkers();
                        serviceSearch(nome);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ///////////////////Progress////////////////////////
        progressRota = new ProgressDialog(this);
        progressRota.setMessage("Aguardando calcular a rota...");
        progressRota.setCancelable(true);

        progressInsert = new ProgressDialog(this);
        progressInsert.setMessage("Inserindo novo ponto...");
        progressInsert.setCancelable(true);

        progressPontos = new ProgressDialog(this);
        progressPontos.setMessage("Aguardando atualizar os pontos de interesse...");
        progressPontos.setCancelable(true);

        progressDetails = new ProgressDialog(this);
        progressDetails.setMessage("Aguardando receber detalhes...");
        progressDetails.setCancelable(true);

        ///Do intent bundle da main
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        this.email = bundle.getString("emailOrigem");
        Toast.makeText(this, "EmailOrigem emal"+email, Toast.LENGTH_SHORT).show();
        //Components

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        apiClient = new GoogleApiClient
                .Builder(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        apiClient.connect();
    }

    public void drawMarkers(String type){
        if(type.equals("restaurant")){
            if(lista.size() == 0){
                Toast.makeText(this, "Nenhum ponto encontrado.", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurante)).position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurante)).position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(String.valueOf(lista.get(i).getId())));
            }
        }else if(type.equals("bank")){
            if(lista.size() == 0){
                Toast.makeText(this, "Nenhum ponto encontrado.", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.banco)).position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.banco)).position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(String.valueOf(lista.get(i).getId())));
            }
        } else if(type.equals("bar")){
            if(lista.size() == 0){
                Toast.makeText(this, "Nenhum ponto encontrado.", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.bar)).position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.bar)).position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(String.valueOf(lista.get(i).getId())));
            }
        }else if(type.equals("car_wash")){
            if(lista.size() == 0){
                Toast.makeText(this, "Nenhum ponto encontrado.", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.lavajato)).position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.lavajato)).position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(String.valueOf(lista.get(i).getId())));
            }
        }else if(type.equals("museum")){
            if(lista.size() == 0){
                Toast.makeText(this, "Nenhum ponto encontrado.", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.museum)).position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.museum)).position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(String.valueOf(lista.get(i).getId())));
            }
        }else if(type.equals("gas_location")){
            if(lista.size() == 0){
                Toast.makeText(this, "Nenhum ponto encontrado.", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.posto_gasolina)).position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.posto_gasolina)).position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(String.valueOf(lista.get(i).getId())));
            }
        }else if(type.equals("car_repair")){
            if(lista.size() == 0){
                Toast.makeText(this, "Nenhum ponto encontrado.", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.oficina)).position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.oficina)).position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(String.valueOf(lista.get(i).getId())));
            }
        } else if(type.equals("troca")){
            if(lista.size() == 0){
                Toast.makeText(this, "Nenhum ponto encontrado.", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.troca)).position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.troca)).position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(String.valueOf(lista.get(i).getId())));
            }
        }
        else if(type.equals("evento")){
            if(lista.size() == 0){
                Toast.makeText(this, "Nenhum ponto encontrado.", Toast.LENGTH_SHORT).show();
                return;
            }

            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.eventos)).position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.eventos)).position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(String.valueOf(lista.get(i).getId())));
            }
        }else if(type.equals("venda")){
            if(lista.size() == 0){
                Toast.makeText(this, "Nenhum ponto encontrado.", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.venda)).position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.venda)).position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(String.valueOf(lista.get(i).getId())));
            }
        }else if(type.equals("hospital")){
            if(lista.size() == 0){
                Toast.makeText(this, "Nenhum ponto encontrado.", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital_posto)).position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital_posto)).position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(String.valueOf(lista.get(i).getId())));
            }
        }
    }

    @Override
    public boolean isOnline(Context c) {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            return manager.getActiveNetworkInfo() != null &&
                    manager.getActiveNetworkInfo().isConnectedOrConnecting();

    }

    public class TypesAdapter extends ArrayAdapter {

        public TypesAdapter(Context context, int textViewResourceId,
                            String[] objects) {
            super(context, textViewResourceId, objects);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.spinner_item, parent, false);

            TextView tvLanguage = (TextView) layout.findViewById(R.id.tvLanguage);
            tvLanguage.setText(Languages[position]);
            tvLanguage.setTextColor(Color.rgb(75, 180, 225));
            ImageView img = (ImageView) layout.findViewById(R.id.imgLanguage);
            img.setImageResource(images[position]);

            if (position == 0) {
                img.setVisibility(View.GONE);
                tvLanguage.setTextSize(20f);
                tvLanguage.setTextColor(Color.BLACK);
            }
            return layout;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

    }

    public void launchRingDialog() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(MapsActivity.this, "Please wait ...", "Downloading Image ...", true);
        ringProgressDialog.setCancelable(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Here you should write your time consuming task...
                    // Let the progress ring for 10 seconds...
                    Thread.sleep(10000);
                } catch (Exception e) {

                }
                ringProgressDialog.dismiss();
            }
        }).start();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        rotas = new ArrayList<LatLng>();
        lista = new ArrayList<MyLocation>();
        //getMarkers();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mMap != null) {

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    geraRota(displayLocation(), marker.getPosition());
                }
            });
            mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
                @Override
                public void onInfoWindowLongClick(final Marker marker) {
                    if(verificaGPS()==false){
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }else {

                            MyLocationDAO locationDAO = new MyLocationDAO(getApplicationContext());

                            final String[] emailz = new String[1];
                                RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
                                StringRequest request = new StringRequest(Request.Method.GET, urlJsonEmailLocation+marker.getSnippet(), new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            if (response.startsWith("ï»¿")) { response = response.substring(3); }
                                            JSONArray jsonArray = new JSONArray(response);
                                            emailz[0] = new String();
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject person = (JSONObject) jsonArray.get(i);
                                                emailz[0] = person.getString("email");


                                                emailDestinos = emailz[0];
                                                
                                                    Intent is = new Intent(MapsActivity.this, DetalhesActivity.class);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("emailOrigem", String.valueOf(email));
                                                    bundle.putString("emailDestino", String.valueOf(emailDestinos));
                                                    bundle.putString("lat", String.valueOf(marker.getPosition().latitude));
                                                    bundle.putString("lng", String.valueOf(marker.getPosition().longitude));
                                                    bundle.putString("typeToken", typeToken);
                                                    bundle.putString("place_id", marker.getSnippet());
                                                    bundle.putString("local", marker.getTitle());
                                                    is.putExtras(bundle);
                                                    startActivity(is);

                                               Toast.makeText(getApplicationContext(), "kkkk ta certo?"+emailz[0], Toast.LENGTH_SHORT).show();

                                            }
                                        }catch (JSONException e) {
                                            Toast.makeText(getApplicationContext(), "k"+response, Toast.LENGTH_SHORT).show();
                                            Log.d("No", response);
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        VolleyLog.d("TAG", "Error:soraia " + error.getMessage());
                                    }
                                })
                                {
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> parameters = new HashMap<String , String>();
                                        parameters.put("id", (marker.getSnippet()));
                                        return parameters;
                                    }
                                };
                                int socketTimeout = 30000;//30 seconds - change to what you want
                                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                                request.setRetryPolicy(policy);
                                AppController.getInstance().addToRequestQueue(request);

                    }
                }
            });

            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(final Marker marker) {

                    
                    View view = getLayoutInflater().inflate(R.layout.info_window, null);
                    TextView txLocality = (TextView) view.findViewById(R.id.tvLocality);
                    TextView txLat = (TextView) view.findViewById(R.id.tvLat);
                    TextView txLng = (TextView) view.findViewById(R.id.tvLng);
                    TextView txSnippet = (TextView) view.findViewById(R.id.tvSnippet);
                    Button b = (Button) view.findViewById(R.id.btn);

                    LatLng ll = marker.getPosition();
                    txLocality.setText(marker.getTitle());
                    txLat.setText("Latitude : " + ll.latitude);
                    txLng.setText("Longitude : " + ll.longitude);
                    txSnippet.setText(marker.getSnippet());
                    return view;

                    
                }

                @Override
                public View getInfoContents(final Marker marker) {
                    return null;
                }
            });
        }

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            Marker first;

            @Override
            public void onMarkerDragStart(final Marker marker) {
                emailDestinos="";

                MyLocationDAO locationDAO = new MyLocationDAO(getApplicationContext());

                final String[] emailz = new String[1];
                    RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest request = new StringRequest(Request.Method.GET, urlJsonEmailLocation+marker.getSnippet(), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                if (response.startsWith("ï»¿")) { response = response.substring(3); }
                                JSONArray jsonArray = new JSONArray(response);
                                emailz[0] = new String();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject person = (JSONObject) jsonArray.get(i);
                                    emailz[0] = person.getString("email");


                                    emailDestinos = emailz[0];
                                     Toast.makeText(MapsActivity.this, "iniStartDrag"+marker.getSnippet()+"+"+emailDestinos, Toast.LENGTH_SHORT).show();

                                        if(email==null){
                                            first = marker;
                                        } if(!email.equals(emailDestinos)){
                                            Toast.makeText(MapsActivity.this, email+" "+emailDestinos+" Você não tem autorização", Toast.LENGTH_SHORT).show();
                                            first = marker;
                                        }

                                   Toast.makeText(getApplicationContext(), "kkkk ta certo?"+emailz[0], Toast.LENGTH_SHORT).show();

                                }
                            }catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), "k"+response, Toast.LENGTH_SHORT).show();
                                Log.d("No", response);
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d("TAG", "Error:soraia " + error.getMessage());
                        }
                    })
                    {
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> parameters = new HashMap<String , String>();
                            parameters.put("id", (marker.getSnippet()));
                            return parameters;
                        }
                    };
                    int socketTimeout = 30000;//30 seconds - change to what you want
                    RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    request.setRetryPolicy(policy);
                    AppController.getInstance().addToRequestQueue(request);

            }

            @Override
            public void onMarkerDrag(Marker marker) {
                marker.hideInfoWindow();
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if(email!=null) {
                    Log.i("COMP",  email +"-"+emailDestinos);
                    if (email.equals(emailDestinos)) {
                        Geocoder gc = new Geocoder(MapsActivity.this);
                        LatLng ll = marker.getPosition();

                        marker.showInfoWindow();

                        MyLocationDAO locationDAO = new MyLocationDAO(getApplicationContext());
                        locationDAO.update(marker.getPosition(), marker.getSnippet() , getApplicationContext());

                    } else {
                        eraseMarkers();
                        adicionaMarkerGoogle(typeToken);
                        marker.setPosition(first.getPosition());
                        Toast.makeText(MapsActivity.this, "Você não tem permissão.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    eraseMarkers();
                    adicionaMarkerGoogle(typeToken);
                    marker.setPosition(first.getPosition());
                }
            }
        });
        
    
        mMap.setMyLocationEnabled(true);

        //______________________________________________________________________________//


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                if (email != null && verificaGPS() == true) {
                    final String[] n = {"", ""};

                    LayoutInflater layoutInflater = LayoutInflater.from(MapsActivity.this);
                    View pront = layoutInflater.inflate(R.layout.insert_location, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                    alertDialogBuilder.setView(pront);

                    final EditText nome = (EditText) pront.findViewById(R.id.nomeEditText);
                    final EditText telefone = (EditText) pront.findViewById(R.id.telefoneEditText);
                    final Spinner tipo = (Spinner) pront.findViewById(R.id.spinner);
                    final String[] type = {""};
                    tipo.setAdapter(new TypesAdapter(MapsActivity.this, R.layout.spinner_item, Languages));
                    tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            type[0] = parent.getItemAtPosition(position).toString();
                            if (position == 0)
                                Toast.makeText(MapsActivity.this, "Selecione um Tipo", Toast.LENGTH_SHORT).show();
                            else if (type[0] == "Restaurante") {
                                type[0] = ("restaurant");
                                return;
                            } else if (type[0] == "Banco") {
                                type[0] = ("bank");
                                return;
                            } else if (type[0] == "Bar") {
                                type[0] = ("bar");
                                return;
                            } else if (type[0] == "LavaJato") {
                                type[0] = ("car_wash");
                                return;
                            }
                            else if (type[0] == "Evento") {
                                adicionaMarkerGoogle("event");
                                return;
                            } else if (type[0] == "Oficina") {
                                type[0] = ("car_repair");
                                return;
                            } else if (type[0] == "Venda") {
                                type[0] = ("venda");
                                return;
                            } else if (type[0] == "Troca") {
                                type[0] = ("troca");
                                return;
                            } else if (type[0] == "Hospital/Postos") {
                                type[0] = ("hospital");
                                return;
                            } else if (type[0] == "Posto de Gasolina") {
                                type[0] = ("gas_station");
                                return;
                            } else if (type[0] == "Museus") {
                                type[0] = ("museum");
                                return;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    alertDialogBuilder.setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            n[0] = nome.getText().toString();
                            n[1] = telefone.getText().toString();

                            MyLocation location = new MyLocation();
                            MyLocationDAO locationDAO = new MyLocationDAO(getApplicationContext());
                            location.setLat(latLng.latitude);
                            location.setLng(latLng.longitude);
                            location.setNome(n[0]);
                            location.setTelefone(n[1]);
                            location.setTipo(type[0]);
                            location.setEmail(email);
                            locationDAO.insert(location, getApplicationContext());
                            Toast.makeText(MapsActivity.this, "Inserido com sucesso!", Toast.LENGTH_SHORT).show();

                            if(type[0]!=null) {
                                eraseMarkers();
                                adicionaMarkerGoogle(type[0]);
                            }


                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            return;
                        }
                    });
                    AlertDialog alert = alertDialogBuilder.create();
                    alert.show();
                }else{
                    Toast.makeText(MapsActivity.this, "Faça o login ou habilite GPS para inserir um ponto.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getMarkers() {
        show();
    }

    private void updateMarkers(){
        showProgressPontos();
        eraseMarkers();
        getMarkers();
        hideProgressPontos();

    }

    private void adicionaMarkerGoogle(String type){
        typeToken=type;
        if(verificaGPS() == true)
            placeSearch(type);
        else
            Toast.makeText(this, "Verifique o GPS", Toast.LENGTH_SHORT).show();
    }

    private void eraseMarkers(){
        lista.removeAll(lista);
        lista = new ArrayList<MyLocation>();
        mMap.clear();
        // Removes all markers, overlays, and polylines from the map.
    }

    public void geraRota(LatLng ll1, final LatLng ll2) {
        if(verificaGPS()==false){
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }else{
            getRoute(ll1, ll2);
        }
    }

    public void getRoute(final LatLng origin, final LatLng destination) {
        showProgressRota();
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.latitude + "," +
                origin.longitude + "&destination=" + destination.latitude + "," + destination.longitude + "&key="+"AIzaSyAVXsswDxxILvxxPAeTrtMHrQ0m_pYWOpQ";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject result = new JSONObject(response.toString());
                    JSONArray routes = result.getJSONArray("routes");

                    // distance = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getInt("value");
                    JSONArray steps = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
                    rotas = new ArrayList<LatLng>();

                    for (int i = 0; i < steps.length(); i++) {
                        String polyline = steps.getJSONObject(i).getJSONObject("polyline").getString("points");
                        for (LatLng ll : decodePolyline(polyline)) {
                            rotas.add(ll);
                        }
                    }
                    drawRoute();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hideProgressRota();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Carregando..", Toast.LENGTH_SHORT).show();
            }

        });

        AppController.getInstance().addToRequestQueue(req);
    }

    public void drawRoute() {
        PolylineOptions po;
        if (polyline == null) {
            po = new PolylineOptions();
            for (int i = 0; i < rotas.size(); i++) {
                po.add(rotas.get(i));
            }
            po.color(BLUE).width(8);
            polyline = mMap.addPolyline(po);

        } else {
            polyline.setPoints(rotas);
        }

    }

    public List<LatLng> decodePolyline(String enconded) {
        List<LatLng> listPoints = new ArrayList<LatLng>();
        int index = 0, len = enconded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = enconded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = enconded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
            listPoints.add(p);
        }
        return listPoints;
    }

    public void serviceSearch(final String type){
        showProgressPontos();
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, "http://grainmapey.pe.hu/GranMapey/show_location_type.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject person = (JSONObject) jsonArray.get(i);
                        MyLocation myLocation = new MyLocation();
                        String id = person.getString("id");
                        String nome = person.getString("Nome");
                        String telefone = person.getString("Telefone");
                        String lat = person.getString("Lat");
                        String lng = person.getString("Lng");
                        String tipo = person.getString("Tipo");
                        String email = person.getString("Email");
                        myLocation.setId(Integer.parseInt(id));
                        myLocation.setTipo(tipo);
                        myLocation.setNome(nome);
                        myLocation.setEmail(email);
                        myLocation.setTelefone(telefone);
                        myLocation.setLat(Double.parseDouble(lat));
                        myLocation.setLng(Double.parseDouble(lng));
                        lista.add(myLocation);
                    }
                    drawMarkers(type);
                    hideProgressPontos();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
            }
        })
        {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String , String>();
                parameters.put("typez", type);
                return parameters;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(request);
    }

    private void placeSearch(final String type) {
        showProgressPontos();
        if(isOnline(getApplicationContext())){
            RequestQueue queue = Volley.newRequestQueue(this);
            LatLng l = displayLocation();
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+l.latitude+","+l.longitude+"&radius=5000&type="+type+"&key="+"AIzaSyAVXsswDxxILvxxPAeTrtMHrQ0m_pYWOpQ";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jo = new JSONObject(response);
                                String c = jo.getString("results");
                                JSONArray jsonArray = new JSONArray(c);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject person = (JSONObject) jsonArray.get(i);
                                    MyLocation myLocation = new MyLocation();
                                    myLocation.setNome(person.getString("name"));
                                    myLocation.setId_reference(person.getString("place_id"));
                                    myLocation.setLat(Double.parseDouble(person.getJSONObject("geometry").getJSONObject("location").getString("lat")));
                                    myLocation.setLng(Double.parseDouble(person.getJSONObject("geometry").getJSONObject("location").getString("lng")));
                                    lista.add(myLocation);
                                }
                                hideProgressPontos();
                                serviceSearch(type);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MapsActivity.this, "Erro na requisição de detalhes!", Toast.LENGTH_SHORT).show();
                }
            });

            queue.add(stringRequest);
        }else{
            hideProgressPontos();
            Toast.makeText(this, "Verifique sua conexão e tente novamente.", Toast.LENGTH_SHORT).show();
        }
    }

    public void show() {
        showProgressPontos();
        JsonArrayRequest req = new JsonArrayRequest("http://grainmapey.pe.hu/GranMapey/show_location.php", new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject person = (JSONObject) response.get(i);
                        MyLocation myLocation = new MyLocation();
                        String id = person.getString("id");
                        String nome = person.getString("Nome");
                        String telefone = person.getString("Telefone");
                        String lat = person.getString("Lat");
                        String lng = person.getString("Lng");
                        String tipo = person.getString("Tipo");
                        String email = person.getString("Email");
                        myLocation.setId(Integer.parseInt(id));
                        myLocation.setTipo(tipo);
                        myLocation.setNome(nome);
                        myLocation.setEmail(email);
                        myLocation.setTelefone(telefone);
                        myLocation.setLat(Double.parseDouble(lat));
                        myLocation.setLng(Double.parseDouble(lng));
                        lista.add(myLocation);

                    }
                    for (int i = 0; i < lista.size(); i++) {
                        LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                        mMap.addMarker(new MarkerOptions().position(latlng).draggable(true).title(lista.get(i).getNome()).snippet(lista.get(i).getEmail()));
                    }
                    hideProgressPontos();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);

    }

    private boolean verificaGPS(){
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showProgressRota() {
        if (!progressRota.isShowing())
            progressRota.show();
    }

    private void hideProgressRota() {
        if (progressRota.isShowing())
            progressRota.dismiss();
    }

    @Override
    protected void onResume() {
        if(!verificaGPS()){
            Toast.makeText(this, "Habilite o GPS para obter resultados", Toast.LENGTH_SHORT).show();
        }types.setSelection(0);
        super.onResume();
    }

    private void showProgressPontos() {
        if (!progressPontos.isShowing())
            progressPontos.show();
    }

    private void hideProgressPontos() {
        if (progressPontos.isShowing())
            progressPontos.dismiss();
    }

    private LatLng displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            return new LatLng(latitude, longitude);
        } else {
            Toast.makeText(this, "Habilite o GPS para usar as funcionalidades do App", Toast.LENGTH_SHORT).show();
        }
        return null;
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Falha na conexão, tente novamente.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {    }

}