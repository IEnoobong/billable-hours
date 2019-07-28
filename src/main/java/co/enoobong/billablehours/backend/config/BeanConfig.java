package co.enoobong.billablehours.backend.config;

import org.apache.commons.csv.CSVFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

  public static final CSVFormat APP_CSV_FORMAT = CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim();

  @Bean
  public CSVFormat csvFormat() {
    return APP_CSV_FORMAT;
  }
}
