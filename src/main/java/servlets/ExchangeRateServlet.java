package servlets;

import com.google.common.base.Splitter;
import com.google.gson.Gson;
import dto.CurrencyDto;
import exceptions.DatabaseConnectionException;
import exceptions.NoSuchCurrencyException;
import exceptions.NoSuchExchangeRateException;
import jakarta.servlet.ServletConfig;
import model.ErrorMessage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.CurrencyService;
import services.ExchangeRateService;

import java.io.*;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet  extends HttpServlet {
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

        String[] pathElements = req.getPathInfo().split("/");

        if(pathElements.length == 2 && pathElements[1].length() == 6) {
            try {
                //for url http://localhost:8080/CurrencyExchange/currency/RUR pathElements = [, RUR]
                //that's why we pass pathElements[1] to the function
                pw.println(new Gson().toJson(exchangeRateService.get(pathElements[1].substring(0, 3), pathElements[1].substring(3))));
                return;
            } catch(NoSuchExchangeRateException e) {
                resp.setStatus(404);
                pw.println(new Gson().toJson(new ErrorMessage("Обменный курс для пары не найден")));
            } catch(SQLException | DatabaseConnectionException e) {
                resp.setStatus(500);
                pw.println(new Gson().toJson(new ErrorMessage(e.getMessage())));
            }
        }
        resp.setStatus(400);
        pw.println(new Gson().toJson(new ErrorMessage("Коды валют пары отсутствуют в адресе")));
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if(method.equals("PATCH"))
            doPatch(req, resp);
        else super.service(req, resp);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();

        String[] pathElements = req.getPathInfo().split("/");

        if(pathElements.length == 2 && pathElements[1].length() == 6) {

            //Get x-www-form-urlencoded param
            BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
            String data = br.readLine();
            Map<String, String> reqParams;
            reqParams = Splitter.on('&')
                    .trimResults()
                    .withKeyValueSeparator(
                            Splitter.on('=')
                                    .limit(2)
                                    .trimResults())
                    .split(data);

            String newRate = reqParams.get("rate");
            if(newRate == null || newRate.isEmpty()) {
                resp.setStatus(400);
                pw.println(new Gson().toJson(new ErrorMessage(data)));
                return;
            }

            //Get path params
            String baseCurrencyCode = pathElements[1].substring(0, 3);
            String targetCurrencyCode = pathElements[1].substring(3);

            try {
                //for url http://localhost:8080/CurrencyExchange/currency/RUR pathElements = [, RUR]
                //that's why we pass pathElements[1] to the function
                CurrencyDto baseCurrency = currencyService.getCurrency(baseCurrencyCode);
                CurrencyDto targetCurrency = currencyService.getCurrency(targetCurrencyCode);

                exchangeRateService.updateExchangeRate(baseCurrency.getId(),
                        targetCurrency.getId(), Double.parseDouble(newRate));

                pw.println(new Gson().toJson(exchangeRateService.get(baseCurrencyCode,targetCurrencyCode)));
                return;

            } catch(NoSuchExchangeRateException e) {
                resp.setStatus(400);
                pw.println(new Gson().toJson(new ErrorMessage("Курс для валютной пары не найден")));
            } catch(NoSuchCurrencyException e) {
                resp.setStatus(404);
                pw.println(new Gson().toJson(new ErrorMessage("Одна (или обе) валюта из валютной пары не существует в БД")));
            } catch (SQLException | DatabaseConnectionException e) {
                resp.setStatus(500);
                pw.println(new Gson().toJson(new ErrorMessage(e.getMessage())));
            }
        } else {
            resp.setStatus(400);
            pw.println(new Gson().toJson(new ErrorMessage("Коды валют пары отсутствуют в адресе")));
        }
    }
}
