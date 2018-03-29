package com.example.juniorf.tcc.DAO;

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


import java.util.HashMap;
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

    public List<Tipo> getTipos(final Context t){
        List<Tipos> lista = new ArrayList<Tipo>();
        
        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, "http://grainmapey.pe.hu/GranMapey/get_types.php", null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                try {
                    jsonResponse = "";
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject person = (JSONObject) response.get(i);

                        Tipo tipo = new Tipo();
                        String id = person.getString("id");
                        String emailOrigem = person.getString("emailOrigem");
                        String emailDestino = person.getString("emailDestino");
                        String message = person.getString("message");
                        tipo.setId(Integer.parseInt(id));
                        tipo.setEmailOrigem(emailOrigem);
                        tipo.setEmailDestino(emailDestino);
                        tipo.setTexto(message);
                        tipo.setLocal(message);
                        Log.i("MENSAGEMZ", mensagemz.getTexto());
                        lista.add(message);
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
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
        return this.lista;
    }

}