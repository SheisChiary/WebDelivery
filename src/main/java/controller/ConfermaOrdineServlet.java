package controller;

import dao.OrdineDAO;
import model.Utente;
import model.CartItem;
import util.EmailUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@WebServlet(name = "ConfermaOrdineServlet", urlPatterns = {"/conferma-ordine"})
public class ConfermaOrdineServlet extends HttpServlet {

    private OrdineDAO ordineDao;

    @Override
    public void init() throws ServletException {
        ordineDao = new OrdineDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Utente utenteLoggato = (session != null) ? (Utente) session.getAttribute("utente") : null;
        
        if (utenteLoggato == null) {
            response.sendRedirect("login");
            return;
        }

        List<CartItem> carrello = (List<CartItem>) session.getAttribute("carrello");
        if (carrello == null || carrello.isEmpty()) {
            response.sendRedirect("carrello");
            return;
        }

        String orarioStr = request.getParameter("orarioProgrammato");
        
        // Calcolo totale e tempi
        double subtotale = 0.0;
        int tempoPreparazioneBase = 0;
        for (CartItem item : carrello) {
            subtotale += (item.getProdotto().getPrezzo() + item.getCostoExtra()) * item.getQuantita();
            tempoPreparazioneBase += item.getProdotto().getTempoPreparazione() * item.getQuantita();
        }
        double totale = subtotale + 2.50; 
        int tempoStimato = tempoPreparazioneBase + 15;

        LocalDateTime orarioConsegna = LocalDateTime.now().plusMinutes(tempoStimato);
        if (orarioStr != null && !orarioStr.trim().isEmpty()) {
            LocalTime time = LocalTime.parse(orarioStr);
            orarioConsegna = LocalDateTime.now().with(time);
            if (orarioConsegna.isBefore(LocalDateTime.now())) {
                orarioConsegna = orarioConsegna.plusDays(1);
            }
        }

        try {
          
            Long idOrdine = ordineDao.salvaNuovoOrdine(utenteLoggato.getId(), orarioConsegna, totale, tempoStimato, carrello);
            
            
            EmailUtil.inviaEmailConfermaOrdine(
                utenteLoggato.getEmail(), 
                utenteLoggato.getNomeCompleto(), 
                idOrdine, 
                totale
            );

            session.removeAttribute("carrello");
            session.setAttribute("ordineCompletato", true);
            response.sendRedirect("carrello");

        } catch (Exception e) {
            throw new ServletException("Errore durante il salvataggio dell'ordine", e);
        }
    }
}