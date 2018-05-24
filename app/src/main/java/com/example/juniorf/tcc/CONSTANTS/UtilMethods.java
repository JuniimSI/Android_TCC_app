package com.example.juniorf.tcc.CONSTANTS;

/**
 * Created by juniorf on 19/02/18.
 */
import android.widget.Toast;
import android.app.ProgressDialog;

import android.content.DialogInterface;

import android.support.v7.app.AlertDialog;

import android.support.v7.app.AppCompatActivity;

import android.content.Context;



public class UtilMethods {

    public static void error(Context c){
        Toast.makeText(c, "Oops, uma falha aconteceu...  \n Tente novamente mais tarde", Toast.LENGTH_SHORT).show();

    }

    public static void errorDialog(Context c){
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