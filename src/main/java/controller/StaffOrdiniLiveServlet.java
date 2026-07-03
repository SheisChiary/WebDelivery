package controller;

import dao.OrdineDAO;
import model.Ordine;
import model.Utente;
import freemarker.template.Configuration;
import freemarker.template.Template;
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

@WebServlet(name = "StaffOrdiniLiveServlet", urlPatterns = {"/staff/ordini-live"})
public class StaffOrdiniLiveServlet extends HttpServlet {

    private Configuration cfg;
    private OrdineDAO ordineDao;

    @Override
    public void init() throws ServletException {
        cfg = new Configuration(Configuration.VERSION_2_3_33);
        freemarker.ext.jakarta.servlet.WebappTemplateLoader templateLoader = 
            new freemarker.ext.jakarta.servlet.WebappTemplateLoader(getServletContext(), "/WEB-INF/templates");
        cfg.setTemplateLoader(templateLoader);
        cfg.setDefaultEncoding("UTF-8");
        
        ordineDao= new OrdineDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        Utente utenteLoggato = (Utente) session.getAttribute("utente");

        if (utenteLoggato == null || !utenteLoggato.getRuolo().equals("personale")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        
        try {
           
List<Ordine> ordiniLive = ordineDao.getOrdiniInCorso();

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("ordiniLive", ordiniLive);
            templateData.put("utenteLoggato", utenteLoggato);

            Template template = cfg.getTemplate("staff_ordini_live.ftl");
            template.process(templateData, response.getWriter());

        } catch (Exception e) {
            throw new ServletException("Errore nel caricamento degli ordini live", e);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Utente utenteLoggato = (Utente) session.getAttribute("utente");

        Long idOrdine = Long.parseLong(request.getParameter("id_ordine"));
        String nuovoStato = request.getParameter("nuovo_stato");
        
        try {
        ordineDao.aggiornaStatoOrdine(idOrdine, nuovoStato, utenteLoggato);
        } catch (Exception e) {
            throw new ServletException("Errore nell'aggiornamento dello stato", e);
        }

        response.sendRedirect("ordini-live");
    }
}