package control;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DBConnect;
import model.OrdineAdminDashboard;

@WebServlet(name = "AdminOrdiniServlet", urlPatterns = {"/AdminOrdiniServlet"})
public class AdminOrdiniServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Creo l'oggetto per connettermi al DB
        DBConnect db = new DBConnect();
        
        try {
            // 2. Chiedo al database la lista degli ordini (quella che abbiamo scritto prima)
            List<OrdineAdminDashboard> listaOrdini = db.getTuttiGliOrdiniConStorico();
            
            // 3. Metto la lista nel "vassoio" (request) così la pagina JSP può leggerla
            request.setAttribute("listaOrdini", listaOrdini);
            
            // 4. Mando tutto alla pagina che disegna la tabella (dashboard_admin.jsp)
            request.getRequestDispatcher("dashboard_admin.jsp").forward(request, response);
            
        } catch (SQLException e) {
            // Se c'è un errore col database, lo scriviamo nel log e avvisiamo l'utente
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore caricamento ordini: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Per ora facciamo lo stesso del doGet
        doGet(request, response);
    }
}