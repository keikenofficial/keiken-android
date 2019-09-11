package com.keiken.model;

import java.util.Calendar;
import java.util.HashMap;

public class Prenotazione {
    private String ID_PRENOTAZIONE, ID_CREATORE_ESPERIENZA, ID_PRENOTANTE, ID_ESPERIENZA;
    private Calendar dataPrenotata;
    private int nPostiPrenotati;
    private int ore;
    private int minuti;
    private float prezzo;
    private boolean isAccepted; // true = confermata
                                    //false = pending
                                        //se rifiutata, viene eliminata la prenotazione.

    public Prenotazione(String ID_PRENOTAZIONE, String ID_CREATORE_ESPERIENZA, String ID_PRENOTANTE, String ID_ESPERIENZA, Calendar dataPrenotata, int nPostiPrenotati, int ore, int minuti, float prezzo, boolean isAccepted) {
        this.ID_PRENOTAZIONE = ID_PRENOTAZIONE;
        this.ID_CREATORE_ESPERIENZA = ID_CREATORE_ESPERIENZA;
        this.ID_PRENOTANTE = ID_PRENOTANTE;
        this.ID_ESPERIENZA = ID_ESPERIENZA;
        this.dataPrenotata = dataPrenotata;
        this.nPostiPrenotati = nPostiPrenotati;
        this.ore = ore;
        this.minuti = minuti;
        this.prezzo = prezzo;
        this.isAccepted = isAccepted;
    }

    public boolean getIsAccepted() { return  isAccepted; }

    public String getID_PRENOTAZIONE() {
        return ID_PRENOTAZIONE;
    }

    public String getID_CREATORE_ESPERIENZA() {
        return ID_CREATORE_ESPERIENZA;
    }

    public String getID_PRENOTANTE() {
        return ID_PRENOTANTE;
    }

    public String getID_ESPERIENZA() {
        return ID_ESPERIENZA;
    }

    public Calendar getDataPrenotata() {
        return dataPrenotata;
    }

    public int getnPostiPrenotati() {
        return nPostiPrenotati;
    }

    public int getOre() {
        return ore;
    }

    public int getMinuti() {
        return minuti;
    }

    public float getPrezzo() {
        return prezzo;
    }

}
