package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Prodotto")
public class Prodotto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prodotto")
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String descrizione;

    @Column(name = "prezzo_base",nullable = false)
    private Double prezzo;

    @Column(name = "tempo_preparazione")
    private int tempopreparazione;
    
    @Column(name = "ingredienti")
    private String ingredienti;
        
    @Column(name = "ricetta")
    private String ricetta;
    
    @Column(name = "categoria")
    private String categoria;
        
    @Column(name = "immagine_url")
    private String immagine;
    
    @Column(name = "badge")
    private String badge;
   
    public Prodotto() {
    }

  

    public Long getId() { return id;}
    public void setId(Long id) { this.id = id;}

    public String getNome() { return nome;}
    public void setNome(String nome) { this.nome = nome;}

    public String getDescrizione() { return descrizione;}
    public void setDescrizione(String descrizione) { this.descrizione = descrizione;}

    public Double getPrezzo() { return prezzo;}
    public void setPrezzo(Double prezzo) { this.prezzo = prezzo;}
    
    public int getTempoPreparazione() { return tempopreparazione;}
    public void setTempoPreparazione(int tempopreparazione) { this.tempopreparazione = tempopreparazione;}
    
    public String getIngredienti() { return ingredienti;}
    public void setIngredienti(String ingredienti) { this.ingredienti = ingredienti;}
    
    public String getRicetta() { return ricetta;}
    public void setRicetta(String ricetta) { this.ricetta = ricetta;}
    
    public String getCategoria() { return categoria;}
    public void setCategoria(String categoria) { this.categoria = categoria;}
    
    public void setImmagine(String immagine) { this.immagine = immagine;}
    public String getImmagine() { return immagine; }
    
    public String getBadge() { return badge;}
    public void setBadge(String badge) { this.badge = badge;}
}