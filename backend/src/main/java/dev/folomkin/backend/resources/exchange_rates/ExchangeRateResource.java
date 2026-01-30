package dev.folomkin.backend.resources.exchange_rates;

import dev.folomkin.backend.repository.ExchangeRatesRepository;
import dev.folomkin.backend.service.ExchangeRatesService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
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
        try {
            return Response.ok(service.getExchangeRateByCodes(codes)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(400).entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Метод нужен для ситуации,
     * когда в /exchangeRate/USDEUR - коды валют отсутствовать
     */

    @GET
    @Path("")
    public Response getExchangeRateWithoutCodes() {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"Код валютной пары не должны быть пустыми\"}")
                .build();
    }


    /**
     * PATCH /exchangeRate/{codes}
     * Тело запроса: x-www-form-urlencoded
     * Поле: rate=новое_значение
     * Обновляет курс для существующей пары, например USD/EUR
     */

    @PATCH
    @Path("/{codes}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateExchangeRate(
            @PathParam("codes") String codes,
            @FormParam("rate") BigDecimal newRate) {
        try {
            return Response.ok(service.updateExchangeRate(codes, newRate)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(400).entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
