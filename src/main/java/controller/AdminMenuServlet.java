package controller;

import dao.ProdottoDAO;
import model.Prodotto;
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

@WebServlet(name = "AdminMenuServlet", urlPatterns = {"/admin/menu"})
public class AdminMenuServlet extends HttpServlet {

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
        
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        Utente utenteLoggato = (Utente) session.getAttribute("utente");
        
        if (utenteLoggato == null || !utenteLoggato.getRuolo().equals("proprietario")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            List<Prodotto> menuList = prodottoDao.getAllProdotti();
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("menuList", menuList);
            templateData.put("utenteLoggato", utenteLoggato);

            Template template = cfg.getTemplate("admin_menu.ftl");
            template.process(templateData, response.getWriter());
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Prodotto p = new Prodotto();
        p.setNome(request.getParameter("nome"));
        p.setPrezzo(Double.parseDouble(request.getParameter("prezzo")));
        p.setDescrizione(request.getParameter("descrizione"));
        p.setCategoria(request.getParameter("categoria"));
        p.setImmagine(request.getParameter("immagine"));
        p.setTempoPreparazione(Integer.parseInt(request.getParameter("tempo_preparazione")));
        p.setBadge(request.getParameter("badge"));

        try {
            prodottoDao.salva(p);
        } catch (Exception e) {
            throw new ServletException(e);
        }

        response.sendRedirect("menu");
    }
}