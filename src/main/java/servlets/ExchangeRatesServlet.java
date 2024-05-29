package servlets;

import com.google.gson.Gson;
import dao.*;
import model.Currency;
import model.ErrorMessage;
import model.ExchangeRate;
import services.CurrencyExchangeDBService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();
        CurrencyExchangeDBService dbService = new CurrencyExchangeDBService();
        Optional<List<ExchangeRate>> result = dbService.getAllExchangeRates();
        result.ifPresent(exchangeRates -> pw.println(new Gson().toJson(exchangeRates)));
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();
        ExchangeRate newExchangeRate = new ExchangeRate(0,
                new Currency(0, null, req.getParameter("baseCurrencyCode"), null),
                new Currency(0, null, req.getParameter("targetCurrencyCode"), null),
                Double.parseDouble(req.getParameter("rate")));

        if(validateExchangeRate(newExchangeRate)) {
            CurrencyDAO currencyDAO = new CurrencyDAOImplSQLite();
            Optional<Currency> baseCurrency = currencyDAO.getByCode(newExchangeRate.getBaseCurrency().getCode());
            Optional<Currency> targetCurrency = currencyDAO.getByCode(newExchangeRate.getTargetCurrency().getCode());

            if(baseCurrency.isPresent() && targetCurrency.isPresent()) {
                ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAOImplSQLite();
                if(exchangeRateDAO.getByCurrencyPair(newExchangeRate.getBaseCurrency().getCode(), newExchangeRate.getTargetCurrency().getCode()).isPresent()) {

                } else {
                    boolean result = exchangeRateDAO.add(newExchangeRate);
                    if(result) {
                        resp.setStatus(201);
                        pw.println(new Gson().toJson(exchangeRateDAO.getByCurrencyPair(newExchangeRate.getBaseCurrency().getCode(), newExchangeRate.getTargetCurrency().getCode()).isPresent()));
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

    private boolean validateExchangeRate(ExchangeRate exchangeRate) {
        if(exchangeRate.getBaseCurrency().getCode() == null || exchangeRate.getBaseCurrency().getCode().isEmpty())
            return false;
        if(exchangeRate.getTargetCurrency().getCode() == null || exchangeRate.getTargetCurrency().getCode().isEmpty())
            return false;
        if(exchangeRate.getRate() == 0)
            return false;
        return true;
    }

}
