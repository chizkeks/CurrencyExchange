package services;

import dao.CurrencyDAO;
import dao.CurrencyDAOImplSQLite;
import dto.CurrencyDto;
import dto.CurrencyFilter;
import exceptions.CurrencyAlreadyExistsException;
import exceptions.DatabaseConnectionException;
import exceptions.NoSuchCurrencyException;
import model.Currency;

import java.sql.SQLException;
import java.util.List;

public class CurrencyService {

    private final CurrencyDAO currencyDAO;

    public static CurrencyService getInstance() {
        return CurrencyService.CurrencyServiceHelper.singletonObject;
    }

    private static class CurrencyServiceHelper{
        public static CurrencyService singletonObject = new CurrencyService();
    }

    private CurrencyService() {
        this.currencyDAO = CurrencyDAOImplSQLite.getInstance();
    }

    public List<Currency> getAllCurrencies() throws SQLException, DatabaseConnectionException{
            return currencyDAO.findAll(null);
    }

    public CurrencyDto getCurrency(String code) throws SQLException, DatabaseConnectionException, NoSuchCurrencyException{
        List<Currency> response = currencyDAO.findAll(new CurrencyFilter(code, null, null, 1, 0));
        if(!response.isEmpty())
                return new CurrencyDto(
                        response.get(0).getId(),
                        response.get(0).getCode(),
                        response.get(0).getFullName(),
                        response.get(0).getSign());
        else
            throw new NoSuchCurrencyException("Валюта не найдена");
    }
    public CurrencyDto createCurrency(CurrencyDto currency) throws SQLException, CurrencyAlreadyExistsException, DatabaseConnectionException {
        currency.setId(currencyDAO.add(new Currency(currency.getCode(), currency.getFullName(), currency.getSign())));
        return currency;
    }
}
