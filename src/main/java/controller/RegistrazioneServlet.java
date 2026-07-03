package controller;

import dao.UtenteDAO;
import model.Utente;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "RegistrazioneServlet", urlPatterns = {"/registrazione"})
public class RegistrazioneServlet extends HttpServlet {

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
        Map<String, Object> dataModel = new HashMap<>();
        
        if (request.getParameter("error") != null) {
            dataModel.put("errore", "Errore durante la registrazione. Email forse già in uso?");
        }

        try {
            Template template = cfg.getTemplate("registrazione.ftl");
            template.process(dataModel, response.getWriter());
        } catch (TemplateException e) {
            throw new ServletException("Errore nel caricamento del template", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        Utente nuovoUtente = new Utente();
        nuovoUtente.setNomeCompleto(request.getParameter("nomeCompleto"));
        nuovoUtente.setEmail(request.getParameter("email"));
        nuovoUtente.setPassword(request.getParameter("password"));
        nuovoUtente.setTelefono(request.getParameter("telefono"));
        nuovoUtente.setIndirizzo(request.getParameter("indirizzo"));
        nuovoUtente.setRuolo("cliente");
        
        try {
            UtenteDAO dao = new UtenteDAO();
            dao.salvaUtente(nuovoUtente);
            response.sendRedirect("login");
        } catch (Exception e) {
            response.sendRedirect("registrazione?error=1");
        }
    }
}