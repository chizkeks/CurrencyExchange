package servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import model.ErrorMessage;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.CurrencyService;
import services.ExchangeRateService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
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
        Optional<?> result = exchangeRateService.getAllExchangeRates();
        result.ifPresent(exchangeRates -> pw.println(new Gson().toJson(exchangeRates)));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        double rate = Double.parseDouble(req.getParameter("rate"));

        if(validateRequiredParameters(baseCurrencyCode, targetCurrencyCode, rate)) {
            //Trying to get currencies corresponds to this exchange rate
            Optional<?> baseCurrency = currencyService.getCurrency(baseCurrencyCode);
            Optional<?> targetCurrency = currencyService.getCurrency(targetCurrencyCode);
            //If there are both currencies - continue exchange rate creation
            if(baseCurrency.isPresent() && targetCurrency.isPresent()) {
                //if the rate for the same currency pair already exists
                if(exchangeRateService.get(baseCurrencyCode, targetCurrencyCode).isPresent()) {
                    resp.setStatus(409);
                    pw.println(new Gson().toJson(new ErrorMessage("Валютная пара с таким кодом уже существует ")));
                } else {
                    //adding the exchange rate to the database
                    boolean result = exchangeRateService.createExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
                    if(result) {
                        resp.setStatus(201);
                        pw.println(new Gson().toJson(exchangeRateService.get(baseCurrencyCode, targetCurrencyCode).isPresent()));
                    } else {
                        resp.setStatus(500);
                    }
                }

            } else {
                resp.setStatus(404);
                pw.println(new Gson().toJson(new ErrorMessage("Одна (или обе) валюта из валютной пары не существует в БД")));
            }

        } else {
            resp.setStatus(400);
            pw.println(new Gson().toJson(new ErrorMessage("Отсутствует одно из обязательных полей")));
        }

    }

    //Function checks if exchangeRate parameter has all required fields
    private boolean validateRequiredParameters(String baseCurrencyCode, String targetCurrencyCode, double rate) {
        if(baseCurrencyCode== null || baseCurrencyCode.isEmpty())
            return false;
        if(targetCurrencyCode == null || targetCurrencyCode.isEmpty())
            return false;
        if(rate == 0)
            return false;
        return true;
    }

}
