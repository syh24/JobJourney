package CSE4186.interview.exception;


import CSE4186.interview.utils.ApiUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        ApiUtil.ApiErrorResult<String> error = ApiUtil.error(statusCode.value(), "알 수 없는 오류 발생");
        return super.handleExceptionInternal(ex, error, headers, statusCode, request);
    }


    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<?> handleNotFoundException(Exception e) {
        return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body(ApiUtil.error(HttpServletResponse.SC_NOT_FOUND, e.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).body(ApiUtil.error(HttpServletResponse.SC_BAD_REQUEST, "Method Argument Not Valid"));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleNormalException(Exception e) {
        return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body(ApiUtil.error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "알 수 없는 오류 발생"));
    }
}
