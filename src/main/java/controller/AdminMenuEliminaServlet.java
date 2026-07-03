package controller;

import model.Utente;
import model.Prodotto;
import util.JpaUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.persistence.EntityManager;
import java.io.IOException;

@WebServlet(name = "AdminMenuEliminaServlet", urlPatterns = {"/admin/menu-elimina"})
public class AdminMenuEliminaServlet extends HttpServlet {

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
            Long idProdotto = Long.parseLong(idParam);
            EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
            
            try {
                em.getTransaction().begin();
                Prodotto p = em.find(Prodotto.class, idProdotto);
                if (p != null) {
                    em.remove(p);
                }
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new ServletException(e);
            } finally {
                em.close();
            }
        }

        response.sendRedirect("menu");
    }
}