package controller;

import dao.ProdottoDAO;
import model.Prodotto;
import model.Utente;
import model.Caratteristica;
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

@WebServlet(name = "AdminMenuModificaServlet", urlPatterns = {"/admin/menu-modifica"})
public class AdminMenuModificaServlet extends HttpServlet {

    private Configuration cfg;
    private ProdottoDAO prodottoDao;

    @Override
    public void init() throws ServletException {
        cfg = new Configuration(Configuration.VERSION_2_3_33);
        freemarker.ext.jakarta.servlet.WebappTemplateLoader templateLoader = 
            new freemarker.ext.jakarta.servlet.WebappTemplateLoader(getServletContext(), "/WEB-INF/templates");
        cfg.setTemplateLoader(templateLoader);
        cfg.setDefaultEncoding("UTF-8");
        
        prodottoDao = new ProdottoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Utente utenteLoggato = (Utente) session.getAttribute("utente");
        if (utenteLoggato == null || !utenteLoggato.getRuolo().equals("proprietario")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect("menu");
            return;
        }

        try {
            Long idProdotto = Long.parseLong(idParam);
            Prodotto prodotto = prodottoDao.getProdottoById(idProdotto);
            List<Caratteristica> caratteristiche = prodottoDao.getCaratteristicheByProdotto(idProdotto);
            
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("prodotto", prodotto);
            templateData.put("caratteristiche", caratteristiche);
            templateData.put("utenteLoggato", utenteLoggato);
            
            Template template = cfg.getTemplate("admin_menu_modifica.ftl");
            template.process(templateData, response.getWriter());
        } catch (Exception e) {
            throw new ServletException("Errore", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String subAction = request.getParameter("sub_action");
        Long idProdotto = Long.parseLong(request.getParameter("id_prodotto"));

        try {
            if ("update_prodotto".equals(subAction)) {
                prodottoDao.aggiornaProdotto(
                    idProdotto,
                    request.getParameter("nome"),
                    Double.parseDouble(request.getParameter("prezzo")),
                    request.getParameter("descrizione"),
                    request.getParameter("categoria"),
                    request.getParameter("immagine"),
                    Integer.parseInt(request.getParameter("tempo_preparazione")),
                    request.getParameter("badge")
                );
            } else if ("add_caratteristica".equals(subAction)) {
                String idGruppoStr = request.getParameter("id_gruppo");
                Long idGruppo = (idGruppoStr != null && !idGruppoStr.isEmpty()) ? Long.parseLong(idGruppoStr) : null;
                
                prodottoDao.aggiungiCaratteristica(
                    idProdotto,
                    request.getParameter("caratt_nome"),
                    Double.parseDouble(request.getParameter("caratt_prezzo")),
                    request.getParameter("caratt_default") != null,
                    idGruppo
                );
            } else if ("delete_caratteristica".equals(subAction)) {
                Long idCaratt = Long.parseLong(request.getParameter("id_caratteristica"));
                prodottoDao.eliminaCaratteristica(idCaratt);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }

        response.sendRedirect("menu-modifica?id=" + idProdotto);
    }
}