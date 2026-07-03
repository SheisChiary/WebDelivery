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
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminOrdiniServlet", urlPatterns = {"/admin/ordini"})
public class AdminOrdiniServlet extends HttpServlet {

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

        if (utenteLoggato == null || !utenteLoggato.getRuolo().equals("proprietario")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            dao.OrdineDAO ordineDao = new dao.OrdineDAO();
            List<Ordine> ordiniInCorso = ordineDao.getOrdiniInCorso();
            List<Ordine> ordiniPassati = ordineDao.getStoricoOrdini();

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("ordiniInCorso", ordiniInCorso);
            templateData.put("ordiniPassati", ordiniPassati);
            templateData.put("utenteLoggato", utenteLoggato);

            Template template = cfg.getTemplate("admin_ordini.ftl");
            template.process(templateData, response.getWriter());
            
            

        } catch (Exception e) {
            throw new ServletException("Errore nel caricamento degli ordini", e);
        }
    }
}