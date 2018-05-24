package com.example.juniorf.tcc;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.content.Context;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.location.LocationManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.juniorf.tcc.LOGIN.LogInActivity;
import com.example.juniorf.tcc.MAPS.MapsActivity;
import com.example.juniorf.tcc.TUTORIAL.TutorialActivity;
import com.example.juniorf.tcc.CONSTANTS.UtilMethods;
import com.example.juniorf.tcc.CONSTANTS.Codes;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Places;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_PERMISSIONS_CODE = 0;
    /////////////////////////////////////////////////
    private GoogleApiClient client;
    private ProgressDialog pDialog;
    private ProgressDialog lDialog;
    private List<String> lista;
    private GoogleApiClient googleApiClient;
    private String email;
    private ImageView ivInitMapButton;
    private MaterialDialog mMaterialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        solicitaPermissao();

        ////DADOS activity

        ivInitMapButton = (ImageView) findViewById(R.id.ivInitMapButton);
        ivInitMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMap(v);
            }
        });

        //////////////////LOGIN OGOGLE////////////////////////////////

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //////////////////////////////////////////////////////////////

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);


        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API).build();
        lista = new ArrayList<String>();


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

	lDialog = new ProgressDialog(this);
	lDialog.setMessage("Aguarde...");
	lDialog.setCancelable(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        //noinspection SimplifiableIfStatement
        if(id == R.id.action_tutorial){
            Intent i = new Intent(this, TutorialActivity.class);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_instrucoes) {
           Intent i = new Intent(this, TutorialActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_mapa) {

            if(verificaGPS()){
                Intent i = new Intent(this, MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("emailOrigem", (this.email));
                i.putExtras(bundle);
                startActivity(i);
            }else{
                Toast.makeText(MainActivity.this, "Ative o GPS para utilizar o Mapa", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_sair) {
            finish();
        } else if(id == R.id.nav_login){
            goLoginScreen();
        } else if(id ==R.id.nav_logout){
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout(){
	lDialog.show();
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if(status.isSuccess()){
                    Toast.makeText(MainActivity.this, "Seção encerrada", Toast.LENGTH_SHORT).show();
                    email = null;
		    lDialog.dismiss();
                }else{
		    lDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Tente Novamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initMap(View view) {
        if(verificaGPS()){
            Intent i = new Intent(this, MapsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("emailOrigem", (this.email));
            i.putExtras(bundle);
            startActivity(i);
        }else{
            Toast.makeText(MainActivity.this, "Ative o GPS para utilizar o Mapa", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean verificaGPS(){
        ///////////////////Permission//////////////////////
        solicitaPermissao();

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if(opr.isDone()){
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        }else{
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess()){

            GoogleSignInAccount account = result.getSignInAccount();
            if(account.getDisplayName()!=null)
                Toast.makeText(this, "Olá, "+account.getDisplayName(), Toast.LENGTH_SHORT).show();
            email = (account.getEmail());
            
        }else{
        }
    }

    private void goLoginScreen() {
        Intent intent = new Intent (this, LogInActivity.class);

        startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void solicitaPermissao(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
              if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, ACCESS_FINE_LOCATION)) {
                   // Caso o usuário tenha negado a permissão anteriormente, e não tenha marcado o check "nunca mais mostre este alerta"
                   // Podemos mostrar um alerta explicando para o usuário porque a permissão é importante.
                   callDialog( new String[]{Manifest.permission.ACCESS_FINE_LOCATION} );
              } else {
                  // Solicita a permissão
                  ActivityCompat.requestPermissions(MainActivity.this,new String[]{ACCESS_FINE_LOCATION},REQUEST_PERMISSIONS_CODE);
              }
        } else {
              // Tudo OK, podemos prosseguir.
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch( requestCode ){
            case REQUEST_PERMISSIONS_CODE:
                for( int i = 0; i < permissions.length; i++ ){
                    if( permissions[i].equalsIgnoreCase( ACCESS_FINE_LOCATION )
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED ){
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void callDialog( final String[] permissions ){
        ActivityCompat.requestPermissions(MainActivity.this, permissions, REQUEST_PERMISSIONS_CODE);
    }
}
