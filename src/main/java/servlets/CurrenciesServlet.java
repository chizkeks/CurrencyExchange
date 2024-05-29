package servlets;

import com.google.gson.Gson;
import dao.CurrencyDAO;
import dao.CurrencyDAOImplSQLite;
import model.Currency;
import model.ErrorMessage;
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

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();
        //resp.setContentType("application/json; charset = UTF-8");

        CurrencyDAO currencyDAO = new CurrencyDAOImplSQLite();
        //URL url = ClassLoaderUtil.getResource("test.csv", servlets.CurrenciesServlet.class);

        Optional<List<Currency>> result = currencyDAO.getList();
        result.ifPresent(currencies -> pw.println(new Gson().toJson(currencies)));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();
        Currency newCurrency = new Currency(0, req.getParameter("code"), req.getParameter("name"), req.getParameter("sign"));
        if(validateCurrency(newCurrency)) {
            CurrencyDAO currencyDAO = new CurrencyDAOImplSQLite();

            if(currencyDAO.getByCode(newCurrency.getCode()).isPresent()) {
                resp.setStatus(409);
                pw.println(new Gson().toJson(new ErrorMessage("Валюта с таким кодом уже существует")));
                return;
            }

            boolean result = currencyDAO.add(newCurrency);
            if(result) {
                resp.setStatus(201);
                pw.println(new Gson().toJson(currencyDAO.getByCode(newCurrency.getCode()).get()));
            } else {
                resp.setStatus(500);
            }

        } else {
            resp.setStatus(400);
            pw.println(new Gson().toJson(new ErrorMessage("Отсутствует одно из обязательных полей")));
        }

    }

    private boolean validateCurrency(Currency currency) {
        if(currency.getCode() == null || currency.getCode().isEmpty())
            return false;
        if(currency.getFullName() == null || currency.getFullName().isEmpty())
            return false;
        if(currency.getSign() == null || currency.getSign().isEmpty())
            return false;
        return true;
    }
}
