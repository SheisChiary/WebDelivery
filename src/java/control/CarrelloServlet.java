package control;

import model.ElementoCarrello;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class CarrelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            HttpSession session = request.getSession();
            List<ElementoCarrello> carrello = (List<ElementoCarrello>) session.getAttribute("carrello");
            StringBuilder json = new StringBuilder();
            json.append("[");
            
            if (carrello != null && !carrello.isEmpty()) {
                for (int i = 0; i < carrello.size(); i++) {
                    ElementoCarrello el = carrello.get(i);
                    String nome = el.getProdotto().getNome() != null ? el.getProdotto().getNome().replace("\"", "\\\"") : "";
                    String desc = el.getProdotto().getDescrizione() != null ? el.getProdotto().getDescrizione().replace("\"", "\\\"").replace("\n", " ") : "";
                    String img = el.getProdotto().getImmagineUrl() != null ? el.getProdotto().getImmagineUrl() : "";
                    String opzioniSalvate = el.getOpzioni() != null ? el.getOpzioni().replace("\"", "\\\"") : "";

                    json.append("{");
                    json.append("\"id\":").append(el.getProdotto().getIdProdotto()).append(",");
                    json.append("\"nome\":\"").append(nome).append("\",");
                    json.append("\"descrizione\":\"").append(desc).append("\",");
                    json.append("\"prezzo\":").append(el.getPrezzoUnitario()).append(",");
                    
                    
                    json.append("\"prezzoBase\":").append(el.getProdotto().getPrezzoBase()).append(",");
                    
                    json.append("\"immagine\":\"").append(img).append("\",");
                    json.append("\"quantita\":").append(el.getQuantita()).append(",");
                    json.append("\"opzioni\":\"").append(opzioniSalvate).append("\"");
                    json.append("}");
                    
                    if (i < carrello.size() - 1) json.append(",");
                }
            }
            json.append("]");
            out.print(json.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
            out.print("[]");
        }
    }
}