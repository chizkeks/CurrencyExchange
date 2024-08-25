package services;

import dao.ExchangeRateDAO;
import dao.ExchangeRateDAOImplSQLite;
import dto.CurrencyDto;
import dto.ExchangeRateDto;
import dto.ExchangeRateFilter;
import exceptions.CurrencyPairAlreadyExistsException;
import exceptions.DatabaseConnectionException;
import exceptions.NoSuchCurrencyException;
import exceptions.NoSuchExchangeRateException;
import mappers.ExchangeRateMapper;
import model.ExchangeRate;
import org.sqlite.SQLiteException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateService {
    private final ExchangeRateDAO exchangeRateDAO;
    private final CurrencyService currencyService;
    private ExchangeRateService() {
        this.exchangeRateDAO = ExchangeRateDAOImplSQLite.getInstance();
        this.currencyService = CurrencyService.getInstance();
    }
    public static ExchangeRateService getInstance() {
        return ExchangeRateService.ExchangeRateServiceHelper.singletonObject;
    }

    private static class ExchangeRateServiceHelper{
        public static ExchangeRateService singletonObject = new ExchangeRateService();
    }

    public ExchangeRateDto get(String baseCurrency, String targetCurrency) throws SQLException, DatabaseConnectionException, NoSuchExchangeRateException {
        List<ExchangeRate> response = exchangeRateDAO.findAll(new ExchangeRateFilter(baseCurrency, targetCurrency, null, 1,0));
        if(!response.isEmpty())
            return ExchangeRateMapper.mapExchangeRateToDto(response.get(0));
        else
            throw new NoSuchExchangeRateException(String.format("Обменный курс не найден для пары %s - %s", baseCurrency, targetCurrency));
    }

    public List<ExchangeRateDto> getAllExchangeRates() throws SQLException, DatabaseConnectionException {
        List<ExchangeRateDto> result = new ArrayList<>();
        for (ExchangeRate exchangeRate:
                exchangeRateDAO.findAll(null)) {
            result.add(ExchangeRateMapper.mapExchangeRateToDto(exchangeRate));
        }
        return result;
    }

    public ExchangeRateDto createExchangeRate(String baseCurrencyCode, String targetCurrencyCode, double rate)
            throws SQLException, DatabaseConnectionException, CurrencyPairAlreadyExistsException, NoSuchCurrencyException {
        //Trying to get currencies corresponds to this exchange rate
        CurrencyDto baseCurrency = currencyService.getCurrency(baseCurrencyCode);
        CurrencyDto targetCurrency = currencyService.getCurrency(targetCurrencyCode);
        //Create new exchange rate
        ExchangeRateDto newExchangeRate = new ExchangeRateDto();
        newExchangeRate.setBaseCurrency(baseCurrency);
        newExchangeRate.setTargetCurrency(targetCurrency);
        newExchangeRate.setRate(rate);
        //adding the exchange rate to the database (set auto-generated id)
        newExchangeRate.setId(exchangeRateDAO.add(baseCurrency.getId(), targetCurrency.getId(), rate));
        return newExchangeRate;
    }

    public void updateExchangeRate(int baseCurrencyId, int targetCurrencyId, double rate) throws DatabaseConnectionException, SQLException{
        exchangeRateDAO.update(baseCurrencyId, targetCurrencyId, rate);
    }
}
