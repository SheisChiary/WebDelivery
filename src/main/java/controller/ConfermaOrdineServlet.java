package controller;

import dao.OrdineDAO;
import model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import model.CartItem;

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

        Long idCliente = utenteLoggato.getId();
        String orarioStr = request.getParameter("orarioProgrammato");

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
            Long idOrdine = ordineDao.salvaNuovoOrdine(idCliente, orarioConsegna, totale, tempoStimato, carrello);

            System.out.println("\n=======================================================");
            System.out.println(" NUOVA EMAIL INVIATA AL CLIENTE");
            System.out.println("=======================================================");
            System.out.println("Oggetto: Conferma Ordine #" + idOrdine + " - WebDelivery");
            System.out.println("Ciao " + utenteLoggato.getNomeCompleto() + ",");
            System.out.println("Il tuo ordine di €" + String.format("%.2f", totale) + " è stato ricevuto con successo.");
            System.out.println("Orario di consegna previsto: " + orarioConsegna.format(DateTimeFormatter.ofPattern("HH:mm")));
            System.out.println("Grazie per aver scelto WebDelivery!");
            System.out.println("=======================================================\n");

            session.removeAttribute("carrello");
            session.setAttribute("ordineCompletato", true);
            response.sendRedirect("carrello");

        } catch (Exception e) {
            throw new ServletException("Errore durante il salvataggio dell'ordine", e);
        }
    }
}