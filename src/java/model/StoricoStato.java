package model;

public class StoricoStato {
    private String stato;
    private String dataOra;
    private String nomePersonale;

    public StoricoStato(String stato, String dataOra, String nomePersonale) {
        this.stato = stato;
        this.dataOra = dataOra;
        this.nomePersonale = nomePersonale;
    }

    public String getStato() { return stato; }
    public String getDataOra() { return dataOra; }
    public String getNomePersonale() { return nomePersonale; }
}