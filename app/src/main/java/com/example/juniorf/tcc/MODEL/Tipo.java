package com.example.juniorf.tcc.MODEL;

/**
 * Created by juniorf on 23/12/16.
 */

public class Tipo {
	private Integer id;
    private String tipo;
    private String type;


    public Tipo(){

    }

    public Integer getId(){
    	return this.id;
    }

    public String getTipo(){
        return this.tipo;
    }

    public String getType(){
        return this.type;
    }

    public void setId(Integer id){
    	this.id = id;
    }

    public void setTipo(String tipo){
        this.tipo = tipo;
    }

    public void setType(String type){
        this.type = type;
    }

}
