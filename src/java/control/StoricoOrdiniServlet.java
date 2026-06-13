package control;

import model.DBConnect;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "StoricoOrdiniServlet", urlPatterns = {"/StoricoOrdiniServlet"})
public class StoricoOrdiniServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("idUtente") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int idCliente = (int) session.getAttribute("idUtente");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String sql = "SELECT id_ordine, data_creazione, stato_attuale, prezzo_totale FROM Ordine WHERE id_cliente = ? ORDER BY data_creazione DESC";
        
       
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder json = new StringBuilder("[");
                boolean first = true;
                while (rs.next()) {
                    if (!first) json.append(",");
                    
                    int id = rs.getInt("id_ordine");
                    String data = rs.getString("data_creazione");
                    String stato = rs.getString("stato_attuale");
                    double totale = rs.getDouble("prezzo_totale");
                    
                    json.append("{")
                        .append("\"id\":").append(id).append(",")
                        .append("\"data\":\"").append(data != null ? data : "").append("\",")
                        .append("\"stato\":\"").append(stato != null ? stato.replace("\"", "\\\"") : "").append("\",")
                        .append("\"totale\":").append(totale)
                        .append("}");
                    first = false;
                }
                json.append("]");
                response.getWriter().write(json.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500);
        }
    }
}