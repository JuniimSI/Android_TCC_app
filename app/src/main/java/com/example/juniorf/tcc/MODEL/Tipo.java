package com.example.juniorf.tcc.MODEL;

/**
 * Created by juniorf on 23/12/16.
 */

public class Tipo {
	private Integer id;
    private String tipo;

    public Tipo(){

    }

    public Integer getId(){
    	return this.id;
    }

    public String getTipo(){
    	return this.tipo;
    }

    public void setId(Integer id){
    	this.id = id;
    }

    public void setTipo(String tipo){
    	this.tipo = tipo;
    }

}
