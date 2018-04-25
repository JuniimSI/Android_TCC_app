package com.example.juniorf.tcc.MAPS;

import android.Manifest;
import android.support.v7.app.AlertDialog;
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
import android.support.design.widget.FloatingActionButton;

import java.lang.Runnable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;

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
import com.example.juniorf.tcc.DAO.TipoDAO;
import com.example.juniorf.tcc.DetalhesActivity;
import com.example.juniorf.tcc.MODEL.MyLocation;
import com.example.juniorf.tcc.MODEL.Tipo;
import com.example.juniorf.tcc.R;
import com.example.juniorf.tcc.TUTORIAL.TutorialActivity;
import com.example.juniorf.tcc.CONSTANTS.UtilMethods;
import com.example.juniorf.tcc.CONSTANTS.Codes;
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
import android.os.Handler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.util.Log;

import static android.graphics.Color.BLUE;

public class MapsActivity extends FragmentActivity implements  OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, Functions {

    private GoogleMap mMap;
    private GoogleApiClient apiClient;
    private Location mLastLocation;
    private ProgressDialog progressPontos;
    private ProgressDialog progressRota;
    private ProgressDialog progressInsert;
    private ProgressDialog progressType;
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
    public String tipoz = "";

    ////////URL
    private String urlJsonEmailLocation = "http://grainmapey.pe.hu/GranMapey/find_email_location_by_id.php?id=";
    private String urlJsonDetailsIdLocation = "http://grainmapey.pe.hu/GranMapey/find_details_by_id_location.php";



    List<String> Languages = new ArrayList<String>();
    Integer[] images = { 0, R.drawable.restaurante, R.drawable.banco, R.drawable.bar, R.drawable.eventos, R.drawable.oficina,R.drawable.hospital_posto, R.drawable.posto_gasolina,R.drawable.troca,  R.drawable.venda, R.drawable.museum, R.drawable.lavajato};


    public void preencheSpinner(){
        progressType.show();
        Languages = null;
        Languages = new ArrayList<String>();
        final List<String> lista = new ArrayList<String>();
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, "http://grainmapey.pe.hu/GranMapey/show_type.php", new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject person = (JSONObject) response.get(i);
                        String tipo = person.getString("Tipo");
                        Languages.add(tipo);
                    }
                    types.setAdapter(new TypesAdapter(MapsActivity.this, R.layout.spinner_item, Languages));
                    progressType.dismiss();
                }
                catch (JSONException e) {
                    UtilMethods.error(MapsActivity.this);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UtilMethods.error(MapsActivity.this);
                VolleyLog.d("TAG", "Error: " + error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(req);
    }

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

        
        ///////////////////Progress////////////////////////
        progressRota = new ProgressDialog(this);
        progressRota.setMessage("Aguardando calcular a rota...");
        progressRota.setCancelable(true);


        progressType = new ProgressDialog(this);
        progressType.setMessage("Aguardando receber Tipos...");
        progressType.setCancelable(false);

        progressInsert = new ProgressDialog(this);
        progressInsert.setMessage("Inserindo novo ponto...");
        progressInsert.setCancelable(true);

        progressPontos = new ProgressDialog(this);
        progressPontos.setMessage("Aguardando atualizar os pontos de interesse...");
        progressPontos.setCancelable(true);

        progressDetails = new ProgressDialog(this);
        progressDetails.setMessage("Aguardando receber detalhes...");
        progressDetails.setCancelable(true);

        ///Verificando google services
        ///TYPES SPINNER
        types = (Spinner) findViewById(R.id.types);


        ///Preenchendo as linguagens com os valores do banco
        preencheSpinner();


        //Método do Spinner para capturar o item selecionado
        types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                //types.getSelectedItem()
                if (posicao == 0 && verificaGPS()==true)
                    Toast.makeText(MapsActivity.this, "Selecione um Tipo", Toast.LENGTH_SHORT).show();
                else if(verificaGPS() == false){
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }else {
                    String nome = parent.getItemAtPosition(posicao).toString();
                    if (nome.equals("Restaurante")) {
                        eraseMarkers();
                        adicionaMarkerGoogle("restaurant");
                    } else if (nome.equals("Banco")) {
                        eraseMarkers();
                        adicionaMarkerGoogle("bank");
                    } else if (nome.equals("Bar")) {
                        eraseMarkers();
                        adicionaMarkerGoogle("bar");
                    } else if (nome.equals("LavaJato")) {
                        eraseMarkers();
                        adicionaMarkerGoogle("car_wash");
                    } else if (nome.equals("Eventos")) {
                        eraseMarkers();
                        serviceSearch("evento");
                    } else if (nome.equals("Oficina")) {
                        eraseMarkers();
                        adicionaMarkerGoogle("car_repair");
                    } else if (nome.equals( "Venda")) {
                        eraseMarkers();
                        serviceSearch("venda");
                    } else if (nome.equals("Troca")) {
                        eraseMarkers();
                        serviceSearch("troca");
                    } else if (nome.equals("Hospital/Postos")) {
                        eraseMarkers();
                        adicionaMarkerGoogle("hospital");
                    } else if (nome.equals("Posto de Gasolina")) {
                        eraseMarkers();
                        adicionaMarkerGoogle("gas_station");
                    } else if (nome .equals("Museus")) {
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

        


        ///Do intent bundle da main
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        this.email = bundle.getString("emailOrigem");
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
            tipoz="restaurante";
            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurante)).position(latlng).draggable(true).title(lista.get(i).getNome() +"\n"+lista.get(i).getHorarioFuncionamento()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurante)).position(latlng).draggable(true).title(lista.get(i).getNome()+"\n"+lista.get(i).getHorarioFuncionamento()).snippet(String.valueOf(lista.get(i).getId())));
            }
        }else if(type.equals("bank")){
            if(lista.size() == 0){
                Toast.makeText(this, "Nenhum ponto encontrado.", Toast.LENGTH_SHORT).show();
                return;
            } tipoz = "banco";
            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.banco)).position(latlng).draggable(true).title(lista.get(i).getNome() +"\n"+lista.get(i).getHorarioFuncionamento()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.banco)).position(latlng).draggable(true).title(lista.get(i).getNome()+"\n"+lista.get(i).getHorarioFuncionamento()).snippet(String.valueOf(lista.get(i).getId())));
            }
        } else if(type.equals("bar")){
            if(lista.size() == 0){
                Toast.makeText(this, "Nenhum ponto encontrado.", Toast.LENGTH_SHORT).show();
                return;
            } tipoz = "bar";
            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.bar)).position(latlng).draggable(true).title(lista.get(i).getNome() +"\n"+lista.get(i).getHorarioFuncionamento()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.bar)).position(latlng).draggable(true).title(lista.get(i).getNome()+"\n"+lista.get(i).getHorarioFuncionamento()).snippet(String.valueOf(lista.get(i).getId())));
            }
        }else if(type.equals("car_wash")){
            if(lista.size() == 0){
                Toast.makeText(this, "Nenhum ponto encontrado.", Toast.LENGTH_SHORT).show();
                return;
            } tipoz = "lavajato";
            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.lavajato)).position(latlng).draggable(true).title(lista.get(i).getNome() +"\n"+lista.get(i).getHorarioFuncionamento()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.lavajato)).position(latlng).draggable(true).title(lista.get(i).getNome()+"\n"+lista.get(i).getHorarioFuncionamento()).snippet(String.valueOf(lista.get(i).getId())));
            }
        }else if(type.equals("museum")){
            if(lista.size() == 0){
                Toast.makeText(this, "Nenhum ponto encontrado.", Toast.LENGTH_SHORT).show();
                return;
            } tipoz = "museus";
            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.museum)).position(latlng).draggable(true).title(lista.get(i).getNome() +"\n"+lista.get(i).getHorarioFuncionamento()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.museum)).position(latlng).draggable(true).title(lista.get(i).getNome()+"\n"+lista.get(i).getHorarioFuncionamento()).snippet(String.valueOf(lista.get(i).getId())));
            }
        }else if(type.equals("gas_location")){
            if(lista.size() == 0){
                Toast.makeText(this, "Nenhum ponto encontrado.", Toast.LENGTH_SHORT).show();
                return;
            } tipoz = "posto_gasolina";
            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.posto_gasolina)).position(latlng).draggable(true).title(lista.get(i).getNome() +"\n"+lista.get(i).getHorarioFuncionamento()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.posto_gasolina)).position(latlng).draggable(true).title(lista.get(i).getNome()+"\n"+lista.get(i).getHorarioFuncionamento()).snippet(String.valueOf(lista.get(i).getId())));
            }
        }else if(type.equals("car_repair")){
            if(lista.size() == 0){
                Toast.makeText(this, "Nenhum ponto encontrado.", Toast.LENGTH_SHORT).show();
                return;
            } tipoz = "oficina";
            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.oficina)).position(latlng).draggable(true).title(lista.get(i).getNome() +"\n"+lista.get(i).getHorarioFuncionamento()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.oficina)).position(latlng).draggable(true).title(lista.get(i).getNome()+"\n"+lista.get(i).getHorarioFuncionamento()).snippet(String.valueOf(lista.get(i).getId())));
            }
        } else if(type.equals("troca")){
            if(lista.size() == 0){
                Toast.makeText(this, "Nenhum ponto encontrado.", Toast.LENGTH_SHORT).show();
                return;
            } tipoz = "troca";
            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.troca)).position(latlng).draggable(true).title(lista.get(i).getNome()+"\n"+lista.get(i).getHorarioFuncionamento()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.troca)).position(latlng).draggable(true).title(lista.get(i).getNome() +"\n"+lista.get(i).getHorarioFuncionamento()).snippet(String.valueOf(lista.get(i).getId())));
            }
        }
        else if(type.equals("evento")){
            if(lista.size() == 0){
                Toast.makeText(this, "Nenhum ponto encontrado.", Toast.LENGTH_SHORT).show();
                return;
            } tipoz = "eventos";

            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.eventos)).position(latlng).draggable(true).title(lista.get(i).getNome()+"\n"+lista.get(i).getHorarioFuncionamento()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.eventos)).position(latlng).draggable(true).title(lista.get(i).getNome()+"\n"+lista.get(i).getHorarioFuncionamento()).snippet(String.valueOf(lista.get(i).getId())));
            }
        }else if(type.equals("venda")){
            if(lista.size() == 0){
                Toast.makeText(this, "Nenhum ponto encontrado.", Toast.LENGTH_SHORT).show();
                return;
            } tipoz = "venda";
            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.venda)).position(latlng).draggable(true).title(lista.get(i).getNome()+"\n"+lista.get(i).getHorarioFuncionamento()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.venda)).position(latlng).draggable(true).title(lista.get(i).getNome()+"\n"+lista.get(i).getHorarioFuncionamento()).snippet(String.valueOf(lista.get(i).getId())));
            }
        }else if(type.equals("hospital")){
            if(lista.size() == 0){
                Toast.makeText(this, "Nenhum ponto encontrado.", Toast.LENGTH_SHORT).show();
                return;
            } tipoz = "hospital_posto";
            Toast.makeText(this, "Id"+lista.get(0).getId_reference(), Toast.LENGTH_SHORT).show();
            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital_posto)).position(latlng).draggable(true).title(lista.get(i).getNome() +"\n"+lista.get(i).getHorarioFuncionamento()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital_posto)).position(latlng).draggable(true).title(lista.get(i).getNome()+"\n"+lista.get(i).getHorarioFuncionamento()).snippet(String.valueOf(lista.get(i).getId())));
            }
        }
        else {
            tipoz = type;
            for (int i = 0; i < lista.size(); i++) {
                LatLng latlng = new LatLng(lista.get(i).getLat(), lista.get(i).getLng());
                if(lista.get(i).getId_reference()!=null)
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker)).position(latlng).draggable(true).title(lista.get(i).getNome() +"\n"+lista.get(i).getHorarioFuncionamento()).snippet(lista.get(i).getId_reference()));
                else
                    mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker)).position(latlng).draggable(true).title(lista.get(i).getNome()+"\n"+lista.get(i).getHorarioFuncionamento()).snippet(String.valueOf(lista.get(i).getId())));
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
                            List<String> objects) {
            super(context, textViewResourceId, objects);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.spinner_item, parent, false);

            TextView tvLanguage = (TextView) layout.findViewById(R.id.tvLanguage);
            tvLanguage.setText(Languages.get(position));
            tvLanguage.setTextColor(Color.rgb(75, 180, 225));
            ImageView img = (ImageView) layout.findViewById(R.id.imgLanguage);
            if(position>11){
                position = position;
                img.setImageResource(R.mipmap.ic_marker); ///Setar imagem padrao caso esteja fora do range
            } else
                img.setImageResource(images[position]); ///Setar imagem padrao caso esteja fora do range

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

    public boolean existArroba(String frase){
        if (frase == null || ! frase.matches("^[0-9]{1,8}$"))
            return false;
        return true;
    }

    public void detailsFind(final Marker marker) {
                    if(verificaGPS()==false){
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }else if(!existArroba(marker.getSnippet())){
                        Intent is = new Intent(MapsActivity.this, DetalhesActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("emailOrigem", String.valueOf(email));
                        bundle.putString("emailDestino", String.valueOf(emailDestinos));
                        bundle.putString("lat", String.valueOf(marker.getPosition().latitude));
                        bundle.putString("lng", String.valueOf(marker.getPosition().longitude));
                        bundle.putString("typeToken", typeToken);
                        bundle.putString("tipoToken", tipoz);
                        bundle.putString("place_id", marker.getSnippet());
                        bundle.putString("id_reference", marker.getSnippet());
                        bundle.putString("local", marker.getTitle());
                        is.putExtras(bundle);
                        startActivity(is);
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
                                                    bundle.putString("tipoToken", tipoz);
                                                    bundle.putString("place_id", marker.getSnippet());
                                                    bundle.putString("local", marker.getTitle());
                                                    is.putExtras(bundle);
                                                    startActivity(is);

                                            }
                                        }catch (JSONException e) {
                                            UtilMethods.error(getApplicationContext());
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        UtilMethods.error(getApplicationContext());
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        rotas = new ArrayList<LatLng>();
        lista = new ArrayList<MyLocation>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mMap != null) {
                ///////////Gera rota quando clica simples no marker/////////
              mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                  @Override
                  public void onInfoWindowClick(Marker marker) {
                      geraRota(displayLocation(), marker.getPosition());
                  }
              });
               ///////////Obtem detalhes quando clica longo no marker/////////
              mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
                  @Override
                  public void onInfoWindowLongClick(Marker marker) {
                      detailsFind(marker);
                  }
              });
                ///////////Gera janela de informações quando clica simples no marker/////////
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(final Marker marker) {
                   return null;
                }

                @Override
                public View getInfoContents(final Marker marker) {

                    View view = getLayoutInflater().inflate(R.layout.info_window, null);
                    TextView txLocality = (TextView) view.findViewById(R.id.tvLocality);

                    ImageView imageInfoWindow = (ImageView) view.findViewById(R.id.imageInfoWindow);
                    imageInfoWindow.setImageResource(getResources().getIdentifier(
                            tipoz, "drawable", getPackageName()));
                    LatLng ll = marker.getPosition();
                    txLocality.setText(marker.getTitle());

                    ///if(marker.do google, faz no google )
                    // else { faz isso ai}
                    
                    return view;
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
                                        if(email==null){
                                            first = marker;
                                        } if(!email.equals(emailDestinos)){
                                            first = marker;
                                        }
                                }
                            }catch (JSONException e) {
                                UtilMethods.error(getApplicationContext());
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            UtilMethods.error(getApplicationContext());
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



        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng latLng){

                if(rotas!=null){
                    rotas=new ArrayList<LatLng>();
                }
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                if (email != null && verificaGPS() == true) {
                    final String[] n = {"", "", "", ""};

                    LayoutInflater layoutInflater = LayoutInflater.from(MapsActivity.this);
                    View pront = layoutInflater.inflate(R.layout.insert_location, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                    alertDialogBuilder.setView(pront);

                    final EditText nome = (EditText) pront.findViewById(R.id.nomeEditText);
                    final EditText telefone = (EditText) pront.findViewById(R.id.telefoneEditText);
                    final EditText detalhes = (EditText) pront.findViewById(R.id.detalhesEditText);
                    final EditText horario = (EditText) pront.findViewById(R.id.horariosEditText);
                    final LinearLayout llspinner = (LinearLayout) pront.findViewById(R.id.llspinner);
                    final LinearLayout lltipocriado = (LinearLayout) pront.findViewById(R.id.lltipocriado);
                    final TextView tipoCriado = (TextView) pront.findViewById(R.id.tipoCriadoId);
                    telefone.setTransformationMethod(null);
                    final Spinner tipo = (Spinner) pront.findViewById(R.id.spinner);
                    final String[] type = {""};

                    FloatingActionButton insertTipoButton = (FloatingActionButton) pront.findViewById(R.id.insertTypeButton);
                    insertTipoButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final String[] n = {"", ""};

                            LayoutInflater layoutInflater = LayoutInflater.from(MapsActivity.this);
                            View pront = layoutInflater.inflate(R.layout.insert_type, null);
                            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(MapsActivity.this);
                            alertDialogBuilder.setView(pront);

                            final EditText nome = (EditText) pront.findViewById(R.id.textAnswer);

                            alertDialogBuilder.setCancelable(false).setPositiveButton("Inserir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    n[0] = nome.getText().toString();
                                    TipoDAO tipoDAO = new TipoDAO(getApplicationContext());
                                    Tipo tp = new Tipo();
                                    tp.setTipo(n[0]);
                                    tipoDAO.insert(tp, getApplicationContext());
                                    type[0] = n[0];
                                    llspinner.setVisibility(View.GONE);
                                    lltipocriado.setVisibility(View.VISIBLE);
                                    tipoCriado.setText(type[0]);

                                }
                            }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    return;
                                }
                            });
                            android.app.AlertDialog alert = alertDialogBuilder.create();
                            alert.show();
                        }
                    });

                    if(type[0] == ""){
                        tipo.setAdapter(new TypesAdapter(MapsActivity.this, R.layout.spinner_item, Languages));
                        tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                type[0] = parent.getItemAtPosition(position).toString();
                                if (position == 0)
                                    Toast.makeText(MapsActivity.this, "Selecione um Tipo", Toast.LENGTH_SHORT).show();
                                else if(type[0] == "Tipos")
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
                                } else {
                                    type[0] = type[0];
                                    return;
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                preencheSpinner();
                            }
                        });
                    }

                    alertDialogBuilder.setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            n[0] = nome.getText().toString();
                            n[1] = telefone.getText().toString();
                            n[2] = detalhes.getText().toString();
                            n[3] = horario.getText().toString();

                            MyLocation location = new MyLocation();
                            MyLocationDAO locationDAO = new MyLocationDAO(getApplicationContext());
                            location.setLat(latLng.latitude);
                            location.setLng(latLng.longitude);
                            location.setNome(n[0]);
                            location.setTelefone(n[1]);
                            location.setDetalhes(n[2]);
                            location.setHorarioFuncionamento(n[3]);
                            if(type[0].equals("Tipos")){
                                Toast.makeText(MapsActivity.this, "Selecione um Tipo", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            type[0] = type[0].replace("\n", "").replace("\t", "").replace(" ","");
                            location.setTipo(type[0]);
                            location.setEmail(email);
                            locationDAO.insert(location, getApplicationContext());
                            preencheSpinner();
                            Toast.makeText(MapsActivity.this, "Inserido com sucesso!", Toast.LENGTH_SHORT).show();

                            if(type[0]!=null) {
                                eraseMarkers();
                                if(type[0] == "Tipos")
                                    Toast.makeText(MapsActivity.this, "Selecione um Tipo", Toast.LENGTH_SHORT).show();
                                else if (type[0] == "Restaurante") {
                                    adicionaMarkerGoogle("restaurant");
                                    return;
                                } else if (type[0] == "Banco") {
                                   adicionaMarkerGoogle("bank");
                                    return;
                                } else if (type[0] == "Bar") {
                                   adicionaMarkerGoogle("bar");
                                    return;
                                } else if (type[0] == "LavaJato") {
                                   adicionaMarkerGoogle("car_wash");
                                    return;
                                } else if (type[0] == "Evento") {
                                    adicionaMarkerGoogle("event");
                                    return;
                                } else if (type[0] == "Oficina") {
                                    adicionaMarkerGoogle("car_repair");
                                    return;
                                } else if (type[0] == "Venda") {
                                   adicionaMarkerGoogle("venda");
                                    return;
                                } else if (type[0] == "Troca") {
                                    adicionaMarkerGoogle("troca");
                                    return;
                                } else if (type[0] == "Hospital/Postos") {
                                    adicionaMarkerGoogle("hospital");
                                    return;
                                } else if (type[0] == "Posto de Gasolina") {
                                    adicionaMarkerGoogle("gas_station");
                                    return;
                                } else if (type[0] == "Museus") {
                                    adicionaMarkerGoogle("museum");
                                    return;
                                } else {
                                    serviceSearch(type[0]);
                                    return;
                                }
                            }
                            preencheSpinner();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            preencheSpinner();
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
                            "Impossivel chegar ao Destino." ,
                            Toast.LENGTH_LONG).show();
                }
                hideProgressRota();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               UtilMethods.error(getApplicationContext());
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
                        String horario = person.getString("Horario_Funcionamento");
                        myLocation.setId(Integer.parseInt(id));
                        myLocation.setTipo(tipo);
                        myLocation.setNome(nome);
                        myLocation.setEmail(email);
                        myLocation.setTelefone(telefone);
                        myLocation.setLat(Double.parseDouble(lat));
                        myLocation.setLng(Double.parseDouble(lng));
                        myLocation.setHorarioFuncionamento(horario);
                        lista.add(myLocation);
                    }
                    drawMarkers(type);
                    hideProgressPontos();
                }
                catch (JSONException e) {
                    UtilMethods.error(getApplicationContext());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UtilMethods.error(getApplicationContext());
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
                                    if(person.has("opening_hours")){
                                        if(person.getJSONObject("opening_hours").getBoolean("open_now") == true)
                                            myLocation.setHorarioFuncionamento("Aberto");        
                                        else
                                            myLocation.setHorarioFuncionamento("Fechado");
                                    }
                                    lista.add(myLocation);
                                }
                                hideProgressPontos();
                                serviceSearch(type);
                            } catch (JSONException e) {
                                UtilMethods.error(getApplicationContext());
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    UtilMethods.error(getApplicationContext());
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
                    UtilMethods.error(getApplicationContext());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UtilMethods.error(getApplicationContext());
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

    public static void error(Context c){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Oops, uma falha aconteceu...");
        builder.setMessage("Tente novamente mais tarde");
        builder.setPositiveButton("OK, voltar!", new DialogInterface.OnClickListener() {
        
        @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        
        builder.show();
    }

}
