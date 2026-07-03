package controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
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
import util.JpaUtil;

@WebServlet(name = "ConfermaOrdineServlet", urlPatterns = {"/conferma-ordine"})
public class ConfermaOrdineServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente_id") == null) {
            response.sendRedirect("login");
            return;
        }

        List<CartItem> carrello = (List<CartItem>) session.getAttribute("carrello");
        if (carrello == null || carrello.isEmpty()) {
            response.sendRedirect("carrello");
            return;
        }

        Long idCliente = (Long) session.getAttribute("utente_id");
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

        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            Query q = em.createNativeQuery("INSERT INTO Ordine (id_cliente, orario_consegna_richiesto, stato_attuale, prezzo_totale, tempo_stimato_consegna) VALUES (?, ?, 'inserito', ?, ?)");
            q.setParameter(1, idCliente);
            q.setParameter(2, orarioConsegna);
            q.setParameter(3, totale);
            q.setParameter(4, tempoStimato);
            q.executeUpdate();

            Number idOrdine = (Number) em.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult();

            for (CartItem item : carrello) {
                Query qDet = em.createNativeQuery("INSERT INTO Dettaglio_Ordine (id_ordine, id_prodotto, quantita, prezzo_unitario) VALUES (?, ?, ?, ?)");
                qDet.setParameter(1, idOrdine.longValue());
                qDet.setParameter(2, item.getProdotto().getId());
                qDet.setParameter(3, item.getQuantita());
                qDet.setParameter(4, item.getProdotto().getPrezzo() + item.getCostoExtra());
                qDet.executeUpdate();

                Number idDettaglio = (Number) em.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult();

                for (Long idCaratt : item.getCaratteristicheScelte()) {
                    Query qCar = em.createNativeQuery("INSERT INTO Dettaglio_Ordine_Caratteristiche (id_dettaglio, id_caratteristica) VALUES (?, ?)");
                    qCar.setParameter(1, idDettaglio.longValue());
                    qCar.setParameter(2, idCaratt);
                    qCar.executeUpdate();
                }
            }
            em.getTransaction().commit();

            System.out.println("\n=======================================================");
            System.out.println("📧 NUOVA EMAIL INVIATA AL CLIENTE");
            System.out.println("=======================================================");
            System.out.println("Oggetto: Conferma Ordine #" + idOrdine.longValue() + " - WebDelivery");
            System.out.println("Ciao " + session.getAttribute("utente_nome") + ",");
            System.out.println("Il tuo ordine di €" + String.format("%.2f", totale) + " è stato ricevuto con successo.");
            System.out.println("Orario di consegna previsto: " + orarioConsegna.format(DateTimeFormatter.ofPattern("HH:mm")));
            System.out.println("Grazie per aver scelto WebDelivery!");
            System.out.println("=======================================================\n");

            session.removeAttribute("carrello");
            session.setAttribute("ordineCompletato", true);
            response.sendRedirect("carrello");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new ServletException(e);
        } finally {
            em.close();
        }
    }
}