package co.enoobong.billablehours.backend.data;

import java.time.LocalDate;
import java.time.LocalTime;

public class Timesheet {

  private long employeeId;

  private double billableRate;

  private String project;

  private LocalDate date;

  private LocalTime startTime;

  private LocalTime endTime;

  public Timesheet(long employeeId, double billableRate, String project, LocalDate date, LocalTime startTime,
                   LocalTime endTime) {
    this.employeeId = employeeId;
    this.billableRate = billableRate;
    this.project = project;
    this.date = date;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public long getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(long employeeId) {
    this.employeeId = employeeId;
  }

  public double getBillableRate() {
    return billableRate;
  }

  public void setBillableRate(double billableRate) {
    this.billableRate = billableRate;
  }

  public String getProject() {
    return project;
  }

  public void setProject(String project) {
    this.project = project;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalTime startTime) {
    this.startTime = startTime;
  }

  public LocalTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalTime endTime) {
    this.endTime = endTime;
  }
}
