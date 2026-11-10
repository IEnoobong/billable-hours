package co.enoobong.billablehours.backend.service;

import co.enoobong.billablehours.backend.data.InvoiceResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;

import static co.enoobong.billablehours.backend.config.BeanConfig.APP_CSV_FORMAT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

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

  static ByteArrayResource csv(String rows) {
    return new ByteArrayResource((HEADER + rows).getBytes(StandardCharsets.UTF_8));
  }
}
