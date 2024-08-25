package mappers;

import dto.CurrencyDto;
import dto.ExchangeRateDto;
import model.Currency;
import model.ExchangeRate;

public class ExchangeRateMapper {
    public static ExchangeRateDto mapExchangeRateToDto(ExchangeRate exchangeRate) {
        return new ExchangeRateDto(
                exchangeRate.getId(),
                CurrencyMapper.mapCurrencyToCurrencyDto(exchangeRate.getBaseCurrency()),
                CurrencyMapper.mapCurrencyToCurrencyDto(exchangeRate.getTargetCurrency()),
                exchangeRate.getRate());
    }

    public static ExchangeRate mapDtoToExchangeRate(ExchangeRateDto exchangeRateDto) {
        return new ExchangeRate(
                exchangeRateDto.getId(),
                CurrencyMapper.mapCurrencyDtotoCurrency(exchangeRateDto.getBaseCurrency()),
                CurrencyMapper.mapCurrencyDtotoCurrency(exchangeRateDto.getTargetCurrency()),
                exchangeRateDto.getRate());
    }
}
