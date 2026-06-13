package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdineDAO {

    public List<Map<String, Object>> getOrdiniCliente(int idCliente) {
        List<Map<String, Object>> ordini = new ArrayList<>();
        
        String sql = "SELECT id_ordine, data_creazione, stato_attuale, prezzo_totale FROM Ordine WHERE id_cliente = ? ORDER BY data_creazione DESC";
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setInt(1, idCliente);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> ordine = new HashMap<>();
                    ordine.put("id", rs.getInt("id_ordine"));
                    ordine.put("data", rs.getString("data_creazione"));
                    ordine.put("stato", rs.getString("stato_attuale"));
                    ordine.put("totale", rs.getDouble("prezzo_totale"));
                    ordini.add(ordine);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ordini;
    }
}