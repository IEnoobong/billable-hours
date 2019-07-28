package co.enoobong.billablehours.backend.data;

import java.util.ArrayList;
import java.util.List;

public class InvoiceResponse {

  private List<CompanyInvoice> companyInvoices = new ArrayList<>();

  private List<String> errors = new ArrayList<>();

  public List<CompanyInvoice> getCompanyInvoices() {
    return companyInvoices;
  }

  public void setCompanyInvoices(List<CompanyInvoice> companyInvoices) {
    this.companyInvoices = companyInvoices;
  }

  public List<String> getErrors() {
    return errors;
  }

  public void setErrors(List<String> errors) {
    this.errors = errors;
  }

  public void addCompanyInvoice(CompanyInvoice companyInvoice) {
    this.companyInvoices.add(companyInvoice);
  }
}
