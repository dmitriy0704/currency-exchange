package dev.folomkin.backend.resources;

import dev.folomkin.backend.repository.CurrenciesRepository;
import dev.folomkin.backend.repository.ExchangeRatesRepository;
import dev.folomkin.backend.service.ExchangeRatesService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;

@Path("/exchangeRate")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExchangeRateResource {

    @Context
    private ServletContext context;

    private ExchangeRatesService service;


    @PostConstruct
    public void init() {
        ExchangeRatesRepository repository = new ExchangeRatesRepository(context);
        this.service = new ExchangeRatesService(repository);

    }


    @GET
    @Path("/{codes}")
    public Response getExchangeRate(@PathParam("codes") String codes) {

//        if (baseCode == null || targetCode == null ||
//                baseCode.length() != 3 || targetCode.length() != 3) {
//            return Response.status(Response.Status.BAD_REQUEST)
//                    .entity("{\"error\": \"Коды валют должны состоять из 3 букв\"}")
//                    .build();
//        }
//
//        baseCode = baseCode.toUpperCase();
//        targetCode = targetCode.toUpperCase();
//
//        if (baseCode.equals(targetCode)) {
//            return Response.status(Response.Status.BAD_REQUEST)
//                    .entity("{\"error\": \"Базовая и целевая валюты не могут быть одинаковыми\"}")
//                    .build();
//        }

        try {
            return Response.ok(service.getExchangeRateByCodes(codes)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(400).entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @GET
    @Path("")
    public Response getExchangeRateWithoutCodes() {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Код валютной пары не должны быть пустыми\"}")
                .build();
    }
}
