package servlets;

import com.google.gson.Gson;
import dao.ExchangeRateDAO;
import dao.ExchangeRateDAOImplSQLite;
import model.ErrorMessage;
import model.ExchangeRate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet  extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();

        String[] pathElements = req.getPathInfo().split("/");

        if(pathElements.length == 2 && pathElements[1].length() == 6){
            ExchangeRateDAO dao = new ExchangeRateDAOImplSQLite();

            //for url http://localhost:8080/CurrencyExchange/currency/RUR pathElements = [, RUR]
            //that's why we pass pathElements[1] to the function
            System.out.println(pathElements[1].substring(0, 3));
            System.out.println(pathElements[1].substring(3));
            Optional<ExchangeRate> rate = dao.getByCurrencyPair(pathElements[1].substring(0, 3), pathElements[1].substring(3));
            if(rate.isPresent()) {
                pw.println(new Gson().toJson(rate.get()));
            } else {
                resp.setStatus(404);
                pw.println(new Gson().toJson(new ErrorMessage("Обменный курс для пары не найден")));;
            }
            return;
        }
        resp.setStatus(400);
        pw.println(new Gson().toJson(new ErrorMessage("Коды валют пары отсутствуют в адресе")));
    }
}
