package com.example.juniorf.tcc.DAO;

import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.juniorf.tcc.MODEL.Tipo;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.juniorf.tcc.CONFIG.AppController;
import com.example.juniorf.tcc.CONSTANTS.UtilMethods;
import com.example.juniorf.tcc.CONSTANTS.Codes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TipoDAO extends  AbstractDAO<Tipo> {
	private String urlInsert = "http://grainmapey.pe.hu/GranMapey/insert_tipo.php";
    private String Tag = "TagDoDAO";

	public TipoDAO(Context context) {
        super(context);
    }

    public void insert(final Tipo tipo, final Context t) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(t);
        StringRequest request = new StringRequest(Request.Method.POST, urlInsert, new Response.Listener<String>() {
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
                parameters.put("tipo", tipo.getTipo());
                
                return parameters;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(request);
    }

    public List<String> getTipos(final Context t){
        final List<String> lista = new ArrayList<String>();
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, "http://grainmapey.pe.hu/GranMapey/show_type.php", new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject person = (JSONObject) response.get(i);
                        String tipo = person.getString("tipo");
                        lista.add(tipo);
                    }
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
        });

        AppController.getInstance().addToRequestQueue(req);
        return lista;
    }

}