package dev.folomkin.backend.service;

import dev.folomkin.backend.exception.ConflictException;
import dev.folomkin.backend.model.Currency;
import dev.folomkin.backend.repository.CurrenciesRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class CurrenciesService {

    private final CurrenciesRepository currenciesRepository;

    public CurrenciesService(CurrenciesRepository currenciesRepository) {
        this.currenciesRepository = currenciesRepository;
    }

    public List<Currency> getAllCurrencies() throws SQLException {
        return currenciesRepository.findAll();
    }

    public Currency getCurrencyByCode(String code) throws SQLException {
        return currenciesRepository.findByCode(code);
    }


    public Currency createCurrency(String name, String code, BigDecimal rub_rate, String sign) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Национальность валюты обязательна");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Код валюты обязателен");
        }

        // Проверка на дубликат
        if (currenciesRepository.findByCode(code) != null) {
            throw new ConflictException("Валюта с кодом '" + code + "' уже существует");
        }

        return currenciesRepository.save(name, code, rub_rate, sign);
    }
}
