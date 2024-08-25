package mappers;

import dto.CurrencyDto;
import model.Currency;

public class CurrencyMapper {
    public static CurrencyDto mapCurrencyToCurrencyDto(Currency currency) {
        return new CurrencyDto(currency.getId(), currency.getCode(), currency.getFullName(), currency.getSign());
    }

    public static Currency mapCurrencyDtotoCurrency(CurrencyDto currencyDto) {
        return new Currency(currencyDto.getId(), currencyDto.getCode(), currencyDto.getFullName(), currencyDto.getSign());
    }
}
