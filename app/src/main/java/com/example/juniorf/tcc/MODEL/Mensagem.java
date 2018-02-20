package com.example.juniorf.tcc.MODEL;

/**
 * Created by juniorf on 24/05/17.
 */

public class Mensagem {

    private Integer id;
    private String texto;
    private String emailOrigem;
    private String emailDestino;
    private String local;
    private boolean answer;

    public Mensagem(int i, String s, String s1, String s2) {
        this.id=i;
        this.texto=s2;
        this.emailDestino=s1;
        this.emailOrigem=s;
    }

    public boolean isAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

    public Mensagem() {
    }

    public Mensagem(Integer id, String texto, String emailOrigem, String emailDestino, String local) {
        this.id = id;
        this.texto = texto;
        this.emailOrigem = emailOrigem;
        this.emailDestino = emailDestino;
        this.local = local;
    }

    public Integer getId() { return id;}
    public void setId(Integer id) { this.id = id;     }
    public String getTexto() {
        return texto;
    }
    public void setTexto(String texto) {
        this.texto = texto;
    }
    public String getEmailOrigem() { return emailOrigem; }
    public void setEmailOrigem(String emailOrigem) { this.emailOrigem = emailOrigem; }
    public String getEmailDestino() { return emailDestino; }
    public void setEmailDestino(String emailDestino) { this.emailDestino = emailDestino; }
    public String getLocal() {
        return local;
    }
    public void setLocal(String local) {
        this.local = local;
    }

    @Override
    public String toString() {
        return "Mensagem{" +
                "id=" + id +
                ", texto='" + texto + '\'' +
                ", emailOrigem='" + emailOrigem + '\'' +
                ", emailDestino='" + emailDestino + '\'' +
                ", local='" + local + '\'' +
                ", answer=" + answer +
                '}';
    }
}
