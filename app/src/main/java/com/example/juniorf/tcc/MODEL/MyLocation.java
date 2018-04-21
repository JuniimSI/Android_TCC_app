package com.example.juniorf.tcc.MODEL;

import android.content.Context;

/**
 * Created by juniorf on 17/12/16.
 */

public class MyLocation {
    private int id;
    private String nome;
    private String telefone;
    private double lat;
    private double lng;
    private String tipo;
    private String email;
    private String id_reference;
    private String detalhes;
    private String horario_funcionamento;

    public String getDetalhes(){
        return detalhes;
    }

    public void setDetalhes(String detalhes){
        this.detalhes = detalhes;
    }

    public String getHorarioFuncionamento(){
        if(horario_funcionamento!= null)
            return horario_funcionamento;
        else
            return "";
    }

    public void setHorarioFuncionamento(String horario){
        this.horario_funcionamento = horario;
    }

    public String getId_reference() {
        return id_reference;
    }

    public void setId_reference(String id_reference) {
        this.id_reference = id_reference;
    }

    public MyLocation() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MyLocation(double lat, double lng, String nome, String telefone, String tipo, String email) {
        this.email = email;
        this.lat = lat;
        this.lng = lng;
        this.nome = nome;
        this.telefone = telefone;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
