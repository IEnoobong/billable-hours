package co.enoobong.billablehours.backend.api;

import co.enoobong.billablehours.backend.data.CompanyInvoice;
import co.enoobong.billablehours.backend.service.TimesheetService;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static co.enoobong.billablehours.util.TestUtil.CSV_ERROR_MESSAGE;
import static co.enoobong.billablehours.util.TestUtil.companyInvoice;
import static co.enoobong.billablehours.util.TestUtil.invoiceResponse;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TimesheetApiResource.class)
public class TimesheetApiResourceTest {

  @MockBean
  private TimesheetService timesheetService;

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void givenTimesheetDataShouldGenerateInvoice() throws Exception {
    willDoNothing().given(timesheetService).generateInvoice(any(Resource.class));
    given(timesheetService.getInvoiceResponse()).willReturn(invoiceResponse());

    final InputStream timesheetStream = getClass().getClassLoader().getResourceAsStream("timesheet.csv");
    final MockMultipartFile timesheetFile = new MockMultipartFile("timesheet", "timesheet.csv", "text/csv", timesheetStream);

    final CompanyInvoice companyInvoice = companyInvoice();
    mockMvc.perform(multipart("/api/v1/timesheets/generate-invoice")
            .file(timesheetFile)
            .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.companyInvoices[0].companyName").value(companyInvoice.getCompanyName()))
            .andExpect(jsonPath("$.companyInvoices[0].totalCost").value(companyInvoice.getTotalCost()))
            .andExpect(jsonPath("$.errors[0]").value(CSV_ERROR_MESSAGE));
  }

  @Test
  public void givenInvalidTimesheetDataShouldReturnBadRequest() throws Exception {
    mockMvc.perform(multipart("/api/v1/timesheets/generate-invoice")
            .file(new MockMultipartFile("timesheet", "timesheet.csv".getBytes(StandardCharsets.UTF_8)))
            .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.description").value(containsString("errors in your request")))
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors").isNotEmpty());
  }
}
