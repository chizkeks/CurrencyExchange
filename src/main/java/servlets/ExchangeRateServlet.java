package servlets;

import com.google.gson.Gson;
import dao.CurrencyDAO;
import dao.CurrencyDAOImplSQLite;
import dao.ExchangeRateDAO;
import dao.ExchangeRateDAOImplSQLite;
import model.Currency;
import model.ErrorMessage;
import model.ExchangeRate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
            Optional<ExchangeRate> rate = dao.getByCurrencyPairCode(pathElements[1].substring(0, 3), pathElements[1].substring(3));
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

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if(!method.equals("PATCH"))
            super.service(req, resp);

        doPatch(req, resp);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();

        String[] pathElements = req.getPathInfo().split("/");

        if(pathElements.length == 2 && pathElements[1].length() == 6) {

            //check required fields
            BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
            String data = br.readLine();
            String newRate  = "";
            if(newRate == null || newRate.isEmpty()) {
                resp.setStatus(400);
                pw.println(new Gson().toJson(new ErrorMessage("Отсутствует нужное поле формы")));
                return;
            }

            CurrencyDAO curDao = new CurrencyDAOImplSQLite();
            //for url http://localhost:8080/CurrencyExchange/currency/RUR pathElements = [, RUR]
            //that's why we pass pathElements[1] to the function
            Optional<Currency> baseCurrency = curDao.getByCode(pathElements[1].substring(0, 3));
            Optional<Currency> targetCurrency = curDao.getByCode(pathElements[1].substring(3));
            if(baseCurrency.isPresent() && targetCurrency.isPresent()) {
                ExchangeRateDAO erDao = new ExchangeRateDAOImplSQLite();
                boolean result = erDao.updateRateByCurrencyPairId(baseCurrency.get().getId(), baseCurrency.get().getId(), Double.parseDouble(newRate));
                if(result) {
                    pw.println(new Gson().toJson(erDao.getByCurrencyPairCode(baseCurrency.get().getCode(), targetCurrency.get().getCode())));
                    return;
                }
            }
            resp.setStatus(404);
            pw.println(new Gson().toJson(new ErrorMessage("Валютная пара отсутствует в базе данных")));;
            return;
        }
        resp.setStatus(400);
        pw.println(new Gson().toJson(new ErrorMessage("Коды валют пары отсутствуют в адресе")));
    }
}
