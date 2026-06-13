package control;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DBConnect;

@WebServlet(name = "GetDettaglioOrdineJson", urlPatterns = {"/GetDettaglioOrdineJson"})
public class GetDettaglioOrdineJson extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (idStr == null || idStr.isEmpty()) {
            out.print("{}");
            return;
        }

        int idOrdine = Integer.parseInt(idStr);

        try (Connection conn = DBConnect.getConnection()) {
            StringBuilder json = new StringBuilder("{");

            // 1. Recupero il Prezzo Totale
            try (PreparedStatement ps = conn.prepareStatement("SELECT prezzo_totale FROM Ordine WHERE id_ordine = ?")) {
                ps.setInt(1, idOrdine);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        json.append("\"totale\": \"").append(rs.getDouble("prezzo_totale")).append("\",");
                    }
                }
            }

            // 2. Recupero i Prodotti ordinati 
            json.append("\"prodotti\": [");
            String queryProd = "SELECT d.id_dettaglio, p.nome, p.prezzo_base, p.ricetta, p.ingredienti, d.quantita " +
                               "FROM Dettaglio_Ordine d JOIN Prodotto p ON d.id_prodotto = p.id_prodotto " +
                               "WHERE d.id_ordine = ?";
            try (PreparedStatement ps = conn.prepareStatement(queryProd)) {
                ps.setInt(1, idOrdine);
                try (ResultSet rs = ps.executeQuery()) {
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) json.append(",");
                        json.append("{");
                        json.append("\"nome\":\"").append(rs.getString("nome").replace("\"", "\\\"")).append("\",");
                        json.append("\"quantita\":").append(rs.getInt("quantita")).append(",");
                        json.append("\"prezzo\":").append(rs.getDouble("prezzo_base")).append(",");
                        
                       
                        String ricetta = rs.getString("ricetta");
                        String ingredienti = rs.getString("ingredienti");
                        json.append("\"ricetta\":\"").append(ricetta != null ? ricetta.replace("\"", "\\\"").replace("\n", " ") : "Nessuna nota").append("\",");
                        json.append("\"ingredienti\":\"").append(ingredienti != null ? ingredienti.replace("\"", "\\\"").replace("\n", " ") : "Non specificati").append("\",");
                        
                        // Personalizzazioni scelte dal cliente
                        json.append("\"opzioni\": [");
                        String queryOpt = "SELECT c.nome FROM Dettaglio_Ordine_Caratteristiche doc " +
                                          "JOIN Caratteristica c ON doc.id_caratteristica = c.id_caratteristica " +
                                          "WHERE doc.id_dettaglio = ?";
                        try(PreparedStatement psOpt = conn.prepareStatement(queryOpt)) {
                            psOpt.setInt(1, rs.getInt("id_dettaglio"));
                            try(ResultSet rsOpt = psOpt.executeQuery()) {
                                boolean firstOpt = true;
                                while(rsOpt.next()) {
                                    if(!firstOpt) json.append(",");
                                    json.append("{\"nome\":\"").append(rsOpt.getString("nome").replace("\"", "\\\"")).append("\"}");
                                    firstOpt = false;
                                }
                            }
                        }
                        json.append("]");
                        json.append("}");
                        first = false;
                    }
                }
            }
            json.append("],");

            // 3. Recupero lo Storico di chi ha gestito l'ordine
            json.append("\"storico\": [");
            String queryHist = "SELECT s.stato, DATE_FORMAT(s.data_ora_modifica, '%d/%m %H:%i') as data_formattata, u.nome_completo " +
                               "FROM Storico_Stati_Ordine s JOIN Utente u ON s.id_personale = u.id_utente " +
                               "WHERE s.id_ordine = ? ORDER BY s.data_ora_modifica ASC";
            try (PreparedStatement ps = conn.prepareStatement(queryHist)) {
                ps.setInt(1, idOrdine);
                try (ResultSet rs = ps.executeQuery()) {
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) json.append(",");
                        json.append("{\"stato\":\"").append(rs.getString("stato")).append("\",");
                        json.append("\"data\":\"").append(rs.getString("data_formattata")).append("\",");
                        json.append("\"personale\":\"").append(rs.getString("nome_completo")).append("\"}");
                        first = false;
                    }
                }
            }
            json.append("]");
            json.append("}");
            out.print(json.toString());
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}