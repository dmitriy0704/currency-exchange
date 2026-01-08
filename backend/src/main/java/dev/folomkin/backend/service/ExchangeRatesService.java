package dev.folomkin.backend.service;

import dev.folomkin.backend.model.Currency;
import dev.folomkin.backend.model.ExchangeRate;
import dev.folomkin.backend.repository.CurrenciesRepository;
import dev.folomkin.backend.repository.ExchangeRatesRepository;

import java.sql.SQLException;
import java.util.List;

public class ExchangeRatesService {

    private final ExchangeRatesRepository repository;


    public ExchangeRatesService(ExchangeRatesRepository repository) {
        this.repository = repository;
    }


    public List<ExchangeRate> getAllExchangeRates() throws SQLException {
        return repository.findAll();
    }


    public ExchangeRate getExchangeRateByCodes(String codes) throws SQLException {

        assert codes != null;
        if (codes.length() != 6) {
            throw new IllegalArgumentException("Длина кодовой пары должна составлять 6 символов");
        }
        String baseCode = codes.substring(0, 3);
        String targetCode = codes.substring(3);

        return repository.findByCodes(baseCode, targetCode);
    }
}
