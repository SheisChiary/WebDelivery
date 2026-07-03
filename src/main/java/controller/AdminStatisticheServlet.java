package controller;

import dao.StatisticheDAO;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminStatisticheServlet", urlPatterns = {"/admin/statistiche"})
public class AdminStatisticheServlet extends HttpServlet {

    private Configuration cfg;
    private StatisticheDAO statDao;

    @Override
    public void init() throws ServletException {
        cfg = new Configuration(Configuration.VERSION_2_3_33);
        freemarker.ext.jakarta.servlet.WebappTemplateLoader templateLoader = 
            new freemarker.ext.jakarta.servlet.WebappTemplateLoader(getServletContext(), "/WEB-INF/templates");
        cfg.setTemplateLoader(templateLoader);
        cfg.setDefaultEncoding("UTF-8");
        
        statDao = new StatisticheDAO();
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

        try {
            Double incassoGiornaliero = statDao.getIncassoGiornaliero(dataSelezionata);
            Double incassoMensile = statDao.getIncassoMensile(dataSelezionata);
            List<Object[]> topProdotti = statDao.getTopProdotti();
            List<Object[]> flopProdotti = statDao.getFlopProdotti();
            Double scontrinoMedio = statDao.getScontrinoMedio();
            Long totaleOrdini = statDao.getTotaleOrdini();
            Long ordiniAnnullati = statDao.getOrdiniAnnullati();

            incassoGiornaliero = (incassoGiornaliero != null) ? incassoGiornaliero : 0.0;
            incassoMensile = (incassoMensile != null) ? incassoMensile : 0.0;
            scontrinoMedio = (scontrinoMedio != null) ? scontrinoMedio : 0.0;
            
            double tassoAnnullamento = (totaleOrdini > 0) ? ((double) ordiniAnnullati / totaleOrdini) * 100 : 0.0;

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("utenteLoggato", utenteLoggato);
            templateData.put("dataSelezionata", dataSelezionata.toString()); 
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
        }
    }
}