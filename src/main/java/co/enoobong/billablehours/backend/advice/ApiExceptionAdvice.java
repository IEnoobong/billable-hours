package co.enoobong.billablehours.backend.advice;

import co.enoobong.billablehours.backend.data.ErrorResponse;
import co.enoobong.billablehours.backend.data.FieldErrorResponse;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@ResponseBody
@RestControllerAdvice
public class ApiExceptionAdvice {

  private static final Logger log = LoggerFactory.getLogger(ApiExceptionAdvice.class);

  private final HttpServletRequest httpServletRequest;

  public ApiExceptionAdvice(HttpServletRequest httpServletRequest) {
    this.httpServletRequest = httpServletRequest;
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ErrorResponse handleUnsupportedMediaTypeException(HttpMediaTypeNotSupportedException ex) {
    log.warn("Unsupported media type. Path {}", httpServletRequest.getServletPath(), ex);

    return new ErrorResponse(ex.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MissingServletRequestPartException.class)
  public ErrorResponse handleMissingRequestPartException(MissingServletRequestPartException ex) {
    log.warn("Missing request part. Path {}", httpServletRequest.getServletPath(), ex);

    return new ErrorResponse(ex.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ErrorResponse handleUploadSizeExceededException(MaxUploadSizeExceededException ex) {
    log.warn("Upload size exceeded. Path {}", httpServletRequest.getServletPath(), ex);
    long permittedSize = ex.getMaxUploadSize();
    final Throwable cause = ex.getCause();
    if (cause != null && cause.getCause() instanceof FileUploadBase.SizeException) {
      permittedSize = ((FileUploadBase.SizeException) cause.getCause()).getPermittedSize();
    }

    final String errorMessage =
            "File size is too large. Maximum file size is " + permittedSize + " bytes";

    return new ErrorResponse(errorMessage);
  }

  @ExceptionHandler({ConstraintViolationException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
    log.warn("Constraint violated. Path {}", httpServletRequest.getServletPath(), ex);
    final ErrorResponse errorResponse = new ErrorResponse("There are errors in your request");

    final List<FieldErrorResponse> fieldErrorResponses = new ArrayList<>();
    for (ConstraintViolation<?> constraintViolation : ex.getConstraintViolations()) {
      final Object invalidValue = constraintViolation.getInvalidValue();
      if (invalidValue instanceof MultipartFile) {
        final FieldErrorResponse fieldErrorResponse =
                new FieldErrorResponse(
                        ((MultipartFile) invalidValue).getOriginalFilename(),
                        constraintViolation.getMessage());
        fieldErrorResponses.add(fieldErrorResponse);
      }
    }
    errorResponse.setErrors(fieldErrorResponses);
    return errorResponse;
  }

  @ExceptionHandler({MethodArgumentNotValidException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
    log.warn("Method argument not valid. Path {}", httpServletRequest.getServletPath(), ex);
    return bindingResultToErrorResponse(ex.getBindingResult());
  }

  @ExceptionHandler(BindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleServletRequestBindingException(BindException ex) {
    log.warn("Binding exception. Path {}", httpServletRequest.getServletPath(), ex);
    return bindingResultToErrorResponse(ex.getBindingResult());
  }

  private ErrorResponse bindingResultToErrorResponse(BindingResult result) {
    ErrorResponse response = new ErrorResponse("There are errors in your request");
    List<FieldError> errorList = result.getFieldErrors();
    List<FieldErrorResponse> errors = new ArrayList<>();
    for (FieldError fieldError : errorList) {
      errors.add(
              new FieldErrorResponse(
                      fieldError.getField(), fieldError.isBindingFailure()
                      ? "Invalid data format" : fieldError.getDefaultMessage()));
    }
    response.setErrors(errors);
    return response;
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleGeneralException(Exception ex) {
    log.warn("An unexpected error occurred. Path {}", httpServletRequest.getServletPath(), ex);

    return new ErrorResponse("We could not process your request");
  }
}
