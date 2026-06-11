package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProdottoDAO {

    public List<Prodotto> getAllProdotti() {
        List<Prodotto> menu = new ArrayList<>();
        // Prende tutti i piatti dal database
        String query = "SELECT id_prodotto, nome, descrizione, prezzo_base, categoria, immagine_url, badge FROM Prodotto";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Prodotto p = new Prodotto();
                p.setIdProdotto(rs.getInt("id_prodotto"));
                p.setNome(rs.getString("nome"));
                p.setDescrizione(rs.getString("descrizione"));
                p.setPrezzoBase(rs.getDouble("prezzo_base"));
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
    String query = "SELECT id_prodotto, nome, descrizione, prezzo_base, categoria, immagine_url, badge FROM Prodotto WHERE id_prodotto = ?";
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
}