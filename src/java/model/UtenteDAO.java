package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtenteDAO {

    // Recupera i dati dell'utente
    public ResultSet getDatiUtente(int id) throws SQLException {
        Connection conn = DBConnect.getConnection();
        String sql = "SELECT nome_completo, email, telefono, indirizzo FROM Utente WHERE id_utente = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        return ps.executeQuery();
    }

    // Aggiorna i dati
    public boolean aggiornaProfilo(int id, String nome, String telefono, String indirizzo) {
        String query = "UPDATE Utente SET nome_completo = ?, telefono = ?, indirizzo = ? WHERE id_utente = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nome);
            ps.setString(2, telefono);
            ps.setString(3, indirizzo);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}