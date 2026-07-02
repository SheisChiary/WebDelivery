package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "utenti")
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String ruolo;

    @Column(name = "nome_completo",nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cognome;
    
    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private String indirizzo;

    
    public Utente() {
    }

   
    public Long getId() { return id;}
    public void setId(Long id) { this.id = id;}

    public String getEmail() { return email;}
    public void setEmail(String email) { this.email = email;}

    public String getPassword() { return password;}
    public void setPassword(String password) { this.password = password;}

    public String getRuolo() { return ruolo;}
    public void setRuolo(String ruolo) { this.ruolo = ruolo;}

    public String getNome() { return nome;}
    public void setNome(String nome) { this.nome = nome;}

    public String getTelefono() { return telefono;}
    public void setTelefono(String telefono) { this.telefono = telefono;}
    
    public String getIndirizzo() { return indirizzo;}
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo;}
    
    public String getCognome() { return cognome;}
    public void setCognome(String indirizzo) { this.cognome = cognome;}



}