package model;

public class Prodotto {
    private int idProdotto;
    private String nome;
    private String descrizione;
    private double prezzoBase;
    private String categoria;
    private String immagineUrl;
    private String badge;


    public Prodotto() {}

   
    public int getIdProdotto() { return idProdotto; }
    public void setIdProdotto(int idProdotto) { this.idProdotto = idProdotto; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public double getPrezzoBase() { return prezzoBase; }
    public void setPrezzoBase(double prezzoBase) { this.prezzoBase = prezzoBase; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getImmagineUrl() { return immagineUrl; }
    public void setImmagineUrl(String immagineUrl) { this.immagineUrl = immagineUrl; }

    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }
}