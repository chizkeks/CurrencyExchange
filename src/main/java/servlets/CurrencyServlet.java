package servlets;

import com.google.gson.Gson;
import dao.CurrencyDAO;
import dao.CurrencyDAOImplSQLite;
import model.Currency;
import model.ErrorMessage;
import services.CurrencyExchangeDBService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Optional;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();

        String[] pathElements = req.getPathInfo().split("/");

        if(pathElements.length == 2){
            CurrencyDAO dao = new CurrencyDAOImplSQLite();

            //for url http://localhost:8080/CurrencyExchange/currency/RUR pathElements = [, RUR]
            //that's why we pass pathElements[1] to the function
            Optional<Currency> result = dao.getByCode(pathElements[1]);
            if(result.isPresent()) {
                pw.println(new Gson().toJson(result.get()));
            } else {
                resp.setStatus(404);
                pw.println(new Gson().toJson(new ErrorMessage("Валюта не найдена")));
            }
            return;
        }
        //If there are more than 1 path parameter throw an error
        resp.setStatus(400);
        pw.println(new Gson().toJson(new ErrorMessage("Некорректный запрос")));
    }
}
