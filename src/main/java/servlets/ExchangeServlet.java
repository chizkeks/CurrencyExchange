package servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletConfig;
import model.ErrorMessage;
import model.ExchangeRate;
import model.ExchangeResult;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.CurrencyService;
import services.ExchangeRateService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;
    private CurrencyService currencyService;

    @Override
    public void init(ServletConfig config) {
        exchangeRateService = new ExchangeRateService();
        currencyService = new CurrencyService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter pw = resp.getWriter();

        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String amountString = req.getParameter("amount");

        if(validateRequiredParams(from, to, amountString)) {

            Optional<?> fromCurrency = currencyService.getCurrency(from);
            Optional<?> toCurrency = currencyService.getCurrency(to);

            if(fromCurrency.isEmpty() && toCurrency.isEmpty()) {
                resp.setStatus(404);
                pw.println(new Gson().toJson(new ErrorMessage("Валютная пара отсутствует в базе данных")));
                return;
            }
            //1. В таблице ExchangeRates существует валютная пара AB - берём её курс
            Optional<ExchangeRate> rate = exchangeRateService.get(from, to);
            if(rate.isPresent()) {
                pw.println(new Gson().toJson(new ExchangeResult(rate.get().getBaseCurrency(), rate.get().getTargetCurrency(), rate.get().getRate(), Double.parseDouble(amountString))));
                return;
            }

            //2. В таблице ExchangeRates существует валютная пара BA - берем её курс, и считаем обратный, чтобы получить AB
            rate = exchangeRateService.get(to, from);
            if(rate.isPresent()) {
                pw.println(new Gson().toJson(new ExchangeResult(rate.get().getBaseCurrency(), rate.get().getTargetCurrency(), rate.get().getRate(), Double.parseDouble(amountString))));
                return;
            }

            //3. В таблице ExchangeRates существуют валютные пары USD-A и USD-B - вычисляем из этих курсов курс AB
            rate = exchangeRateService.get("USD", to);
            Optional<ExchangeRate> rateToUSD = exchangeRateService.get("USD", from);
            if(rate.isPresent() && rateToUSD.isPresent()) {
                pw.println(new Gson().toJson(new ExchangeResult(rate.get().getBaseCurrency(), rate.get().getTargetCurrency(), rate.get().getRate(), Double.parseDouble(amountString))));
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
