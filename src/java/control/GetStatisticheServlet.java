package control;

import model.DBConnect;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "GetStatisticheServlet", urlPatterns = {"/GetStatisticheServlet"})
public class GetStatisticheServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String dataScelta = request.getParameter("data"); 
        if (dataScelta == null || dataScelta.trim().isEmpty()) {
            dataScelta = new java.sql.Date(System.currentTimeMillis()).toString();
        }

        double incassoGiorno = 0;
        double incassoMese = 0;
        int ordiniMese = 0;

        StringBuilder json = new StringBuilder("{");

        try (Connection conn = DBConnect.getConnection()) {
            
            // 1. Incasso Giornaliero
            String sqlGiorno = "SELECT SUM(prezzo_totale) FROM Ordine WHERE DATE(data_creazione) = ? AND stato_attuale = 'consegnato'";
            try (PreparedStatement ps = conn.prepareStatement(sqlGiorno)) {
                ps.setString(1, dataScelta);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) incassoGiorno = rs.getDouble(1);
                }
            }

            // 2 e 3. Incasso Mensile e Ordini
            String sqlMese = "SELECT SUM(prezzo_totale), COUNT(id_ordine) FROM Ordine WHERE YEAR(data_creazione) = YEAR(?) AND MONTH(data_creazione) = MONTH(?) AND stato_attuale = 'consegnato'";
            try (PreparedStatement ps = conn.prepareStatement(sqlMese)) {
                ps.setString(1, dataScelta);
                ps.setString(2, dataScelta);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        incassoMese = rs.getDouble(1);
                        ordiniMese = rs.getInt(2);
                    }
                }
            }

            json.append("\"incassoGiorno\":").append(incassoGiorno).append(",")
                .append("\"incassoMese\":").append(incassoMese).append(",")
                .append("\"ordiniMese\":").append(ordiniMese).append(",");

            // 4. Prodotti più venduti
            json.append("\"topProdotti\": [");
            String sqlTop = "SELECT p.nome, SUM(d.quantita) as qta FROM Dettaglio_Ordine d JOIN Ordine o ON d.id_ordine = o.id_ordine JOIN Prodotto p ON d.id_prodotto = p.id_prodotto WHERE o.stato_attuale = 'consegnato' AND YEAR(o.data_creazione) = YEAR(?) AND MONTH(o.data_creazione) = MONTH(?) GROUP BY p.id_prodotto, p.nome ORDER BY qta DESC LIMIT 5";
            try (PreparedStatement ps = conn.prepareStatement(sqlTop)) {
                ps.setString(1, dataScelta);
                ps.setString(2, dataScelta);
                try (ResultSet rs = ps.executeQuery()) {
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) json.append(",");
                        json.append("{\"nome\":\"").append(rs.getString("nome").replace("\"", "\\\"")).append("\", \"quantita\":").append(rs.getInt("qta")).append("}");
                        first = false;
                    }
                }
            }
            json.append("],");

            // 5. Prodotti MENO venduti 
            json.append("\"flopProdotti\": [");
            String sqlFlop = "SELECT p.nome, COALESCE(SUM(d.quantita), 0) as qta " +
                             "FROM Prodotto p " +
                             "LEFT JOIN Dettaglio_Ordine d ON p.id_prodotto = d.id_prodotto " +
                             "LEFT JOIN Ordine o ON d.id_ordine = o.id_ordine AND o.stato_attuale = 'consegnato' AND YEAR(o.data_creazione) = YEAR(?) AND MONTH(o.data_creazione) = MONTH(?) " +
                             "GROUP BY p.id_prodotto, p.nome " +
                             "ORDER BY qta ASC, p.nome ASC LIMIT 5";
            try (PreparedStatement ps = conn.prepareStatement(sqlFlop)) {
                ps.setString(1, dataScelta);
                ps.setString(2, dataScelta);
                try (ResultSet rs = ps.executeQuery()) {
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) json.append(",");
                        json.append("{\"nome\":\"").append(rs.getString("nome").replace("\"", "\\\"")).append("\", \"quantita\":").append(rs.getInt("qta")).append("}");
                        first = false;
                    }
                }
            }
            json.append("]");
            json.append("}");
            out.print(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Errore nel caricamento delle statistiche.\"}");
        }
    }
}