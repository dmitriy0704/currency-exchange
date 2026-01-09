package dev.folomkin.backend.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.sql.SQLException;

@Provider
public class SQLExceptionMapper implements ExceptionMapper<SQLException> {

    @Override
    public Response toResponse(SQLException exception) {
        exception.printStackTrace();  // В продакшене — логгер

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)  // 500
                .entity("{\"error\": \"Внутренняя ошибка сервера. Попробуйте позже.\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}