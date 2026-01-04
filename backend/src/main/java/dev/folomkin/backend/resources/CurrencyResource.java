package dev.folomkin.backend.resources;

import dev.folomkin.backend.exception.ConflictException;
import dev.folomkin.backend.model.Currency;
import dev.folomkin.backend.model.User;
import dev.folomkin.backend.repository.CurrenciesRepository;
import dev.folomkin.backend.service.CurrenciesService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.sql.SQLException;

@Path("/currency")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class CurrencyResource {

    @Context
    private ServletContext context;

    private CurrenciesService currenciesService;


    @PostConstruct
    public void init() {
        CurrenciesRepository repository = new CurrenciesRepository(context);
        this.currenciesService = new CurrenciesService(repository);
    }

    @GET
    @Path("/{code}")
    public Response getCurrencyByCode(@PathParam("code") String code) {

        // Валидация кода (опционально, но рекомендуется)
        if (code == null || code.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Код валюты не валиден\"}")
                    .build();
        }

        code = code.trim().toUpperCase();  // обычно коды хранятся в верхнем регистре

        try {
            return Response.ok(currenciesService.getCurrencyByCode(code)).build();
        } catch (SQLException e) {
            return Response.status(500).build();
        }
    }


    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCurrency(
            @FormParam("name") String name,
            @FormParam("code") String code,
            @FormParam("rub_rate") BigDecimal rub_rate,
            @FormParam("sign") String sign
    ) {
        try {
            Currency currency = currenciesService.createCurrency(name, code, rub_rate, sign);
            return Response.status(201).entity(currency).build();
        } catch (IllegalArgumentException e) {
            return Response.status(400).entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        } catch (ConflictException e) {
            return Response.status(409).entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(500).entity("{\"error\": \"Ошибка базы данных\"}").build();
        }
    }


}
