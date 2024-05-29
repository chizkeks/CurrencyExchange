package dao;

import model.Currency;
import model.ExchangeRate;
import org.sqlite.SQLiteDataSource;
import servlets.CurrenciesServlet;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDAOImplSQLite implements ExchangeRateDAO{
    private final ExchangeRateDbClient dbClient;
    private static final String CONNECTION_URL;
    private static final String SELECT_ALL = "SELECT er.id as exchange_rate_id, c1.id as basecur_id, c1.fullname as basecur_fullname," +
            " c1.code as basecur_code, c1.sign as basecur_sign," +
            " c2.id as targetcur_id, c2.fullname as targetcur_fullname," +
            " c2.code as targetcur_code, c2.sign as targetcur_sign, er.rate" +
            " FROM exchange_rates er INNER JOIN currencies c1 on er.basecurrencyid = c1.id" +
            " INNER JOIN currencies c2 on er.targetcurrencyid = c2.id;";

    private static final String INSERT_DATA = "INSERT INTO exchange_rates VALUES(NULL, '%s', '%s', %.2f);";

    private static final String SELECT_BY_CURRENCY_PAIR = "SELECT er.id as exchange_rate_id, c1.id as basecur_id, c1.fullname as basecur_fullname," +
            " c1.code as basecur_code, c1.sign as basecur_sign," +
            " c2.id as targetcur_id, c2.fullname as targetcur_fullname," +
            " c2.code as targetcur_code, c2.sign as targetcur_sign, er.rate" +
            " FROM exchange_rates er INNER JOIN currencies c1 on er.basecurrencyid = c1.id" +
            " INNER JOIN currencies c2 on er.targetcurrencyid = c2.id" +
            " WHERE c1.code = '%s' AND c2.code = '%s';";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        CONNECTION_URL = "jdbc:sqlite:" + CurrenciesServlet.class.getClassLoader().getResource("db/currency_exchange_db");
    }

    public ExchangeRateDAOImplSQLite() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(CONNECTION_URL);
        this.dbClient = new ExchangeRateDbClient(dataSource);
    }

    @Override
    public boolean add(ExchangeRate rate) {
        return dbClient.run(String.format(INSERT_DATA, rate.getBaseCurrency().getCode(), rate.getTargetCurrency().getCode(), rate.getRate()));
    }

    @Override
    public Optional<List<ExchangeRate>> getList() {
        return this.dbClient.selectForList(SELECT_ALL);
    }

    public Optional<ExchangeRate> getByCurrencyPair(String baseCurrencyCode, String targetCurrencyCode) {
        List<ExchangeRate> result = this.dbClient.selectForList(String.format(SELECT_BY_CURRENCY_PAIR, baseCurrencyCode, targetCurrencyCode)).orElseGet(ArrayList::new);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }
}
