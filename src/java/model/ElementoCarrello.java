package model;

public class ElementoCarrello {
    private Prodotto prodotto;
    private int quantita;
    private String opzioni;
    private double prezzoExtra;

    // COSTRUTTORE
    public ElementoCarrello(Prodotto prodotto, int quantita, String opzioni, double prezzoExtra) {
        this.prodotto = prodotto;
        this.quantita = quantita;
        this.opzioni = opzioni;
        this.prezzoExtra = prezzoExtra;
    }

    public Prodotto getProdotto() { return prodotto; }
    public void setProdotto(Prodotto prodotto) { this.prodotto = prodotto; }

    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }
    
    public String getOpzioni() { return opzioni; }
    public void setOpzioni(String opzioni) { this.opzioni = opzioni; }

    public double getPrezzoExtra() { return prezzoExtra; }
    public void setPrezzoExtra(double prezzoExtra) { this.prezzoExtra = prezzoExtra; }

   
    public double getPrezzoUnitario() {
        return prodotto.getPrezzoBase() + prezzoExtra;
    }
}