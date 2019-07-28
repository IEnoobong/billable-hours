package co.enoobong.billablehours.backend.config;

import co.enoobong.billablehours.BillableHoursApplication;
import java.util.ArrayList;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Import({BeanValidatorPluginsConfiguration.class})
public class SwaggerConfig {

  private static ApiInfo generateApiInfo() {
    return new ApiInfo(
            "Billable Hours Web App",
            "Billable hours is a web application developed with Vaadin and Spring Boot that accepts a timesheet (in csv "
                    + "format) as input and automatically generates invoices for each company",
            "0.0.1",
            "urn:tos",
            new Contact(
                    "Ibanga Enoobong",
                    "https://www.linkedin.com/in/ienoobong/",
                    "ibangaenoobong@yahoo.com"),
            "Apache 2.0",
            "http://www.apache.org/licenses/LICENSE-2.0",
            new ArrayList<>());
  }

  @Bean
  public Docket apiDoc() {
    return new Docket(DocumentationType.SPRING_WEB)
            .apiInfo(generateApiInfo())
            .select()
            .apis(
                    RequestHandlerSelectors.basePackage(
                            BillableHoursApplication.class.getPackage().getName()))
            .paths(PathSelectors.any())
            .build();
  }
}
