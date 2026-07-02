package controller;

import model.Ordine;
import model.Utente;
import util.JpaUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "AdminDettaglioOrdineServlet", urlPatterns = {"/admin/dettaglio-ordine"})
public class AdminDettaglioOrdineServlet extends HttpServlet {

    private Configuration cfg;

    @Override
    public void init() throws ServletException {
        cfg = new Configuration(Configuration.VERSION_2_3_33);
        freemarker.ext.jakarta.servlet.WebappTemplateLoader templateLoader = 
            new freemarker.ext.jakarta.servlet.WebappTemplateLoader(getServletContext(), "/WEB-INF/templates");
        cfg.setTemplateLoader(templateLoader);
        cfg.setDefaultEncoding("UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        Utente utenteLoggato = (Utente) session.getAttribute("utente");

        if (utenteLoggato == null || utenteLoggato.getRuolo().equals("cliente") ||utenteLoggato.getRuolo().equals("staff")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idOrdineParam = request.getParameter("id");
        if (idOrdineParam == null || idOrdineParam.isEmpty()) {
            response.sendRedirect("ordini");
            return;
        }

        Long idOrdine = Long.parseLong(idOrdineParam);
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        
        try {
            Ordine ordine = em.createQuery("SELECT o FROM Ordine o WHERE o.id = :id", Ordine.class)
                .setParameter("id", idOrdine)
                .getSingleResult();

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("ordine", ordine);
            templateData.put("utenteLoggato", utenteLoggato);

            Template template = cfg.getTemplate("admin_dettaglio_ordine.ftl");
            template.process(templateData, response.getWriter());

        } catch (Exception e) {
            throw new ServletException("Errore nel recupero dell'ordine", e);
        } finally {
            em.close();
        }
    }
}