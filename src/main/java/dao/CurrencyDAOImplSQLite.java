package dao;

import db.CurrencyDbClient;
import exceptions.CurrencyAlreadyExistsException;
import exceptions.DatabaseConnectionException;
import model.Currency;
import org.sqlite.SQLiteDataSource;
import org.sqlite.SQLiteException;
import utils.DBConnectionManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDAOImplSQLite implements CurrencyDAO{
    private final CurrencyDbClient dbClient;
    private static final String SELECT_ALL = "SELECT * FROM currencies";
    private static final String SELECT_BY_CODE = "SELECT * FROM currencies WHERE code = '%s'";
    private static final String INSERT_DATA = "INSERT INTO currencies VALUES(NULL, '%s', '%s', '%s')";

    public CurrencyDAOImplSQLite() {
        this.dbClient = new CurrencyDbClient();
    }

    @Override
    public void add(Currency currency) throws CurrencyAlreadyExistsException, DatabaseConnectionException {
        try {
           dbClient.run(String.format(INSERT_DATA, currency.getCode(), currency.getFullName(), currency.getSign()));
        }catch (SQLiteException e) {
            throw new CurrencyAlreadyExistsException("Валюта с таким кодом уже существует");
        }catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }
    @Override
    public Optional<List<Currency>> getList() throws SQLException, DatabaseConnectionException {
        return this.dbClient.selectForList(SELECT_ALL);
    }
    @Override
    public Optional<Currency> getByCode(String code) throws SQLException, DatabaseConnectionException{
        List<Currency> currency = this.dbClient.selectForList(String.format(SELECT_BY_CODE, code)).orElseGet(ArrayList::new);
        if(currency.isEmpty())
            return Optional.empty();
        else return Optional.of(currency.get(0));
    }
}
