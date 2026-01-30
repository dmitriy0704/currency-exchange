package dev.folomkin.backend.resources.currency_exchange;


import dev.folomkin.backend.model.ConversionResultDto;
import dev.folomkin.backend.repository.ExchangeRatesRepository;
import dev.folomkin.backend.service.CurrencyExchangeService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;

@Path("/exchange")
@Produces(MediaType.APPLICATION_JSON)
public class CurrencyExchangeResource {

    @Context
    private ServletContext context;


    private CurrencyExchangeService service;

    @PostConstruct
    public void init() {
        ExchangeRatesRepository repository = new ExchangeRatesRepository(context);
        this.service = new CurrencyExchangeService(repository);
    }


    @GET
    public Response convertCurrency(
            @QueryParam("from") String fromCode,
            @QueryParam("to") String toCode,
            @QueryParam("amount") BigDecimal amount) {


        // Валидация параметров
        if (fromCode == null || fromCode.trim().isEmpty() || fromCode.length() != 3) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Параметр 'from' должен быть 3-буквенным кодом валюты\"}")
                    .build();
        }
        if (toCode == null || toCode.trim().isEmpty() || toCode.length() != 3) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Параметр 'to' должен быть 3-буквенным кодом валюты\"}")
                    .build();
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Параметр 'amount' должен быть положительным числом\"}")
                    .build();
        }
        if (fromCode.equalsIgnoreCase(toCode)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Базовая и целевая валюты не могут быть одинаковыми\"}")
                    .build();
        }

        ConversionResultDto conversionResultDto =
                service.convertCurrency(fromCode, toCode, amount);
        try {
            return Response.ok(conversionResultDto).build();
        } catch (NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND)
                            .entity(e.getMessage())
                            .build();
        }
    }
}
