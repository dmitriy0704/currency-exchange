package dev.folomkin.backend.resources;

import dev.folomkin.backend.repository.CurrenciesRepository;
import dev.folomkin.backend.service.CurrenciesService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
        if (code == null || code.trim().isEmpty() || code.length() != 3) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Код валюты должен состоять из 3 букв\"}")
                    .build();
        }

        code = code.trim().toUpperCase();  // обычно коды хранятся в верхнем регистре

        try {
            return Response.ok(currenciesService.getCurrencyByCode(code)).build();
        } catch (SQLException e) {
            return Response.status(500).build();
        }
    }
}
