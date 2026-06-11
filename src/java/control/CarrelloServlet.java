package control;

import model.ElementoCarrello;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "CarrelloServlet", urlPatterns = {"/CarrelloServlet"})
public class CarrelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        
        List<ElementoCarrello> carrello = (List<ElementoCarrello>) session.getAttribute("carrello");
        StringBuilder json = new StringBuilder();
        json.append("[");
        
        if (carrello != null && !carrello.isEmpty()) {
            for (int i = 0; i < carrello.size(); i++) {
                ElementoCarrello el = carrello.get(i);
                json.append("{");
                json.append("\"id\":").append(el.getProdotto().getIdProdotto()).append(",");
                json.append("\"nome\":\"").append(el.getProdotto().getNome().replace("\"", "\\\"")).append("\",");
                json.append("\"descrizione\":\"").append(el.getProdotto().getDescrizione().replace("\"", "\\\"")).append("\",");
                json.append("\"prezzo\":").append(el.getPrezzoUnitario()).append(",");
                json.append("\"prezzoBase\":").append(el.getProdotto().getPrezzoBase()).append(",");
                json.append("\"immagine\":\"").append(el.getProdotto().getImmagineUrl()).append("\",");
                json.append("\"quantita\":").append(el.getQuantita()).append(",");
                json.append("\"opzioni\":\"").append(el.getOpzioni() != null ? el.getOpzioni().replace("\"", "\\\"") : "").append("\"");
                json.append("}");
                if (i < carrello.size() - 1) json.append(",");
            }
        }
        json.append("]");
        out.print(json.toString());
    }
}