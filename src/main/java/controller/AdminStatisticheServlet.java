package controller;

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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminStatisticheServlet", urlPatterns = {"/admin/statistiche"})
public class AdminStatisticheServlet extends HttpServlet {

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

        
        String dataParam = request.getParameter("data_rif");
        LocalDate dataSelezionata = (dataParam != null && !dataParam.isEmpty()) ? 
                                    LocalDate.parse(dataParam) : LocalDate.now();

        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        
        try {

            Double incassoGiornaliero = em.createQuery(
                "SELECT SUM(o.prezzoTotale) FROM Ordine o WHERE o.stato = 'consegnato' AND o.dataCreazione >= :inizioGiorno AND o.dataCreazione < :fineGiorno", Double.class)
                .setParameter("inizioGiorno", dataSelezionata.atStartOfDay())
                .setParameter("fineGiorno", dataSelezionata.plusDays(1).atStartOfDay())
                .getSingleResult();

          
            Double incassoMensile = em.createQuery(
                "SELECT SUM(o.prezzoTotale) FROM Ordine o WHERE o.stato = 'consegnato' AND o.dataCreazione >= :inizioMese AND o.dataCreazione < :fineMese", Double.class)
                .setParameter("inizioMese", dataSelezionata.withDayOfMonth(1).atStartOfDay())
                .setParameter("fineMese", dataSelezionata.plusMonths(1).withDayOfMonth(1).atStartOfDay())
                .getSingleResult();


            List<Object[]> topProdotti = em.createQuery(
                "SELECT d.prodotto.nome, SUM(d.quantita) as totale FROM DettaglioOrdine d JOIN d.ordine o WHERE o.stato = 'consegnato' GROUP BY d.prodotto.nome ORDER BY totale DESC", Object[].class)
                .setMaxResults(5)
                .getResultList();


            List<Object[]> flopProdotti = em.createQuery(
                "SELECT d.prodotto.nome, SUM(d.quantita) as totale FROM DettaglioOrdine d JOIN d.ordine o WHERE o.stato = 'consegnato' GROUP BY d.prodotto.nome ORDER BY totale ASC", Object[].class)
                .setMaxResults(5)
                .getResultList();

            Double scontrinoMedio = em.createQuery("SELECT AVG(o.prezzoTotale) FROM Ordine o WHERE o.stato = 'consegnato'", Double.class).getSingleResult();
            Long totaleOrdini = em.createQuery("SELECT COUNT(o) FROM Ordine o", Long.class).getSingleResult();
            Long ordiniAnnullati = em.createQuery("SELECT COUNT(o) FROM Ordine o WHERE o.stato = 'annullato'", Long.class).getSingleResult();

            incassoGiornaliero = (incassoGiornaliero != null) ? incassoGiornaliero : 0.0;
            incassoMensile = (incassoMensile != null) ? incassoMensile : 0.0;
            scontrinoMedio = (scontrinoMedio != null) ? scontrinoMedio : 0.0;
            
            double tassoAnnullamento = (totaleOrdini > 0) ? ((double) ordiniAnnullati / totaleOrdini) * 100 : 0.0;

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("utenteLoggato", utenteLoggato);
            templateData.put("dataSelezionata", dataSelezionata.toString()); // Formato YYYY-MM-DD per l'input HTML
            templateData.put("incassoGiornaliero", incassoGiornaliero);
            templateData.put("incassoMensile", incassoMensile);
            templateData.put("topProdotti", topProdotti);
            templateData.put("flopProdotti", flopProdotti);
            templateData.put("scontrinoMedio", scontrinoMedio);
            templateData.put("tassoAnnullamento", tassoAnnullamento);

            Template template = cfg.getTemplate("admin_statistiche.ftl");
            template.process(templateData, response.getWriter());

        } catch (Exception e) {
            throw new ServletException("Errore nel calcolo delle statistiche", e);
        } finally {
            em.close();
        }
    }
}