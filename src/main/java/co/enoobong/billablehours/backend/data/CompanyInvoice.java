package co.enoobong.billablehours.backend.data;

import java.math.BigDecimal;
import java.util.List;

public class CompanyInvoice {
  private String companyName;

  private List<Invoice> invoices;

  private BigDecimal totalCost;

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public List<Invoice> getInvoices() {
    return invoices;
  }

  public void setInvoices(List<Invoice> invoices) {
    this.invoices = invoices;
  }

  public BigDecimal getTotalCost() {
    return totalCost;
  }

  public void setTotalCost(BigDecimal totalCost) {
    this.totalCost = totalCost;
  }
}
