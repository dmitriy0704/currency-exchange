package dev.folomkin.backend.service;

import dev.folomkin.backend.exception.AlreadyExistsException;
import dev.folomkin.backend.exception.NotFoundException;
import dev.folomkin.backend.exception.ValidationException;
import dev.folomkin.backend.model.Currency;
import dev.folomkin.backend.model.ExchangeRate;
import dev.folomkin.backend.repository.CurrenciesRepository;
import dev.folomkin.backend.repository.ExchangeRatesRepository;

import java.math.BigDecimal;
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


    public ExchangeRate createExchangeRates(String baseCode, String targetCode) throws SQLException {


        if (baseCode == null || baseCode.trim().isEmpty() || baseCode.length() != 3) {
            throw new ValidationException("Код базовой валюты должен состоять из 3 букв");
        }
        if (targetCode == null || targetCode.trim().isEmpty() || targetCode.length() != 3) {
            throw new ValidationException("Код целевой валюты должен состоять из 3 букв");
        }
//        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new ValidationException("Курс должен быть положительным числом");
//        }
        if (baseCode.equalsIgnoreCase(targetCode)) {
            throw new ValidationException("Базовая и целевая валюты не могут быть одинаковыми");
        }

        try {
            repository.findByCodes(baseCode, targetCode);
            throw new AlreadyExistsException("Обменный курс уже существует");
        } catch (NotFoundException e) {
            return repository.save(baseCode, targetCode);

        }

    }
}
