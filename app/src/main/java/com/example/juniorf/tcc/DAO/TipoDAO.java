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

}