package com.example.juniorf.tcc.DAO;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by juniorf on 25/11/16.
 */

public abstract class AbstractDAO<T> {
    private Context context;

    public AbstractDAO(Context context){

    }

    public AbstractDAO() {

    }

    public Context getContext(){
        return this.context;
    }

    public void setContext(Context context){
        this.context = context;
    }

}
