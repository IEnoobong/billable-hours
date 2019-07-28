package co.enoobong.billablehours.backend.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

  private String description;

  private List<FieldErrorResponse> errors;

  public ErrorResponse(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<FieldErrorResponse> getErrors() {
    return errors;
  }

  public void setErrors(List<FieldErrorResponse> errors) {
    this.errors = errors;
  }
}
