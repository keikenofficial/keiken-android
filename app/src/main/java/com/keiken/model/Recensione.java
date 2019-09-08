package com.keiken.model;

public class Recensione {
    private String ID_CREATORE;
    private String ID_ESPERIENZA_RECENSITA; //Se non più disponibile sostituire
    private String ID_UTENTE_RECENSITO;
    private int voto; //DA 1 A 5
    private String commento;

    public Recensione(String ID_CREATORE, String ID_ESPERIENZA_RECENSITA, String ID_UTENTE_RECENSITO, int voto, String commento) {
        this.ID_CREATORE = ID_CREATORE;
        this.ID_ESPERIENZA_RECENSITA = ID_ESPERIENZA_RECENSITA;
        this.ID_UTENTE_RECENSITO = ID_UTENTE_RECENSITO;
        this.voto = voto;
        this.commento = commento;
    }

    public String getID_CREATORE() {
        return ID_CREATORE;
    }

    public String getID_ESPERIENZA_RECENSITA() {
        return ID_ESPERIENZA_RECENSITA;
    }

    public String getID_UTENTE_RECENSITO() {
        return ID_UTENTE_RECENSITO;
    }

    public int getVoto() {
        return voto;
    }

    public String getCommento() {
        return commento;
    }

}
