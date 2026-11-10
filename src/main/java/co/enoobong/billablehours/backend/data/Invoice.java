package co.enoobong.billablehours.backend.data;

import java.math.BigDecimal;
public class Invoice {

  private long employeeId;

  private BigDecimal numberOfHours;

  private BigDecimal unitPrice;

  private BigDecimal cost;

  public Invoice(long employeeId, BigDecimal numberOfHours, BigDecimal unitPrice, BigDecimal cost) {
    this.employeeId = employeeId;
    this.numberOfHours = numberOfHours;
    this.unitPrice = unitPrice;
    this.cost = cost;
  }

  public long getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(long employeeId) {
    this.employeeId = employeeId;
  }

  public BigDecimal getNumberOfHours() {
    return numberOfHours;
  }

  public void setNumberOfHours(BigDecimal numberOfHours) {
    this.numberOfHours = numberOfHours;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  public BigDecimal getCost() {
    return cost;
  }

  public void setCost(BigDecimal cost) {
    this.cost = cost;
  }

}
