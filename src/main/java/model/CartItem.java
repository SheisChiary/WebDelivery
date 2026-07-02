package model;

public class CartItem {
    private Prodotto prodotto;
    private int quantita;
    private String personalizzazioni;
    private double costoExtra;

    public CartItem(Prodotto prodotto, int quantita) {
        this.prodotto = prodotto;
        this.quantita = quantita;
        this.personalizzazioni = "";
        this.costoExtra = 0.0;
    }

    public Prodotto getProdotto() { return prodotto; }
    public void setProdotto(Prodotto prodotto) { this.prodotto = prodotto; }

    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }

    public String getPersonalizzazioni() { return personalizzazioni; }
    public void setPersonalizzazioni(String personalizzazioni) { this.personalizzazioni = personalizzazioni; }

    public double getCostoExtra() { return costoExtra; }
    public void setCostoExtra(double costoExtra) { this.costoExtra = costoExtra; }
}