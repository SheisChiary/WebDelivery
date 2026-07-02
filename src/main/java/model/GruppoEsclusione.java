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
@Table(name = "Gruppo_Esclusione")
public class GruppoEsclusione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_gruppo")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_prodotto")
    private Prodotto prodotto;

    @Column(name = "nome_gruppo", nullable = false)
    private String nomeGruppo;

    public GruppoEsclusione() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Prodotto getProdotto() { return prodotto; }
    public void setProdotto(Prodotto prodotto) { this.prodotto = prodotto; }

    public String getNomeGruppo() { return nomeGruppo; }
    public void setNomeGruppo(String nomeGruppo) { this.nomeGruppo = nomeGruppo; }
}