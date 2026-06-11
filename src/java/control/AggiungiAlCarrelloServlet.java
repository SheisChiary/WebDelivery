package control;

import model.Prodotto;
import model.ProdottoDAO;
import model.ElementoCarrello;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AggiungiAlCarrelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        int idPiatto = Integer.parseInt(request.getParameter("id"));
        int quantita = request.getParameter("qty") != null ? Integer.parseInt(request.getParameter("qty")) : 1;
        String opzioni = request.getParameter("opzioni") != null ? request.getParameter("opzioni") : "";
        double prezzoExtra = request.getParameter("prezzoExtra") != null ? Double.parseDouble(request.getParameter("prezzoExtra")) : 0.0;
        
        HttpSession session = request.getSession();
        List<ElementoCarrello> carrello = (List<ElementoCarrello>) session.getAttribute("carrello");
        if (carrello == null) carrello = new ArrayList<>();
        
       
        String delIdxParam = request.getParameter("deleteIndex");
        if (delIdxParam != null && !delIdxParam.isEmpty()) {
            int delIdx = Integer.parseInt(delIdxParam);
            if (delIdx >= 0 && delIdx < carrello.size()) {
                carrello.remove(delIdx);
            }
        }
        
        boolean giaPresente = false;
        for (ElementoCarrello elemento : carrello) {
            if (elemento.getProdotto().getIdProdotto() == idPiatto && elemento.getOpzioni().equals(opzioni)) {
                elemento.setQuantita(elemento.getQuantita() + quantita);
                giaPresente = true;
                break;
            }
        }
        
        if (!giaPresente) {
            ProdottoDAO dao = new ProdottoDAO();
            Prodotto p = dao.getProdottoById(idPiatto);
            if (p != null) {
                carrello.add(new ElementoCarrello(p, quantita, opzioni, prezzoExtra));
            }
        }
        
        session.setAttribute("carrello", carrello);
        out.print("{\"successo\": true, \"messaggio\": \"Piatto personalizzato aggiunto!\"}");
    }
}