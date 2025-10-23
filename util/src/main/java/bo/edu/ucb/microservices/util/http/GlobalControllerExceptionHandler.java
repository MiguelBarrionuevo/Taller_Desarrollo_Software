package bo.edu.ucb.microservices.util.http;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import bo.edu.ucb.microservices.util.exceptions.InvalidInputException;
import bo.edu.ucb.microservices.util.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    // ------------------- Manejo NotFoundException -------------------
    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public @ResponseBody HttpErrorInfo handleNotFoundExceptions(ServerWebExchange exchange, NotFoundException ex) {
        return createHttpErrorInfo(NOT_FOUND, exchange.getRequest().getPath().pathWithinApplication().value(), ex.getMessage());
    }

    // ------------------- Manejo InvalidInputException -------------------
    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(InvalidInputException.class)
    public @ResponseBody HttpErrorInfo handleInvalidInputException(ServerWebExchange exchange,
                                                                   InvalidInputException ex) {
        return createHttpErrorInfo(UNPROCESSABLE_ENTITY, exchange.getRequest().getPath().pathWithinApplication().value(), ex.getMessage());
    }

    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(WebExchangeBindException.class)
    public @ResponseBody HttpErrorInfo handleWebFluxValidationException(ServerWebExchange exchange,
                                                                        WebExchangeBindException ex) {
        String message = ex.getAllErrors()
                .stream()
                .map(err -> err.getDefaultMessage())
                .reduce((m1, m2) -> m1 + "; " + m2)
                .orElse("Entrada inválida");

        return createHttpErrorInfo(UNPROCESSABLE_ENTITY,
                exchange.getRequest().getPath().pathWithinApplication().value(),
                message);
    }

    // ------------------- Método auxiliar -------------------
    private HttpErrorInfo createHttpErrorInfo(HttpStatus status, String path, String message) {
        LOGGER.debug("Returning HTTP status: {} for path: {}, message: {}", status, path, message);
        return new HttpErrorInfo(status, path, message);
    }
}
