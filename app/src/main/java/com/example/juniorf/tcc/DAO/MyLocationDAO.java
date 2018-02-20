package com.example.juniorf.tcc.DAO;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import android.os.AsyncTask;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.juniorf.tcc.CONFIG.AppController;
import com.example.juniorf.tcc.CONSTANTS.VolleyCallback;
import com.example.juniorf.tcc.MODEL.MyLocation;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.type;

/**
 * Created by juniorf on 17/12/16.
 */

public class MyLocationDAO extends AbstractDAO<MyLocation>{


    private String urlJsonInsert = "http://grainmapey.pe.hu/GranMapey/insert.php";
    private String urlJsonInsertLocation = "http://grainmapey.pe.hu/GranMapey/insert_location.php";
    private String urlJsonDetailsLocation = "http://grainmapey.pe.hu/GranMapey/details_location.php";
    private String urlJsonEmailLocation = "http://grainmapey.pe.hu/GranMapey/find_email_location_by_id.php?id=";
    private String urlJsonDeleteLocation = "http://grainmapey.pe.hu/GranMapey/delete_location.php";
    private String urlJsonArry = "http://grainmapey.pe.hu/GranMapey/show_location.php";
    private ProgressDialog pDialog;
    private String Tag = "Akii";
    private ArrayList<MyLocation> lista;
    private String urlJsonUpdateLocation = "http://grainmapey.pe.hu/GranMapey/update_location.php";
    private ProgressDialog detalhes;
    private String ret;

    public MyLocationDAO(Context context) {
        super(context);
    }

    public String findEmailLocationById(final String id, final Context t){
        RequestQueue queue = Volley.newRequestQueue(t);  // this = context
        final String url = "http://grainmapey.pe.hu/GranMapey/find_email_location_by_id.php?id="+id;
        //final String[] ret = new String[1];
        // prepare the Request
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, (JSONObject) null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject email = (JSONObject) response.get(0);

                            ret = email.getString("email").toString();
                            Toast.makeText(t, "Ret1"+ret, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(t, "Ret2"+ret, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response",  ret+error.getMessage()+"erro" );
                    }
                }
        );
        //Toast.makeText(t, "Ret3"+ret, Toast.LENGTH_SHORT).show();
        // add it to the RequestQueue
        queue.add(getRequest);
        return ret;

    }

    public String getEmailLocationById(final String id, final Context t){

        final String[] email = new String[1];
        RequestQueue mRequestQueue = Volley.newRequestQueue(t);
        StringRequest request = new StringRequest(Request.Method.GET, urlJsonEmailLocation+id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response.startsWith("ï»¿")) { response = response.substring(3); }
                    JSONArray jsonArray = new JSONArray(response);
                    email[0] = new String();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject person = (JSONObject) jsonArray.get(i);
                        email[0] = person.getString("email");
                       Toast.makeText(t, "kkkk ta certo?"+email[0], Toast.LENGTH_SHORT).show();

                    }
                }catch (JSONException e) {
                    Toast.makeText(t, "k"+response, Toast.LENGTH_SHORT).show();
                    Log.d("No", response);
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
                parameters.put("id", (id));
                return parameters;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(request);

        return email[0];
    }
    
    public MyLocation getLocationById(final long id, Context t){
        detalhes = new ProgressDialog(t);
        detalhes.setMessage("Deletando marker");
        detalhes.setCancelable(true);
        //detalhes.show();
        final MyLocation[] myLocation = new MyLocation[1];
        RequestQueue mRequestQueue = Volley.newRequestQueue(t);
        StringRequest request = new StringRequest(Request.Method.POST, urlJsonDetailsLocation, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject person = (JSONObject) jsonArray.get(i);
                        myLocation[0] = new MyLocation();
                        String id = person.getString("id");
                        String nome = person.getString("Nome");
                        String telefone = person.getString("Telefone");
                        String lat = person.getString("Lat");
                        String lng = person.getString("Lng");
                        String tipo = person.getString("Tipo");
                        String email = person.getString("Email");
                        myLocation[0].setId(Integer.parseInt(id));
                        myLocation[0].setTipo(tipo);
                        myLocation[0].setNome(nome);
                        myLocation[0].setEmail(email);
                        myLocation[0].setTelefone(telefone);
                        myLocation[0].setLat(Double.parseDouble(lat));
                        myLocation[0].setLng(Double.parseDouble(lng));
                    }
                  //  detalhes.dismiss();
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
                parameters.put("id", String.valueOf(id));
                return parameters;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(request);
        return myLocation[0];
    }

    public void insert(final MyLocation location, Context t) {
//        showpDialog();
        RequestQueue mRequestQueue = Volley.newRequestQueue(t);
        StringRequest request = new StringRequest(Request.Method.POST, urlJsonInsertLocation, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(Tag, response+"a");
            }
        },  new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(Tag, error+"r");
            }
        })

        {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String , String>();
                parameters.put("nome", location.getNome());
                parameters.put("lat", String.valueOf(location.getLat()));
                parameters.put("lng", String.valueOf(location.getLng()));
                parameters.put("tipo", String.valueOf(location.getTipo()));
                parameters.put("telefone", location.getTelefone());
                parameters.put("email", location.getEmail());
              //  hidepDialog();
                return parameters;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(request);
    }

    public void update(final LatLng position, final String id, final Context t){
        RequestQueue mRequestQueue = Volley.newRequestQueue(t);
        StringRequest request = new StringRequest(Request.Method.POST, urlJsonUpdateLocation, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                       Toast.makeText(t, "UPDATE"+response, Toast.LENGTH_SHORT).show();

            }
        },  new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(Tag, error+"r");
                                       Toast.makeText(t, "UPDATE ERRO"+error, Toast.LENGTH_SHORT).show();

            }
        })
        {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String , String>();
                parameters.put("id", id);
                parameters.put("lat", String.valueOf(position.latitude));

                parameters.put("lng", String.valueOf(position.longitude));
                return parameters;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(request);
    }


    public boolean delete(final String lat, final String lng, final Context applicationContext) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(applicationContext);
        StringRequest request = new StringRequest(Request.Method.POST, urlJsonDeleteLocation, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        },  new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(Tag, error+"r");
            }
        })
        {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String , String>();
                parameters.put("lat", lat);
                parameters.put("lng", lng);
                return parameters;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(request);
        return true;
    }
}
