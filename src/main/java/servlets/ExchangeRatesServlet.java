package servlets;

import com.google.gson.Gson;
import exceptions.CurrencyPairAlreadyExistsException;
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

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;
    private CurrencyService currencyService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        exchangeRateService = ExchangeRateService.getInstance();
        currencyService = CurrencyService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();
        try {
            Optional<?> result = exchangeRateService.getAllExchangeRates();
            result.ifPresent(exchangeRates -> pw.println(new Gson().toJson(exchangeRates)));
        } catch(SQLException | DatabaseConnectionException e) {
            resp.setStatus(500);
            pw.println(new Gson().toJson(new ErrorMessage(e.getMessage())));
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");
        System.out.printf("BASE - %s, TARGET - %s; RATE - %s", baseCurrencyCode, targetCurrencyCode, rate);

        if(validateRequiredParameters(baseCurrencyCode, targetCurrencyCode, rate)) {
            try {
                //Trying to get currencies corresponds to this exchange rate
                Optional<Currency> baseCurrency = currencyService.getCurrency(baseCurrencyCode);
                Optional<Currency> targetCurrency = currencyService.getCurrency(targetCurrencyCode);
                //If there are both currencies - continue exchange rate creation
                if (baseCurrency.isPresent() && targetCurrency.isPresent()) {
                    //adding the exchange rate to the database
                    try {
                        exchangeRateService.createExchangeRate(baseCurrency.get().getId(), targetCurrency.get().getId(), Double.parseDouble(rate));
                        resp.setStatus(201);
                        pw.println(new Gson().toJson(exchangeRateService.get(baseCurrencyCode, targetCurrencyCode).isPresent()));
                    } catch (CurrencyPairAlreadyExistsException e) {
                        resp.setStatus(409);
                        pw.println(new Gson().toJson(new ErrorMessage(e.getMessage())));
                    }

                } else {
                    resp.setStatus(404);
                    pw.println(new Gson().toJson(new ErrorMessage("Одна (или обе) валюта из валютной пары не существует в БД")));
                }
            }
            catch(SQLException | DatabaseConnectionException e) {
                resp.setStatus(500);
                pw.println(new Gson().toJson(new ErrorMessage(e.getMessage())));
            }
        } else {
            resp.setStatus(400);
            pw.println(new Gson().toJson(new ErrorMessage("Отсутствует одно из обязательных полей")));
        }

    }

    //Function checks if exchangeRate parameter has all required fields
    private boolean validateRequiredParameters(String baseCurrencyCode, String targetCurrencyCode, String rate) {
        if(baseCurrencyCode== null || baseCurrencyCode.isEmpty())
            return false;
        if(targetCurrencyCode == null || targetCurrencyCode.isEmpty())
            return false;
        return rate != null && !rate.isEmpty();
    }

}
