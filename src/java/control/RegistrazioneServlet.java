package control;

import model.DBConnect;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RegistrazioneServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String ruolo = request.getParameter("ruolo");
        String telefono = request.getParameter("telefono");
        String indirizzo = request.getParameter("indirizzo");

        String sql = "INSERT INTO Utente (nome_completo, email, password, telefono, indirizzo, ruolo) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nome);
            ps.setString(2, email);
            ps.setString(3, password); 
            ps.setString(4, telefono);
            ps.setString(5, indirizzo);
            ps.setString(6, ruolo);
            
            int righeInserite = ps.executeUpdate();
            
            out.println("<html><head><meta charset='UTF-8'></head><body>");
            out.println("<script>");
            if (righeInserite > 0) {
                out.println("alert('Registrazione completata con successo! \\nOra puoi fare l\\'accesso.');");
                out.println("window.location.href='login.html';");
            } else {
                out.println("alert('Errore: Impossibile registrare l\\'utente.');");
                out.println("window.history.back();");
            }
            out.println("</script>");
            out.println("</body></html>");

        } catch (SQLException e) {
            out.println("<html><head><meta charset='UTF-8'></head><body>");
            out.println("<script>");
            out.println("alert('Errore Database: Controlla che l\\'email non sia già registrata! (" + e.getMessage() + ")');");
            out.println("window.history.back();");
            out.println("</script>");
            out.println("</body></html>");
        }
    }
}