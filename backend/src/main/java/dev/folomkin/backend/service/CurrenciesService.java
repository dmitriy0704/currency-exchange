package dev.folomkin.backend.service;

import dev.folomkin.backend.exception.AlreadyExistsException;
import dev.folomkin.backend.exception.ConflictException;
import dev.folomkin.backend.exception.NotFoundException;
import dev.folomkin.backend.model.Currency;
import dev.folomkin.backend.repository.CurrenciesRepository;
import jakarta.ws.rs.core.Response;

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
        Currency currency = null;
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Национальность валюты обязательна");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Код валюты обязателен");
        }

        String normalizedCode = code.trim().toUpperCase();
        String normalizedName = name.trim();
        String normalizedSign = sign.trim();

        try {
            // Пытаемся найти валюту по коду
            currenciesRepository.findByCode(normalizedCode);

            // Если метод не бросил исключение — валюта уже существует
            throw new AlreadyExistsException("Валюта с кодом " + normalizedCode + " уже существует");

        } catch (NotFoundException e) {
            // Валюты нет — можно создавать новую
            return currenciesRepository.save(normalizedName, normalizedCode, rub_rate, normalizedSign);
        }
    }
}
