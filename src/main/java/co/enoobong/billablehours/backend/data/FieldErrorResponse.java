package co.enoobong.billablehours.backend.data;

public class FieldErrorResponse {
  private String fieldName;
  private String message;

  public FieldErrorResponse(String fieldName, String message) {
    this.fieldName = fieldName;
    this.message = message;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
