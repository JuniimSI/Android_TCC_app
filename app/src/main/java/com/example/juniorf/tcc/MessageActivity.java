package com.example.juniorf.tcc;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import com.example.juniorf.tcc.DAO.MessageDAO;
import com.example.juniorf.tcc.MODEL.Mensagem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    private EditText message;
    private ListView listView;
    private Button btnSend;
    String emailOrigem;
    String emailDestino;
    private List<String> lista;
    private String jsonResponse;


    @Override
    protected void onStart() {
        super.onStart();
        /*MessageDAO mDAO = new MessageDAO(this);
        Mensagem n = new Mensagem();
        n.setEmailDestino("Email@Destino.com");
        List<Mensagem> l =(mDAO.show(n, getApplicationContext()));
        //Toast.makeText(this, "size"+l.size(), Toast.LENGTH_SHORT).show();
        if(l.size()>0){
            ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, l);
            listView.setAdapter(adapter);
        }else{
            Toast.makeText(this, "Sem Conexão", Toast.LENGTH_SHORT).show();
        }*/
    }

    public List<String> show(final Mensagem mensagem, final Context c){

        Log.i("NOME DO DESTINO", mensagem.getEmailDestino());
        Log.i("kkk", "antes do req");
        lista = new ArrayList<String>();

        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, "http://grainmapey.pe.hu/GranMapey/show_message.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("OOOOKKKK", response+"          a");
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject person = (JSONObject) jsonArray.get(i);

                        Mensagem mensagemz = new Mensagem();
                        String id = person.getString("id");
                        String emailOrigem = person.getString("emailOrigem");
                        String emailDestino = person.getString("emailDestino");
                        String message = person.getString("message");
                        mensagemz.setId(Integer.parseInt(id));
                        mensagemz.setEmailOrigem(emailOrigem);
                        mensagemz.setEmailDestino(emailDestino);
                        mensagemz.setTexto(message);
                        mensagemz.setLocal(message);
                        Log.i("MENSAGEMZ", mensagemz.getTexto());
                        lista.add(message);

                    }
                    showList();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        },  new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ERRO", error+"r");
            }
        })

        {
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> parameters = new HashMap<String , String>();
                parameters.put("emailDestino", mensagem.getEmailDestino());
                return parameters;
            }
        };
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(request);
        return this.lista;
    }


    private void showList() {
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.lista);
        listView.setAdapter(itemsAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MessageDAO mDAO = new MessageDAO(this);
        Mensagem n = new Mensagem();
        n.setEmailDestino("Email@Destino.com");
        show(n, this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //Dados da activity
        message = (EditText) findViewById(R.id.etMessage);
        listView = (ListView) findViewById(R.id.listView);
        btnSend = (Button) findViewById(R.id.btnSend);

        ///Do intent bundle da main
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        emailOrigem = bundle.getString("emailOrigem");
        emailDestino = bundle.getString("emailDestino");



        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Mensagem n = new Mensagem();
                String text = message.getText().toString();
                n.setTexto(text);
                n.setEmailOrigem(emailOrigem);
                n.setEmailDestino(emailDestino);
                MessageDAO messageDAO = new MessageDAO(getApplicationContext());
                messageDAO.insert(n, getApplicationContext());
            }
        });


    }
}
