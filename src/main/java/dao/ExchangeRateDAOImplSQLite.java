package dao;

import db.ExchangeRateDbClient;
import exceptions.DatabaseConnectionException;
import model.ExchangeRate;
import org.sqlite.SQLiteDataSource;
import org.sqlite.SQLiteException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDAOImplSQLite implements ExchangeRateDAO{
    private final ExchangeRateDbClient dbClient;
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

    private static final String UPDATE_RATE_BY_CURRENCY_PAIR_CODE = "UPDATE exchange_rates SET rate = %.2f " +
            "FROM (SELECT id FROM currencies WHERE code = %s) AS baseCur, (SELECT id FROM currencies WHERE code = %s) AS targetCur" +
            "WHERE basecurrencyid = baseCur.id AND targetcurrencyid = targetCur.id;";
    private static final String UPDATE_RATE_BY_CURRENCY_PAIR_ID = "UPDATE exchange_rates SET rate = %.2f " +
            "WHERE basecurrencyid = %d AND targetcurrencyid = %d;";

    //private static final String UPDATE_RATE_BY_CURRENCY_PAIR_CODE = ""

    public ExchangeRateDAOImplSQLite() {
        this.dbClient = new ExchangeRateDbClient();
    }

    @Override
    public void add(long baseCurrencyId, long targetCurrencyId, double rate) throws SQLException, DatabaseConnectionException, SQLiteException {
        dbClient.run(String.format(INSERT_DATA, baseCurrencyId, targetCurrencyId, rate));
    }

    @Override
    public Optional<List<ExchangeRate>> getList() throws SQLException {
        return this.dbClient.selectForList(SELECT_ALL);
    }

    @Override
    public Optional<ExchangeRate> getByCurrencyPairCode(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        List<ExchangeRate> result = this.dbClient.selectForList(String.format(SELECT_BY_CURRENCY_PAIR, baseCurrencyCode, targetCurrencyCode)).orElseGet(ArrayList::new);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }
    @Override
    public void updateRateByCurrencyPairId(long baseCurrencyId, long targetCurrencyId, double rate) throws DatabaseConnectionException {
        try {
            dbClient.run(String.format(UPDATE_RATE_BY_CURRENCY_PAIR_ID, rate, baseCurrencyId, targetCurrencyId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
