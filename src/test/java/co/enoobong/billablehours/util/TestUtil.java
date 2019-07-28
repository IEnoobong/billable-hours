package co.enoobong.billablehours.util;

import co.enoobong.billablehours.backend.data.CompanyInvoice;
import co.enoobong.billablehours.backend.data.Invoice;
import co.enoobong.billablehours.backend.data.InvoiceResponse;
import java.util.Collections;

public final class TestUtil {

  public static final String CSV_ERROR_MESSAGE = "An error occurred at line 17 when processing timesheet. Please " +
          "verify that {employeeId=Yes, billableRate=No, project=Happy, date=20, startTime=10, endTime=5, =2}, is in the accepted format";

  private TestUtil() {

  }

  public static InvoiceResponse invoiceResponse() {
    final InvoiceResponse invoiceResponse = new InvoiceResponse();
    invoiceResponse.setErrors(Collections.singletonList(CSV_ERROR_MESSAGE));
    invoiceResponse.addCompanyInvoice(companyInvoice());
    return invoiceResponse;
  }

  public static CompanyInvoice companyInvoice() {
    final CompanyInvoice companyInvoice = new CompanyInvoice();
    companyInvoice.setCompanyName("Google");
    companyInvoice.setTotalCost(1024.00D);
    companyInvoice.setInvoices(Collections.singletonList(invoice()));
    return companyInvoice;
  }

  private static Invoice invoice() {
    return new Invoice(1L, 8L, 300D, 50D);
  }
}
