package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProdottoDAO {

    public List<Prodotto> getAllProdotti() {
        List<Prodotto> menu = new ArrayList<>();
        String query = "SELECT id_prodotto, nome, descrizione, prezzo_base, tempo_preparazione, ingredienti, ricetta, categoria, immagine_url, badge FROM Prodotto";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Prodotto p = new Prodotto();
                p.setIdProdotto(rs.getInt("id_prodotto"));
                p.setNome(rs.getString("nome"));
                p.setDescrizione(rs.getString("descrizione"));
                p.setPrezzoBase(rs.getDouble("prezzo_base"));
                p.setTempoPreparazione(rs.getInt("tempo_preparazione"));
                p.setIngredienti(rs.getString("ingredienti"));
                p.setRicetta(rs.getString("ricetta"));
                
                p.setCategoria(rs.getString("categoria"));
                p.setImmagineUrl(rs.getString("immagine_url"));
                p.setBadge(rs.getString("badge"));
                
                menu.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return menu;
    }
    
    public Prodotto getProdottoById(int id) {
        String query = "SELECT id_prodotto, nome, descrizione, prezzo_base, tempo_preparazione, ingredienti, ricetta, categoria, immagine_url, badge FROM Prodotto WHERE id_prodotto = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Prodotto p = new Prodotto();
                    p.setIdProdotto(rs.getInt("id_prodotto"));
                    p.setNome(rs.getString("nome"));
                    p.setDescrizione(rs.getString("descrizione"));
                    p.setPrezzoBase(rs.getDouble("prezzo_base"));
                    p.setTempoPreparazione(rs.getInt("tempo_preparazione"));
                    p.setIngredienti(rs.getString("ingredienti"));
                    p.setRicetta(rs.getString("ricetta"));
                    
                    p.setCategoria(rs.getString("categoria"));
                    p.setImmagineUrl(rs.getString("immagine_url"));
                    p.setBadge(rs.getString("badge"));
                    return p;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    

    public boolean addProdotto(Prodotto p) {
        String query = "INSERT INTO Prodotto (nome, descrizione, prezzo_base, tempo_preparazione, ingredienti, ricetta, categoria, immagine_url, badge) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescrizione());
            ps.setDouble(3, p.getPrezzoBase());

            ps.setInt(4, p.getTempoPreparazione());
            ps.setString(5, p.getIngredienti());
            ps.setString(6, p.getRicetta());
            
            ps.setString(7, p.getCategoria());
            ps.setString(8, p.getImmagineUrl());
            ps.setString(9, p.getBadge());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean updateProdotto(Prodotto p) {
        String query = "UPDATE Prodotto SET nome=?, descrizione=?, prezzo_base=?, tempo_preparazione=?, ingredienti=?, ricetta=?, categoria=?, immagine_url=?, badge=? WHERE id_prodotto=?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescrizione());
            ps.setDouble(3, p.getPrezzoBase());
            
       
            ps.setInt(4, p.getTempoPreparazione());
            ps.setString(5, p.getIngredienti());
            ps.setString(6, p.getRicetta());
            
            ps.setString(7, p.getCategoria());
            ps.setString(8, p.getImmagineUrl());
            ps.setString(9, p.getBadge());
            ps.setInt(10, p.getIdProdotto()); 
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Elimina un prodotto
    public boolean deleteProdotto(int id) {
        String query = "DELETE FROM Prodotto WHERE id_prodotto = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}