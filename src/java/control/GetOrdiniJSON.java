package control;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DBConnect;
import model.OrdineAdminDashboard;

@WebServlet(name = "GetOrdiniJson", urlPatterns = {"/GetOrdiniJson"})
public class GetOrdiniJSON extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            DBConnect db = new DBConnect();
            List<OrdineAdminDashboard> ordini = db.getTuttiGliOrdiniConStorico();
            
            PrintWriter out = response.getWriter();
            StringBuilder json = new StringBuilder("[");
            
            for (int i = 0; i < ordini.size(); i++) {
                OrdineAdminDashboard o = ordini.get(i);
                json.append("{")
                    .append("\"id\":\"").append(o.getIdOrdine()).append("\",")
                    .append("\"cliente\":\"").append(o.getNomeCliente() != null ? o.getNomeCliente().replace("\"", "\\\"") : "").append("\",")
                    .append("\"data_richiesta\":\"").append(o.getDataRichiesta() != null ? o.getDataRichiesta() : "Data non disp.").append("\",")
                    // Inserimento della data di consegna reale per lo storico
                    .append("\"data_consegna\":\"").append(o.getDataConsegna() != null ? o.getDataConsegna() : "").append("\",")
                    .append("\"tempo_stimato\":").append(o.getTempoStimato()).append(",")
                    .append("\"totale\":\"").append(o.getTotale()).append("\",")
                    .append("\"stato\":\"").append(o.getStato() != null ? o.getStato() : "").append("\"")
                    .append("}");
                
                if (i < ordini.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            out.print(json.toString());
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}