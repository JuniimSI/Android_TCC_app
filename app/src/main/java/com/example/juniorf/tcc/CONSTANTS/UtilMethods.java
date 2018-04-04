package com.example.juniorf.tcc.CONSTANTS;

/**
 * Created by juniorf on 19/02/18.
 */
import android.content.DialogInterface;
import android.content.Context;
import android.support.v7.app.AlertDialog;


public class UtilMethods {

    public void error(Context c){
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