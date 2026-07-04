package controller;

import dao.ProdottoDAO;
import model.Prodotto;
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

@WebServlet(name = "MenuServlet", urlPatterns = {"/menu"})
public class MenuServlet extends HttpServlet {
    
    private Configuration cfg;
    private ProdottoDAO prodottoDao;

    @Override
    public void init() throws ServletException {
        cfg = new Configuration(Configuration.VERSION_2_3_33);
        cfg.setServletContextForTemplateLoading(getServletContext(), "/WEB-INF/templates");
        cfg.setDefaultEncoding("UTF-8");
        prodottoDao = new ProdottoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");

        String searchParam = request.getParameter("search");
        String categoriaParam = request.getParameter("categoria");
        
        if (categoriaParam == null || categoriaParam.isEmpty()) {
            categoriaParam = "Tutti";
        }

        try {
            List<Prodotto> catalogo = prodottoDao.getProdottiFiltrati(searchParam, categoriaParam);

            HttpSession session = request.getSession(false); 
            String nomeUtente = "";
            
            if (session != null && session.getAttribute("utente") != null) {
                model.Utente u = (model.Utente) session.getAttribute("utente");
                nomeUtente = u.getNomeCompleto(); 
            }

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("prodotti", catalogo);
            templateData.put("nomeUtente", nomeUtente); 
            templateData.put("searchParam", searchParam);
            templateData.put("categoriaParam", categoriaParam);
            
            
            templateData.put("customizzazioni", new HashMap<String, Object>());

            Template template = cfg.getTemplate("menu.ftl");
            template.process(templateData, response.getWriter());

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}