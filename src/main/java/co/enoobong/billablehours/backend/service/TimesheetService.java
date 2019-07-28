package co.enoobong.billablehours.backend.service;

import co.enoobong.billablehours.backend.data.CompanyInvoice;
import co.enoobong.billablehours.backend.data.Invoice;
import co.enoobong.billablehours.backend.data.InvoiceResponse;
import co.enoobong.billablehours.backend.data.Timesheet;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import static org.springframework.util.StringUtils.capitalize;

@Component
@SessionScope
public class TimesheetService {

  private static final Logger log = LoggerFactory.getLogger(TimesheetService.class);
  private final CsvService csvService;
  private InvoiceResponse generatedInvoiceResponse;

  public TimesheetService(CsvService csvService) {
    this.csvService = csvService;
  }

  public void generateInvoice(Resource timesheetResource) throws IOException {
    final List<CSVRecord> timesheetData = csvService.read(timesheetResource.getInputStream());
    if (timesheetData.isEmpty()) {
      new InvoiceResponse();
    }
    final List<String> errors = new ArrayList<>();

    final Map<String, List<Timesheet>> projectToTimesheets =
            timesheetData.stream()
                    .parallel()
                    .map(csvRecord -> toTimesheet(csvRecord, errors))
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(Timesheet::getProject));

    final InvoiceResponse invoiceResponse = new InvoiceResponse();
    invoiceResponse.setErrors(errors);
    projectToTimesheets.forEach(
            (key, value) -> {
              final List<Invoice> invoices =
                      value.stream().map(this::toInvoice).collect(Collectors.toList());
              final double totalCost = invoices.stream().mapToDouble(Invoice::getCost).sum();
              final CompanyInvoice companyInvoice = new CompanyInvoice();
              companyInvoice.setCompanyName(key);
              companyInvoice.setInvoices(invoices);
              companyInvoice.setTotalCost(totalCost);
              invoiceResponse.addCompanyInvoice(companyInvoice);
            });
    this.generatedInvoiceResponse = invoiceResponse;
  }

  public InvoiceResponse getInvoiceResponse() {
    return generatedInvoiceResponse;
  }

  private Invoice toInvoice(Timesheet timesheet) {
    final long numberOfHours =
            Duration.between(timesheet.getStartTime(), timesheet.getEndTime()).toHours();
    double cost = numberOfHours * timesheet.getBillableRate();

    return new Invoice(timesheet.getEmployeeId(), numberOfHours, timesheet.getBillableRate(), cost);
  }

  private Timesheet toTimesheet(CSVRecord csvRecord, List<String> errors) {
    try {
      final long employeeId = Long.parseLong(csvRecord.get("Employee ID"));
      final double billableRate =
              Double.parseDouble(csvRecord.get("Billable Rate (per hour)"));
      final String project =
              capitalize(csvRecord.get("Project")); // ensure companies of the same name are together
      final LocalDate date = LocalDate.parse(csvRecord.get("Date"));
      final LocalTime startTime = LocalTime.parse(csvRecord.get("Start Time"));
      final LocalTime endTime = LocalTime.parse(csvRecord.get("End Time"));

      if (endTime.isBefore(startTime)) {
        throw new IllegalArgumentException("End time should not be before start time");
      }

      return new Timesheet(employeeId, billableRate, project, date, startTime, endTime);
    } catch (Exception ex) {
      log.warn("Error occurred when parsing value to timesheet", ex);
      final long currentLineNumber = csvRecord.getParser().getCurrentLineNumber();
      final String errorMessage =
              "An error occurred at line %d when processing timesheet. Please verify that %s, "
                      + "is in the accepted format";
      errors.add(String.format(errorMessage, currentLineNumber, csvRecord.toMap()));
      return null;
    }
  }
}
