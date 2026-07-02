package model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Ordine")
public class Ordine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ordine") 
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false) 
    private Utente utente;

    @Column(name = "data_creazione", nullable = false)
    private LocalDateTime dataCreazione;

    @Column(name = "orario_consegna_richiesto", nullable = false)
    private LocalDateTime orarioConsegnaRichiesto;

    @Column(name = "stato_attuale", nullable = false)
    private String stato; 

    @Column(name = "prezzo_totale", nullable = false)
    private Double prezzoTotale;

    @Column(name = "tempo_stimato_consegna", nullable = false)
    private int tempoStimatoConsegna;

    @OneToMany(mappedBy = "ordine", fetch = jakarta.persistence.FetchType.EAGER)
    private List<DettaglioOrdine> dettagli;

    
    public Ordine() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utente getUtente() { return utente; }
    public void setUtente(Utente utente) { this.utente = utente; }

    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }

    public LocalDateTime getOrarioConsegnaRichiesto() { return orarioConsegnaRichiesto; }
    public void setOrarioConsegnaRichiesto(LocalDateTime orarioConsegnaRichiesto) { this.orarioConsegnaRichiesto = orarioConsegnaRichiesto; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public Double getPrezzoTotale() { return prezzoTotale; }
    public void setPrezzoTotale(Double prezzoTotale) { this.prezzoTotale = prezzoTotale; }

    public int getTempoStimatoConsegna() { return tempoStimatoConsegna; }
    public void setTempoStimatoConsegna(int tempoStimatoConsegna) { this.tempoStimatoConsegna = tempoStimatoConsegna; }

    public List<DettaglioOrdine> getDettagli() { return dettagli;}
    public void setDettagli(List<DettaglioOrdine> dettagli) { this.dettagli = dettagli;}

    @jakarta.persistence.OneToMany(mappedBy = "ordine", fetch = jakarta.persistence.FetchType.EAGER)
    private java.util.Set<StoricoStatiOrdine> storicoStati;

    public java.util.Set<StoricoStatiOrdine> getStoricoStati() { return storicoStati; }
    public void setStoricoStati(java.util.Set<StoricoStatiOrdine> storicoStati) { this.storicoStati = storicoStati; }
}