package dev.folomkin.backend.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)  // 404
                .entity("{\"error\": \"" + exception.getMessage() + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}