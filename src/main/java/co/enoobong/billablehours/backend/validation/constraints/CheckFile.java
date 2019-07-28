package co.enoobong.billablehours.backend.validation.constraints;

import co.enoobong.billablehours.backend.validation.validators.CheckFileValidator;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * The annotated element must not be {@code null} and meet specified requirements of {@code maxSize}
 * {@code fileTypes}. Supported types are:
 *
 * <ul>
 *   <li>{@code MultipartFile (file size and content type are evaluated)}
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckFileValidator.class)
public @interface CheckFile {

  /**
   * @return max file size in bytes
   */
  long maxSize() default 1024;

  /**
   * @return allowed file types
   */
  String[] fileTypes() default {"text/csv"};

  /**
   * @return error message when constraints are violated
   */
  String message() default "File fails to meet requirements. Must be a .csv file with max size 1024 bytes";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
