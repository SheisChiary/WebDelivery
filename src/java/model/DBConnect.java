package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBConnect {
    private static final String URL = "jdbc:mysql://localhost:3306/webdelivery?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "pass123"; 

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL non trovato!", e);
        }
    }

    public List<OrdineAdminDashboard> getTuttiGliOrdiniConStorico() throws SQLException {
        List<OrdineAdminDashboard> lista = new ArrayList<>();
        
        try (Connection con = getConnection()) {
            
            // 1. Prendo tutti gli ordini
            String queryOrdini = "SELECT o.id_ordine, u.nome_completo, o.orario_consegna_richiesto, o.tempo_stimato_consegna, o.prezzo_totale, o.stato_attuale " +
                                 "FROM Ordine o JOIN Utente u ON o.id_cliente = u.id_utente ORDER BY o.data_creazione DESC";
            
            try (PreparedStatement stmt = con.prepareStatement(queryOrdini);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    int id = rs.getInt("id_ordine");
                    
                    // Passiamo al costruttore i nuovi dati letti dal DB
                    OrdineAdminDashboard ordine = new OrdineAdminDashboard(
                        id, 
                        rs.getString("nome_completo"), 
                        rs.getString("orario_consegna_richiesto"), 
                        rs.getInt("tempo_stimato_consegna"),      
                        rs.getDouble("prezzo_totale"), 
                        rs.getString("stato_attuale")
                    );
                    
                    // 2. Per ogni ordine, cerco la sua storia
                    String queryStoria = "SELECT s.stato, s.data_ora_modifica, u.nome_completo " +
                                         "FROM Storico_Stati_Ordine s " +
                                         "JOIN Utente u ON s.id_personale = u.id_utente " +
                                         "WHERE s.id_ordine = ? ORDER BY s.data_ora_modifica ASC";
                    
                    try (PreparedStatement stmtStoria = con.prepareStatement(queryStoria)) {
                        stmtStoria.setInt(1, id);
                        ResultSet rsStoria = stmtStoria.executeQuery();
                        while (rsStoria.next()) {
                            
                            // Estraiamo i dati nelle variabili per poterli controllare
                            String statoStorico = rsStoria.getString("stato");
                            String dataModifica = rsStoria.getString("data_ora_modifica");
                            
                            StoricoStato storico = new StoricoStato(
                                statoStorico,
                                dataModifica,
                                rsStoria.getString("nome_completo")
                            );
                            ordine.addCronologia(storico);
                       
                            if ("consegnato".equalsIgnoreCase(statoStorico)) {
                                ordine.setDataConsegna(dataModifica);
                            }
                        }
                    }
                    lista.add(ordine);
                }
            }
        }
        return lista;
    }
}