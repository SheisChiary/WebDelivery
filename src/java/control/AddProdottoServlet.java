package control;

import model.DBConnect;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "AddProdottoServlet", urlPatterns = {"/AddProdottoServlet"})
public class AddProdottoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Dati base del prodotto
        String nome = request.getParameter("nome");
        String categoria = request.getParameter("categoria");
        String prezzoStr = request.getParameter("prezzo");
        String badge = request.getParameter("badge");
        String descrizione = request.getParameter("descrizione");
        String immagine = request.getParameter("immagine");

        // 2. Dati delle varianti/ingredienti extra 
        String[] varNomi = request.getParameterValues("var_nome");
        String[] varPrezzi = request.getParameterValues("var_prezzo");

        double prezzo = 0;
        try {
            prezzo = Double.parseDouble(prezzoStr);
        } catch (Exception e) {
            
        }

        // Tempo di preparazione di default (15 min) e ricetta vuota
        String sqlProdotto = "INSERT INTO Prodotto (nome, categoria, prezzo_base, badge, descrizione, ingredienti, immagine_url, tempo_preparazione, ricetta) VALUES (?, ?, ?, ?, ?, ?, ?, 15, '')";
        
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlProdotto, Statement.RETURN_GENERATED_KEYS)) {
             
            ps.setString(1, nome);
            ps.setString(2, categoria);
            ps.setDouble(3, prezzo);
            ps.setString(4, (badge != null && !badge.trim().isEmpty()) ? badge : null);
            ps.setString(5, descrizione);
            ps.setString(6, descrizione); 
            ps.setString(7, (immagine != null && !immagine.trim().isEmpty()) ? immagine : null);
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                // id_prodotto
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idProdotto = generatedKeys.getInt(1);
                        
                        // 3. Inserimento delle varianti nel Database
                        if (varNomi != null && varPrezzi != null && varNomi.length == varPrezzi.length) {
                            String sqlVariante = "INSERT INTO Caratteristica (id_prodotto, id_gruppo, nome, differenza_prezzo, is_default) VALUES (?, NULL, ?, ?, FALSE)";
                            
                            try (PreparedStatement psVar = conn.prepareStatement(sqlVariante)) {
                                for (int i = 0; i < varNomi.length; i++) {
                                    if(varNomi[i] != null && !varNomi[i].trim().isEmpty()) {
                                        double pVar = 0;
                                        try { pVar = Double.parseDouble(varPrezzi[i]); } catch(Exception e){}
                                        
                                        psVar.setInt(1, idProdotto);
                                        psVar.setString(2, varNomi[i]);
                                        psVar.setDouble(3, pVar);
                                        psVar.executeUpdate(); // Salva l'ingrediente
                                    }
                                }
                            }
                        }
                    }
                }
                
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore salvataggio prodotto.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore Database: " + e.getMessage());
        }
    }
}