package model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Dettaglio_Ordine")
public class DettaglioOrdine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dettaglio")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_ordine", nullable = false)
    private Ordine ordine;

    @ManyToOne
    @JoinColumn(name = "id_prodotto", nullable = false)
    private Prodotto prodotto;

    @Column(name = "quantita", nullable = false)
    private int quantita;

    public DettaglioOrdine() {
    }


    public Long getId() { return id;}
    public void setId(Long id) { this.id = id;}

    public Ordine getOrdine() { return ordine;}
    public void setOrdine(Ordine ordine) { this.ordine = ordine;}

    public Prodotto getProdotto() { return prodotto;}
    public void setProdotto(Prodotto prodotto) { this.prodotto = prodotto;}

    public int getQuantita() { return quantita;}
    public void setQuantita(int quantita) { this.quantita = quantita;}
}