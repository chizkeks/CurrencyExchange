package servlets;

import com.google.gson.Gson;
import dao.CurrencyDAO;
import dao.CurrencyDAOImplSQLite;
import dao.ExchangeRateDAO;
import dao.ExchangeRateDAOImplSQLite;
import model.Currency;
import model.ErrorMessage;
import model.ExchangeRate;
import model.ExchangeResult;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.regex.Pattern;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();

        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amountString = req.getParameter("amount");

        if(validateRequiredParams(from, to, amountString)) {
            ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAOImplSQLite();
            CurrencyDAO currencyDAO = new CurrencyDAOImplSQLite();
            Optional<Currency> fromCurrency = currencyDAO.getByCode(from);
            Optional<Currency> toCurrency = currencyDAO.getByCode(to);

            if(!fromCurrency.isPresent() || !toCurrency.isPresent()) {
                resp.setStatus(404);
                pw.println(new Gson().toJson(new ErrorMessage("Валютная пара отсутствует в базе данных")));
                return;
            }
            //1. В таблице ExchangeRates существует валютная пара AB - берём её курс
            Optional<ExchangeRate> rate = exchangeRateDAO.getByCurrencyPairCode(from, to);
            if(rate.isPresent()) {
                pw.println(new Gson().toJson(new ExchangeResult(fromCurrency.get(), toCurrency.get(), rate.get().getRate(), Double.parseDouble(amountString))));
                return;
            }

            //2. В таблице ExchangeRates существует валютная пара BA - берем её курс, и считаем обратный, чтобы получить AB
            rate = exchangeRateDAO.getByCurrencyPairCode(to, from);
            if(rate.isPresent()) {
                pw.println(new Gson().toJson(new ExchangeResult(fromCurrency.get(), toCurrency.get(), rate.get().getRate(), Double.parseDouble(amountString))));
                return;
            }

            //3. В таблице ExchangeRates существуют валютные пары USD-A и USD-B - вычисляем из этих курсов курс AB
            rate = exchangeRateDAO.getByCurrencyPairCode("USD", to);
            Optional<ExchangeRate> rateToUSD = exchangeRateDAO.getByCurrencyPairCode("USD", from);
            if(rate.isPresent() && rateToUSD.isPresent()) {
                pw.println(new Gson().toJson(new ExchangeResult(fromCurrency.get(), toCurrency.get(), rate.get().getRate(), Double.parseDouble(amountString))));
                return;
            }
        }

        resp.setStatus(400);
        pw.println(new Gson().toJson(new ErrorMessage("Отсутствует нужное поле формы")));
    }

    private boolean validateRequiredParams(String from, String to, String amount) {
        if(from == null || from.isEmpty())
            return false;
        if(to == null || to.isEmpty())
            return false;
        return amount != null && !amount.isEmpty();
    }
}
