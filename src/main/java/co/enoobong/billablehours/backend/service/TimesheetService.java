package co.enoobong.billablehours.backend.service;

import co.enoobong.billablehours.backend.data.CompanyInvoice;
import co.enoobong.billablehours.backend.data.Invoice;
import co.enoobong.billablehours.backend.data.InvoiceResponse;
import co.enoobong.billablehours.backend.data.Timesheet;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import static org.springframework.util.StringUtils.capitalize;

@Service
public class TimesheetService {

  private static final Logger log = LoggerFactory.getLogger(TimesheetService.class);
  private static final BigDecimal MINUTES_PER_HOUR = BigDecimal.valueOf(60);
  private final CsvService csvService;

  public TimesheetService(CsvService csvService) {
    this.csvService = csvService;
  }

  public InvoiceResponse generateInvoice(Resource timesheetResource) throws IOException {
    final List<CSVRecord> timesheetData = csvService.read(timesheetResource.getInputStream());
    final List<String> errors = new ArrayList<>();

    // sequential on purpose: toTimesheet appends to errors, and rows are tiny, so a
    // parallel stream only bought lost errors and a random error order
    final Map<String, List<Timesheet>> projectToTimesheets = new LinkedHashMap<>();
    for (final CSVRecord csvRecord : timesheetData) {
      final Timesheet timesheet = toTimesheet(csvRecord, errors);
      if (timesheet != null) {
        projectToTimesheets.computeIfAbsent(timesheet.getProject(), project -> new ArrayList<>()).add(timesheet);
      }
    }

    final InvoiceResponse invoiceResponse = new InvoiceResponse();
    invoiceResponse.setErrors(errors);
    projectToTimesheets.forEach(
            (key, value) -> {
              final List<Invoice> invoices =
                      value.stream().map(this::toInvoice).collect(Collectors.toList());
              final BigDecimal totalCost =
                      invoices.stream().map(Invoice::getCost).reduce(BigDecimal.ZERO, BigDecimal::add);
              final CompanyInvoice companyInvoice = new CompanyInvoice();
              companyInvoice.setCompanyName(key);
              companyInvoice.setInvoices(invoices);
              companyInvoice.setTotalCost(totalCost);
              invoiceResponse.addCompanyInvoice(companyInvoice);
            });
    return invoiceResponse;
  }

  private Invoice toInvoice(Timesheet timesheet) {
    // Duration.toHours() truncates, which billed 09:00-16:30 as 7 hours instead of 7.5
    final BigDecimal minutes =
            BigDecimal.valueOf(Duration.between(timesheet.getStartTime(), timesheet.getEndTime()).toMinutes());
    final BigDecimal numberOfHours = minutes.divide(MINUTES_PER_HOUR, 2, RoundingMode.HALF_UP);
    // from the exact minutes, not the rounded hours: 20 minutes at 100/h is 33.33, not 0.33 * 100
    final BigDecimal cost =
            timesheet.getBillableRate().multiply(minutes).divide(MINUTES_PER_HOUR, 2, RoundingMode.HALF_UP);

    return new Invoice(timesheet.getEmployeeId(), numberOfHours, timesheet.getBillableRate(), cost);
  }

  private Timesheet toTimesheet(CSVRecord csvRecord, List<String> errors) {
    try {
      final long employeeId = Long.parseLong(csvRecord.get("Employee ID"));
      final BigDecimal billableRate = new BigDecimal(csvRecord.get("Billable Rate (per hour)"));
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
      // getParser().getCurrentLineNumber() is where the parser is now, i.e. the end of the
      // file once all records are read. +1 because line 1 is the header.
      final long lineNumber = csvRecord.getRecordNumber() + 1;
      final String errorMessage =
              "An error occurred at line %d when processing timesheet. Please verify that %s, "
                      + "is in the accepted format";
      errors.add(String.format(errorMessage, lineNumber, csvRecord.toMap()));
      return null;
    }
  }

}
