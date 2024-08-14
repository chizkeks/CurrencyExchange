package servlets;

import com.google.gson.Gson;
import exceptions.CurrencyAlreadyExistsException;
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

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyService service;

    @Override
    public void init(ServletConfig config) throws ServletException {
        service = new CurrencyService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();
        try {
            service.getAllCurrencies().ifPresent(currencies -> pw.println(new Gson().toJson(currencies)));
        }catch (SQLException | DatabaseConnectionException e) {
            resp.setStatus(500);
            pw.println(new Gson().toJson(new ErrorMessage(e.getMessage())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");
        if(validateRequiredParameters(code, name, sign)) {

            try {
                service.createCurrency(req.getParameter("code"), req.getParameter("name"), req.getParameter("sign"));
                resp.setStatus(201);
                pw.println(new Gson().toJson(service.getCurrency(code).get()));
            }catch (CurrencyAlreadyExistsException e) {
                resp.setStatus(409);
                pw.println(new Gson().toJson(new ErrorMessage(e.getMessage())));
            } catch(SQLException | DatabaseConnectionException e) {
                resp.setStatus(500);
                pw.println(new Gson().toJson(new ErrorMessage(e.getMessage())));
            }

        } else {
            resp.setStatus(400);
            pw.println(new Gson().toJson(new ErrorMessage("Отсутствует одно из обязательных полей")));
        }

    }

    private boolean validateRequiredParameters(String code, String name, String sign) {
        if(code == null ||code.isEmpty())
            return false;
        if(name == null || name.isEmpty())
            return false;
        if(sign == null || sign.isEmpty())
            return false;
        return true;
    }
}
