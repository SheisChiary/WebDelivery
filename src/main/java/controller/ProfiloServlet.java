package controller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import model.Utente;
import util.JpaUtil;

@WebServlet(name = "ProfiloServlet", urlPatterns = {"/profilo"})
public class ProfiloServlet extends HttpServlet {

    private Configuration cfg;

    @Override
    public void init() throws ServletException {
        cfg = new Configuration(Configuration.VERSION_2_3_33);
        cfg.setServletContextForTemplateLoading(getServletContext(), "/WEB-INF/templates");
        cfg.setDefaultEncoding("UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente_id") == null) {
            response.sendRedirect("login");
            return;
        }

        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            Long idUtente = (Long) session.getAttribute("utente_id");
            Utente utente = em.find(Utente.class, idUtente);
            
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("utente", utente);
            if (request.getParameter("success") != null) {
                dataModel.put("successMessage", "Profilo aggiornato con successo!");
            }
            
            Template template = cfg.getTemplate("profilo.ftl");
            template.process(dataModel, response.getWriter());
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            em.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente_id") == null) {
            response.sendRedirect("login");
            return;
        }

        String nomeCompleto = request.getParameter("nomeCompleto");
        String telefono = request.getParameter("telefono");
        String indirizzo = request.getParameter("indirizzo");

        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Long idUtente = (Long) session.getAttribute("utente_id");
            Utente utente = em.find(Utente.class, idUtente);
            
            utente.setNomeCompleto(nomeCompleto);
            utente.setTelefono(telefono);
            utente.setIndirizzo(indirizzo);
            
            em.getTransaction().commit();
            session.setAttribute("utente_nome", nomeCompleto);
            response.sendRedirect("profilo?success=1");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new ServletException(e);
        } finally {
            em.close();
        }
    }
}