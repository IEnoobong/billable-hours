package co.enoobong.billablehours.backend.validation.validators;

import co.enoobong.billablehours.backend.validation.constraints.CheckFile;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class CheckFileValidator implements ConstraintValidator<CheckFile, MultipartFile> {

  private long maxSize;
  private String[] fileTypes;

  @Override
  public void initialize(CheckFile constraintAnnotation) {
    this.maxSize = constraintAnnotation.maxSize();
    this.fileTypes = constraintAnnotation.fileTypes();
  }

  @Override
  public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
    if (file == null) {
      return false;
    }
    if (file.getSize() > maxSize) {
      return false;
    }

    for (String fileType : fileTypes) {
      if (fileType.trim().equalsIgnoreCase(file.getContentType())) {
        return true;
      }
    }

    return false;
  }
}
