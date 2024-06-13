package services;

import dao.CurrencyDAO;
import dao.CurrencyDAOImplSQLite;
import model.Currency;

import java.util.List;
import java.util.Optional;

public class CurrencyService {

    private final CurrencyDAO currencyDAO;

    public CurrencyService() {
        this.currencyDAO = new CurrencyDAOImplSQLite();
    }

    public Optional<List<Currency>> getAllCurrencies() {
        return currencyDAO.getList();
    }

    public Optional<Currency> getCurrency(String code) {
        return currencyDAO.getByCode(code);
    }
    public boolean createCurrency(String code, String name, String sign) {
        return currencyDAO.add(new Currency(code, name, sign));
    }
}
