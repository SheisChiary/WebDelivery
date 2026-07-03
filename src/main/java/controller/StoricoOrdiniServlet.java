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
import java.util.List;
import java.util.Map;
import util.JpaUtil;

@WebServlet(name = "StoricoOrdiniServlet", urlPatterns = {"/storico-ordini"})
public class StoricoOrdiniServlet extends HttpServlet {

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
            List<Object[]> ordini = em.createNativeQuery("SELECT id_ordine, data_creazione, stato_attuale, prezzo_totale, tempo_stimato_consegna FROM Ordine WHERE id_cliente = ? ORDER BY data_creazione DESC")
                    .setParameter(1, idUtente)
                    .getResultList();
            
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("ordini", ordini);
            
            Template template = cfg.getTemplate("storico_ordini.ftl");
            template.process(dataModel, response.getWriter());
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            em.close();
        }
    }
}