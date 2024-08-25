package servlets;

import com.google.gson.Gson;
import dto.CurrencyDto;
import dto.ExchangeRateDto;
import exceptions.DatabaseConnectionException;
import exceptions.NoSuchCurrencyException;
import exceptions.NoSuchExchangeRateException;
import jakarta.servlet.ServletConfig;
import model.ErrorMessage;
import model.ExchangeResult;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.CurrencyService;
import services.ExchangeRateService;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;
    private CurrencyService currencyService;

    @Override
    public void init(ServletConfig config) {
        exchangeRateService = ExchangeRateService.getInstance();
        currencyService = CurrencyService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter pw = resp.getWriter();

        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amountString = req.getParameter("amount");

        if(validateRequiredParams(from, to, amountString)) {
            try {
                //Trying to get both currencies (if ok - continue, else - NoSuchCurrencyException)
                CurrencyDto fromCurrency = currencyService.getCurrency(from);
                CurrencyDto toCurrency = currencyService.getCurrency(to);

                ExchangeRateDto rate;
                //1. В таблице ExchangeRates существует валютная пара AB - берём её курс

                try {
                    rate = exchangeRateService.get(from, to);
                    pw.println(new Gson().toJson(new ExchangeResult(rate.getBaseCurrency(), rate.getTargetCurrency(), rate.getRate(), Double.parseDouble(amountString))));
                    return;
                } catch(NoSuchExchangeRateException e) {
                    //Just ignore and get to the other condition
                    e.printStackTrace();
                }

                //2. В таблице ExchangeRates существует валютная пара BA - берем её курс, и считаем обратный, чтобы получить AB
                try {
                    rate = exchangeRateService.get(to, from);
                    pw.println(new Gson().toJson(new ExchangeResult(rate.getBaseCurrency(), rate.getTargetCurrency(), rate.getRate(), Double.parseDouble(amountString))));
                    return;
                } catch(NoSuchExchangeRateException e) {
                    //Just ignore and get to the other condition
                    e.printStackTrace();
                }

                //3. В таблице ExchangeRates существуют валютные пары USD-A и USD-B - вычисляем из этих курсов курс AB
                try {
                    rate = exchangeRateService.get("USD", to);
                    ExchangeRateDto rateToUSD = exchangeRateService.get("USD", from);
                    pw.println(new Gson().toJson(new ExchangeResult(rate.getBaseCurrency(), rate.getTargetCurrency(), rate.getRate(), Double.parseDouble(amountString))));
                    return;
                } catch(NoSuchExchangeRateException e) {
                    resp.setStatus(400);
                    pw.println(new Gson().toJson(new ErrorMessage("Курс для валютной пары не найден")));
                }
            }  catch(NoSuchCurrencyException e) {
                resp.setStatus(404);
                pw.println(new Gson().toJson(new ErrorMessage("Одна (или обе) валюта из валютной пары не существует в БД")));
            } catch(SQLException | DatabaseConnectionException e) {
                resp.setStatus(500);
                pw.println(new Gson().toJson(new ErrorMessage(e.getMessage())));
            }

        } else {
            resp.setStatus(400);
            pw.println(new Gson().toJson(new ErrorMessage("Отсутствует нужное поле формы")));
        }
    }

    private boolean validateRequiredParams(String from, String to, String amount) {
        if(from == null || from.isEmpty())
            return false;
        if(to == null || to.isEmpty())
            return false;
        return amount != null && !amount.isEmpty();
    }
}
