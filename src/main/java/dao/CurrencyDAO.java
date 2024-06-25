package dao;

import model.Currency;

import java.util.List;
import java.util.Optional;

public interface CurrencyDAO {
    public boolean add(Currency currency);
    public Optional<List<Currency>> getList();
    public Optional<Currency> getByCode(String code);
}
