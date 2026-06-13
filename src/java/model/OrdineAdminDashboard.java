package model;

import java.util.ArrayList;
import java.util.List;

public class OrdineAdminDashboard {
    private int idOrdine;
    private String nomeCliente;
    private String dataRichiesta;
    private int tempoStimato; 
    private double totale;
    private String stato;
    private List<StoricoStato> cronologia;
    private String dataConsegna;

    
    public OrdineAdminDashboard(int idOrdine, String nomeCliente, String dataRichiesta, int tempoStimato, double totale, String stato) {
        this.idOrdine = idOrdine;
        this.nomeCliente = nomeCliente;
        this.dataRichiesta = dataRichiesta;
        this.tempoStimato = tempoStimato;
        this.totale = totale;
        this.stato = stato;
        this.cronologia = new ArrayList<>();
    }
    
    public String getDataConsegna() { return dataConsegna; }
    public void setDataConsegna(String data) { this.dataConsegna = data; }
    public int getIdOrdine() { return idOrdine; }
    public String getNomeCliente() { return nomeCliente; }
    public String getDataRichiesta() { return dataRichiesta; }
    public int getTempoStimato() { return tempoStimato; } 
    public double getTotale() { return totale; }
    public String getStato() { return stato; }
    public List<StoricoStato> getCronologia() { return cronologia; }
    public void addCronologia(StoricoStato s) { this.cronologia.add(s); }
}