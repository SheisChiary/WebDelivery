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
@Table(name = "Caratteristica")
public class Caratteristica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_caratteristica")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_prodotto")
    private Prodotto prodotto;

    @ManyToOne
    @JoinColumn(name = "id_gruppo")
    private GruppoEsclusione gruppo;

    @Column(nullable = false)
    private String nome;

    @Column
    private String descrizione;

    @Column(name = "differenza_prezzo")
    private Double differenzaPrezzo;

    @Column(name = "is_default")
    private Boolean isDefault;

    public Caratteristica() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Prodotto getProdotto() { return prodotto; }
    public void setProdotto(Prodotto prodotto) { this.prodotto = prodotto; }

    public GruppoEsclusione getGruppo() { return gruppo; }
    public void setGruppo(GruppoEsclusione gruppo) { this.gruppo = gruppo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public Double getDifferenzaPrezzo() { return differenzaPrezzo == null ? 0.0 : differenzaPrezzo; }
    public void setDifferenzaPrezzo(Double differenzaPrezzo) { this.differenzaPrezzo = differenzaPrezzo; }

    public Boolean getIsDefault() { return isDefault != null && isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
}