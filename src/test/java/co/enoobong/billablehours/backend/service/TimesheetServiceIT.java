package co.enoobong.billablehours.backend.service;

import co.enoobong.billablehours.backend.data.CompanyInvoice;
import co.enoobong.billablehours.backend.data.InvoiceResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.springframework.core.io.ClassPathResource;

import static co.enoobong.billablehours.backend.config.BeanConfig.APP_CSV_FORMAT;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

public class TimesheetServiceIT {

  @Rule
  public ErrorCollector errorCollector = new ErrorCollector();
  private TimesheetService timesheetService;

  @Before
  public void setup() {
    final CsvService csvService = new CsvService(APP_CSV_FORMAT);
    this.timesheetService = new TimesheetService(csvService);
  }

  @Test
  public void givenTimesheetResourceWhenGenerateInvoiceShouldGenerate() throws IOException {
    final InvoiceResponse invoiceResponse = timesheetService.generateInvoice(new ClassPathResource("timesheet.csv"));

    errorCollector.checkThat("response was null", invoiceResponse, notNullValue());
    errorCollector.checkThat("company invoice count was not correct", invoiceResponse.getCompanyInvoices(), hasSize(2));

    final Map<String, CompanyInvoice> companyToInvoices = invoiceResponse.getCompanyInvoices().stream()
            .collect(Collectors.toMap(CompanyInvoice::getCompanyName, Function.identity()));
    final CompanyInvoice googleInvoices = companyToInvoices.get("Google");
    errorCollector.checkThat("google's total invoice cost was not correct", googleInvoices.getTotalCost(),
            is(new BigDecimal("17800.00")));
    errorCollector.checkThat("google's invoice count was not correct", googleInvoices.getInvoices(), hasSize(11));
    errorCollector.checkThat("error count was not correct", invoiceResponse.getErrors(), hasSize(1));
    errorCollector.checkThat("error should point at the bad row", invoiceResponse.getErrors().get(0),
            startsWith("An error occurred at line 7 "));
  }
}
