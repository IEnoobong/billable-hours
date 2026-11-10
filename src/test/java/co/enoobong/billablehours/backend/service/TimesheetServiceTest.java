package co.enoobong.billablehours.backend.service;

import co.enoobong.billablehours.backend.data.Invoice;
import co.enoobong.billablehours.backend.data.InvoiceResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;

import static co.enoobong.billablehours.backend.config.BeanConfig.APP_CSV_FORMAT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

public class TimesheetServiceTest {

  private static final String HEADER = "Employee ID,Billable Rate (per hour),Project,Date,Start Time,End Time\n";

  private TimesheetService timesheetService;

  @Before
  public void setup() {
    timesheetService = new TimesheetService(new CsvService(APP_CSV_FORMAT));
  }

  @Test
  public void eachCallReturnsItsOwnInvoiceSoConcurrentUsersDoNotSeeEachOthers() throws IOException {
    final InvoiceResponse first = timesheetService.generateInvoice(csv("1,100,Google,2019-07-01,09:00,17:00\n"));
    timesheetService.generateInvoice(csv("2,100,Facebook,2019-07-01,09:00,17:00\n"));

    assertThat(first.getCompanyInvoices(), hasSize(1));
    assertThat(first.getCompanyInvoices().get(0).getCompanyName(), is("Google"));
  }

  @Test
  public void reportsTheLineOfTheRowThatFailedNotTheLastLineOfTheFile() throws IOException {
    final InvoiceResponse response = timesheetService.generateInvoice(csv(
            "1,100,Google,2019-07-01,09:00,17:00\n"     // line 2
                    + "x,100,Google,2019-07-01,09:00,17:00\n" // line 3: bad employee id
                    + "2,100,Google,2019-07-01,09:00,17:00\n" // line 4
    ));

    assertThat(response.getErrors(), hasSize(1));
    assertThat(response.getErrors().get(0), startsWith("An error occurred at line 3 "));
  }

  @Test
  public void keepsEveryRowErrorInFileOrder() throws IOException {
    final int rows = 20_000;
    final StringBuilder body = new StringBuilder();
    for (int i = 0; i < rows; i++) {
      // even rows are valid, odd rows have a bad employee id
      body.append(i % 2 == 0 ? "1" : "x").append(",100,Google,2019-07-01,09:00,17:00\n");
    }

    final InvoiceResponse response = timesheetService.generateInvoice(csv(body.toString()));

    assertThat(response.getErrors(), hasSize(rows / 2));
    for (int e = 0; e < rows / 2; e++) {
      // error e comes from row i = 2e + 1, which is on line i + 2 (line 1 is the header)
      assertThat(response.getErrors().get(e), startsWith("An error occurred at line " + (2 * e + 3) + " "));
    }
  }

  @Test
  public void billsPartialHours() throws IOException {
    final InvoiceResponse response = timesheetService.generateInvoice(csv("1,100,Google,2019-07-01,09:00,16:30\n"));

    final Invoice invoice = response.getCompanyInvoices().get(0).getInvoices().get(0);
    assertThat(invoice.getNumberOfHours(), is(new BigDecimal("7.50")));
    assertThat(invoice.getCost(), is(new BigDecimal("750.00")));
  }

  @Test
  public void chargesExactMinutesRatherThanRoundedHours() throws IOException {
    final InvoiceResponse response = timesheetService.generateInvoice(csv("1,100,Google,2019-07-01,09:00,09:20\n"));

    final Invoice invoice = response.getCompanyInvoices().get(0).getInvoices().get(0);
    assertThat(invoice.getNumberOfHours(), is(new BigDecimal("0.33")));
    assertThat(invoice.getCost(), is(new BigDecimal("33.33")));
  }

  @Test
  public void totalsAddUpToTheCent() throws IOException {
    // 0.1 + 0.2 is 0.30000000000000004 as doubles
    final InvoiceResponse response = timesheetService.generateInvoice(csv(
            "1,0.10,Google,2019-07-01,09:00,10:00\n"
                    + "2,0.20,Google,2019-07-01,09:00,10:00\n"));

    assertThat(response.getCompanyInvoices().get(0).getTotalCost(), is(new BigDecimal("0.30")));
  }

  static ByteArrayResource csv(String rows) {
    return new ByteArrayResource((HEADER + rows).getBytes(StandardCharsets.UTF_8));
  }
}
