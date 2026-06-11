package control;

import model.Prodotto;
import model.ProdottoDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MenuServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        
        ProdottoDAO dao = new ProdottoDAO();
        List<Prodotto> prodotti = dao.getAllProdotti();

        
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (int i = 0; i < prodotti.size(); i++) {
            Prodotto p = prodotti.get(i);
            json.append("{");
            json.append("\"id\":").append(p.getIdProdotto()).append(",");
            json.append("\"nome\":\"").append(p.getNome().replace("\"", "\\\"")).append("\",");
            json.append("\"descrizione\":\"").append(p.getDescrizione().replace("\"", "\\\"")).append("\",");
            json.append("\"prezzo\":").append(p.getPrezzoBase()).append(",");
            json.append("\"categoria\":\"").append(p.getCategoria()).append("\",");
            json.append("\"immagine\":\"").append(p.getImmagineUrl()).append("\",");
            json.append("\"badge\":").append(p.getBadge() == null ? "null" : "\"" + p.getBadge() + "\"");
            json.append("}");
            
            if (i < prodotti.size() - 1) {
                json.append(","); 
            }
        }
        json.append("]");
        
     
        out.print(json.toString());
    }
}