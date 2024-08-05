package servlets;

import com.google.gson.Gson;
import exceptions.DatabaseConnectionException;
import jakarta.servlet.ServletConfig;
import model.Currency;
import model.ErrorMessage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.CurrencyService;
import services.ExchangeRateService;

import java.io.*;
import java.util.Optional;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet  extends HttpServlet {
    private ExchangeRateService exchangeRateService;
    private CurrencyService currencyService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        exchangeRateService = new ExchangeRateService();
        currencyService = new CurrencyService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();

        String[] pathElements = req.getPathInfo().split("/");

        if(pathElements.length == 2 && pathElements[1].length() == 6){

            //for url http://localhost:8080/CurrencyExchange/currency/RUR pathElements = [, RUR]
            //that's why we pass pathElements[1] to the function
            Optional<?> rate = exchangeRateService.get(pathElements[1].substring(0, 3), pathElements[1].substring(3));
            if(rate.isPresent()) {
                pw.println(new Gson().toJson(rate.get()));
            } else {
                resp.setStatus(404);
                pw.println(new Gson().toJson(new ErrorMessage("Обменный курс для пары не найден")));
            }
            return;
        }
        resp.setStatus(400);
        pw.println(new Gson().toJson(new ErrorMessage("Коды валют пары отсутствуют в адресе")));
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        super.service(req, resp);
        if(method.equals("PATCH"))
            doPatch(req, resp);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();

        String[] pathElements = req.getPathInfo().split("/");

        if(pathElements.length == 2 && pathElements[1].length() == 6) {

            //check required fields
            BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
            String data = br.readLine();
            System.out.println(data);
            String newRate  = "";
            if(newRate == null || newRate.isEmpty()) {
                resp.setStatus(400);
                pw.println(new Gson().toJson(new ErrorMessage(data)));
                return;
            }

            String baseCurrencyCode = pathElements[1].substring(0, 3);
            String targetCurrencyCode = pathElements[1].substring(3);

            //for url http://localhost:8080/CurrencyExchange/currency/RUR pathElements = [, RUR]
            //that's why we pass pathElements[1] to the function
            Optional<Currency> baseCurrency = currencyService.getCurrency(baseCurrencyCode);
            Optional<Currency> targetCurrency = currencyService.getCurrency(targetCurrencyCode);
            if(baseCurrency.isPresent() && targetCurrency.isPresent()) {
               try {
                   exchangeRateService.updateExchangeRate(baseCurrency.get().getId(),
                           targetCurrency.get().getId(), Double.parseDouble(newRate));
               } catch(DatabaseConnectionException e) {
                   resp.setStatus(500);
               }
               pw.println(new Gson().toJson(exchangeRateService.get(baseCurrencyCode,targetCurrencyCode)));
               return;
            }
            resp.setStatus(404);
            pw.println(new Gson().toJson(new ErrorMessage("Валютная пара отсутствует в базе данных")));;
            return;
        }
        resp.setStatus(400);
        pw.println(new Gson().toJson(new ErrorMessage("Коды валют пары отсутствуют в адресе")));
    }
}
