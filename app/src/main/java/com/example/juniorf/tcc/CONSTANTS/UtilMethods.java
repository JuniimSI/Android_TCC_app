package com.example.juniorf.tcc.CONSTANTS;

/**
 * Created by juniorf on 19/02/18.
 */
import android.widget.Toast;

import android.content.Context;



public class UtilMethods {

    public static void error(Context c){
        Toast.makeText(c, "Oops, uma falha aconteceu...  \n Tente novamente mais tarde", Toast.LENGTH_SHORT).show();

    }

}