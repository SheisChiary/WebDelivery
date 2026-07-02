package controller;

import model.Utente;
import util.JpaUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.persistence.EntityManager;
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
            Long idUtente = Long.parseLong(idParam);
            EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
            
            try {
                em.getTransaction().begin();
                Utente utenteDaEliminare = em.find(Utente.class, idUtente);
                
                if (utenteDaEliminare != null && !utenteDaEliminare.getId().equals(utenteLoggato.getId())) {
                    em.remove(utenteDaEliminare);
                }
                
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                throw new ServletException("Errore durante l'eliminazione del membro dello staff", e);
            } finally {
                em.close();
            }
        }

        response.sendRedirect("staff");
    }
}