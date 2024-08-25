package servlets;

import com.google.gson.Gson;
import dto.CurrencyDto;
import exceptions.CurrencyAlreadyExistsException;
import exceptions.DatabaseConnectionException;
import jakarta.servlet.ServletConfig;
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

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyService service;

    @Override
    public void init(ServletConfig config) throws ServletException {
        service = CurrencyService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();
        try {
            pw.println(new Gson().toJson(service.getAllCurrencies()));
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
                CurrencyDto newCurrency = new CurrencyDto(req.getParameter("code"), req.getParameter("name"), req.getParameter("sign"));
                resp.setStatus(201);
                pw.println(new Gson().toJson(service.createCurrency(newCurrency)));
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
        return sign != null && !sign.isEmpty();
    }
}
