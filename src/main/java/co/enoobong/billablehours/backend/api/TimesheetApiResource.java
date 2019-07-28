package co.enoobong.billablehours.backend.api;

import co.enoobong.billablehours.backend.data.InvoiceResponse;
import co.enoobong.billablehours.backend.service.TimesheetService;
import co.enoobong.billablehours.backend.validation.constraints.CheckFile;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("api/v1/timesheets")
public class TimesheetApiResource {
  private final TimesheetService timesheetService;

  public TimesheetApiResource(TimesheetService timesheetService) {
    this.timesheetService = timesheetService;
  }

  @PostMapping(value = "generate-invoice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public InvoiceResponse generateInvoiceForTimesheet(@RequestParam("timesheet") @CheckFile MultipartFile request) throws IOException {
    timesheetService.generateInvoice(request.getResource());
    return timesheetService.getInvoiceResponse();
  }
}
