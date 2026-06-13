package control;

import model.DBConnect;
import model.UtenteDAO;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "ProfiloServlet", urlPatterns = {"/ProfiloServlet"})
public class ProfiloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("idUtente") == null) {
            response.sendError(401);
            return;
        }
        
        int id = (int) session.getAttribute("idUtente");
        
        // Chiudiamo tutte le connessioni in modo sicuro
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT nome_completo, email, telefono, indirizzo FROM Utente WHERE id_utente = ?")) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    
                    String nome = rs.getString("nome_completo") != null ? rs.getString("nome_completo").replace("\"", "\\\"") : "";
                    String email = rs.getString("email") != null ? rs.getString("email").replace("\"", "\\\"") : "";
                    String telefono = rs.getString("telefono") != null ? rs.getString("telefono").replace("\"", "\\\"") : "";
                    String indirizzo = rs.getString("indirizzo") != null ? rs.getString("indirizzo").replace("\"", "\\\"").replace("\n", " ").replace("\r", "") : "";
                    
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().print("{\"nome\":\"" + nome + "\", \"email\":\"" + email + "\", \"telefono\":\"" + telefono + "\", \"indirizzo\":\"" + indirizzo + "\"}");
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
            response.sendError(500);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("idUtente") == null) { 
            response.sendRedirect("login.html"); 
            return; 
        }
        
        int id = (int) session.getAttribute("idUtente");
        String nome = request.getParameter("nome");
        String telefono = request.getParameter("telefono");
        String indirizzo = request.getParameter("indirizzo");

        UtenteDAO dao = new UtenteDAO();
        if (dao.aggiornaProfilo(id, nome, telefono, indirizzo)) {
            session.setAttribute("nomeUtente", nome);
            session.setAttribute("nome_completo", nome);
            response.sendRedirect("dashboard.html");
        } else {
            response.getWriter().print("Errore aggiornamento");
        }
    }
}