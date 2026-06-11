package control;

import model.DBConnect;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class OpzioniServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        int idProdotto = Integer.parseInt(request.getParameter("id"));
        StringBuilder json = new StringBuilder();
        json.append("{");
        
        try (Connection conn = DBConnect.getConnection()) {
            
            
            String queryGruppi = "SELECT id_gruppo, nome_gruppo FROM Gruppo_Esclusione WHERE id_prodotto = ?";
            try (PreparedStatement psG = conn.prepareStatement(queryGruppi)) {
                psG.setInt(1, idProdotto);
                ResultSet rsG = psG.executeQuery();
                json.append("\"gruppi\": [");
                boolean firstGruppo = true;
                
                while(rsG.next()) {
                    if(!firstGruppo) json.append(",");
                    json.append("{\"nome\": \"").append(rsG.getString("nome_gruppo")).append("\", \"opzioni\": [");
                    
                   
                    String queryOpz = "SELECT nome, differenza_prezzo, is_default FROM Caratteristica WHERE id_gruppo = ?";
                    try (PreparedStatement psO = conn.prepareStatement(queryOpz)) {
                        psO.setInt(1, rsG.getInt("id_gruppo"));
                        ResultSet rsO = psO.executeQuery();
                        boolean firstOpz = true;
                        while(rsO.next()) {
                            if(!firstOpz) json.append(",");
                            json.append("{\"nome\": \"").append(rsO.getString("nome")).append("\",");
                            json.append("\"prezzo\": ").append(rsO.getDouble("differenza_prezzo")).append(",");
                            json.append("\"is_default\": ").append(rsO.getBoolean("is_default")).append("}");
                            firstOpz = false;
                        }
                    }
                    json.append("]}");
                    firstGruppo = false;
                }
                json.append("],");
            }
            
            
            String queryExtra = "SELECT nome, differenza_prezzo FROM Caratteristica WHERE id_prodotto = ? AND id_gruppo IS NULL";
            try (PreparedStatement psE = conn.prepareStatement(queryExtra)) {
                psE.setInt(1, idProdotto);
                ResultSet rsE = psE.executeQuery();
                json.append("\"extra\": [");
                boolean firstExtra = true;
                while(rsE.next()) {
                    if(!firstExtra) json.append(",");
                    json.append("{\"nome\": \"").append(rsE.getString("nome")).append("\",");
                    json.append("\"prezzo\": ").append(rsE.getDouble("differenza_prezzo")).append("}");
                    firstExtra = false;
                }
                json.append("]");
            }
            
        } catch (Exception e) {
             e.printStackTrace();
             out.print("{\"error\":\"true\"}");
             return;
        }
        
        json.append("}");
        out.print(json.toString());
    }
}