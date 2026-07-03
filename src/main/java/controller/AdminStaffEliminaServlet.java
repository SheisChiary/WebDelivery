package controller;

import dao.UtenteDAO;
import model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "AdminStaffEliminaServlet", urlPatterns = {"/admin/staff-elimina"})
public class AdminStaffEliminaServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Utente utenteLoggato = (Utente) session.getAttribute("utente");
        
        if (utenteLoggato == null || !utenteLoggato.getRuolo().equals("proprietario")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            Long idUtenteDaEliminare = Long.parseLong(idParam);
            
            try {
                UtenteDAO dao = new UtenteDAO();
                dao.eliminaStaff(idUtenteDaEliminare, utenteLoggato.getId());
            } catch (Exception e) {
                throw new ServletException("Errore durante l'eliminazione del membro dello staff", e);
            }
        }

        response.sendRedirect("staff");
    }
}