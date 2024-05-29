package dao;

import model.Currency;
import org.sqlite.SQLiteDataSource;
import servlets.CurrenciesServlet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDAOImplSQLite implements CurrencyDAO{
    private final CurrencyDbClient dbClient;
    private static final String CONNECTION_URL;
    private static final String SELECT_ALL = "SELECT * FROM currencies";
    private static final String SELECT_BY_ID = "SELECT * FROM currencies WHERE code = %d";
    private static final String INSERT_DATA = "INSERT INTO currencies VALUES(NULL, '%s', '%s', '%s')";


    static {
        try {
            Class.forName("org.sqlite.JDBC");
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        CONNECTION_URL = "jdbc:sqlite:" + CurrenciesServlet.class.getClassLoader().getResource("db/currency_exchange_db");
    }

    public CurrencyDAOImplSQLite() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(CONNECTION_URL);
        this.dbClient = new CurrencyDbClient(dataSource);
    }

    @Override
    public boolean add(Currency currency) {
        return dbClient.run(String.format(INSERT_DATA, currency.getCode(), currency.getFullName(), currency.getSign()));
    }
    @Override
    public Optional<List<Currency>> getList() {
        return this.dbClient.selectForList(SELECT_ALL);
    }
    @Override
    public Optional<Currency> getByCode(String code) {
        List<Currency> currency = this.dbClient.selectForList(SELECT_BY_ID).orElseGet(ArrayList::new);
        if(currency.isEmpty())
            return Optional.empty();
        else return Optional.of(currency.get(0));
    }
}
