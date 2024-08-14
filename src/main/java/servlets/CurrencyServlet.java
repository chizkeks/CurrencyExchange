package servlets;

import com.google.gson.Gson;
import exceptions.DatabaseConnectionException;
import jakarta.servlet.ServletConfig;
import model.ErrorMessage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.CurrencyService;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import model.Currency;
import java.util.Optional;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyService service;

    @Override
    public void init(ServletConfig config) throws ServletException {
        service = new CurrencyService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();

        String[] pathElements = req.getPathInfo().split("/");

        if(pathElements.length == 2){
            try{
                //for url http://localhost:8080/CurrencyExchange/currency/RUR pathElements = [, RUR]
                //that's why we pass pathElements[1] to the function
                Optional<Currency> result = service.getCurrency(pathElements[1]);
                if(result.isPresent()) {
                    pw.println(new Gson().toJson(result.get()));
                } else {
                    resp.setStatus(404);
                    pw.println(new Gson().toJson(new ErrorMessage("Валюта не найдена")));
                }
                return;
            }catch (SQLException | DatabaseConnectionException e) {
                resp.setStatus(500);
                pw.println(new Gson().toJson(new ErrorMessage(e.getMessage())));
            }

        }
        //If there are more than 1 path parameter throw an error
        resp.setStatus(400);
        pw.println(new Gson().toJson(new ErrorMessage("Некорректный запрос. Код валюты отсутствует в адресе")));
    }
}
