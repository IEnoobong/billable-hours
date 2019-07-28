package co.enoobong.billablehours.backend.data;

public class Invoice {

  private long employeeId;

  private long numberOfHours;

  private double unitPrice;

  private double cost;

  public Invoice(long employeeId, long numberOfHours, double unitPrice, double cost) {
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

  public long getNumberOfHours() {
    return numberOfHours;
  }

  public void setNumberOfHours(long numberOfHours) {
    this.numberOfHours = numberOfHours;
  }

  public double getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(double unitPrice) {
    this.unitPrice = unitPrice;
  }

  public double getCost() {
    return cost;
  }

  public void setCost(double cost) {
    this.cost = cost;
  }

}
