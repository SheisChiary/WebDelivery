package controller;

import model.Ordine;
import model.StoricoStatiOrdine;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "StaffOrdiniLiveServlet", urlPatterns = {"/staff/ordini-live"})
public class StaffOrdiniLiveServlet extends HttpServlet {

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

        if (utenteLoggato == null || !utenteLoggato.getRuolo().equals("personale")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        
        try {
           
            List<Ordine> ordiniLive = em.createQuery(
                "SELECT o FROM Ordine o WHERE o.stato NOT IN ('consegnato', 'annullato') ORDER BY o.dataCreazione ASC", 
                Ordine.class).getResultList();

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("ordiniLive", ordiniLive);
            templateData.put("utenteLoggato", utenteLoggato);

            Template template = cfg.getTemplate("staff_ordini_live.ftl");
            template.process(templateData, response.getWriter());

        } catch (Exception e) {
            throw new ServletException("Errore nel caricamento degli ordini live", e);
        } finally {
            em.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Utente utenteLoggato = (Utente) session.getAttribute("utente");

        Long idOrdine = Long.parseLong(request.getParameter("id_ordine"));
        String nuovoStato = request.getParameter("nuovo_stato");

        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        
        try {
            em.getTransaction().begin();
            
            Ordine ordine = em.find(Ordine.class, idOrdine);
            
            if (ordine != null && isTransizioneValida(ordine.getStato(), nuovoStato)) {
                
                ordine.setStato(nuovoStato);
                
                StoricoStatiOrdine storico = new StoricoStatiOrdine();
                storico.setOrdine(ordine);
                storico.setPersonale(utenteLoggato); 
                storico.setStato(nuovoStato);
                storico.setDataOraModifica(LocalDateTime.now());
                
                em.persist(storico); 
            }
            
            em.getTransaction().commit();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new ServletException("Errore nell'aggiornamento dello stato", e);
        } finally {
            em.close();
        }

        response.sendRedirect("ordini-live");
    }

    private boolean isTransizioneValida(String statoAttuale, String nuovoStato) {
        if (statoAttuale.equalsIgnoreCase("inserito") && nuovoStato.equalsIgnoreCase("in preparazione")) return true;
        if (statoAttuale.equalsIgnoreCase("in preparazione") && nuovoStato.equalsIgnoreCase("pronto")) return true;
        if (statoAttuale.equalsIgnoreCase("pronto") && nuovoStato.equalsIgnoreCase("in consegna")) return true;
        if (statoAttuale.equalsIgnoreCase("in consegna") && nuovoStato.equalsIgnoreCase("consegnato")) return true;
        return false;
    }
}