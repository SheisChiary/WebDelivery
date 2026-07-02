package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "Storico_Stati_Ordine")
public class StoricoStatiOrdine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_storico")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_ordine", nullable = false)
    private Ordine ordine;

    @ManyToOne
    @JoinColumn(name = "id_personale", nullable = false)
    private Utente personale;

    @Column(name = "stato", nullable = false, length = 50)
    private String stato;

    @Column(name = "data_ora_modifica", nullable = false)
    private LocalDateTime dataOraModifica;

    public StoricoStatiOrdine() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Ordine getOrdine() { return ordine; }
    public void setOrdine(Ordine ordine) { this.ordine = ordine; }

    public Utente getPersonale() { return personale; }
    public void setPersonale(Utente personale) { this.personale = personale; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public LocalDateTime getDataOraModifica() { return dataOraModifica; }
    public void setDataOraModifica(LocalDateTime dataOraModifica) { this.dataOraModifica = dataOraModifica; }
}