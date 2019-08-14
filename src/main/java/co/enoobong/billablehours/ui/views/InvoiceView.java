package co.enoobong.billablehours.ui.views;

import co.enoobong.billablehours.backend.data.CompanyInvoice;
import co.enoobong.billablehours.backend.data.Invoice;
import co.enoobong.billablehours.backend.data.InvoiceResponse;
import co.enoobong.billablehours.backend.service.TimesheetService;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import java.util.List;

@Route("invoice")
@PageTitle("Invoice")
@PreserveOnRefresh
//@HtmlImport("styles/shared-styles.html")
public class InvoiceView extends VerticalLayout implements HasNotifications, AfterNavigationObserver {

  private final TimesheetService timesheetService;

  public InvoiceView(TimesheetService timesheetService) {
    this.timesheetService = timesheetService;
  }

  @Override
  public void afterNavigation(AfterNavigationEvent event) {
    final InvoiceResponse invoiceResponse = timesheetService.getInvoiceResponse();

    if (invoiceResponse == null) {
      getUI().ifPresent(ui -> ui.navigate(UploadView.class)); //go to upload
      return;
    }

    add(new H1("Invoice(s)"));

    displayCompanyInvoices(invoiceResponse.getCompanyInvoices());

    displayErrors(invoiceResponse.getErrors());
  }

  private void displayCompanyInvoices(List<CompanyInvoice> companyInvoices) {
    for (final CompanyInvoice companyInvoice : companyInvoices) {
      add(new H2("Company: " + companyInvoice.getCompanyName()));
      final List<Invoice> invoices = companyInvoice.getInvoices();
      final Grid<Invoice> invoiceGrid = new Grid<>();

      invoiceGrid.setHeightByRows(invoices.size() <= 10);
      invoiceGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
      invoiceGrid.setItems(invoices);
      invoiceGrid.prependHeaderRow();

      invoiceGrid.addColumn(Invoice::getEmployeeId).setHeader("Employee ID");
      invoiceGrid.addColumn(Invoice::getNumberOfHours).setHeader("Number of hours");
      invoiceGrid.addColumn(Invoice::getUnitPrice).setHeader("Unit Price").setFooter("Total");
      invoiceGrid.addColumn(Invoice::getCost).setHeader("Cost").setFooter(String.valueOf(companyInvoice.getTotalCost()));

      add(invoiceGrid);
    }
  }

  private void displayErrors(List<String> errors) {
    if (!errors.isEmpty()) {
      final String errorMessage = "%d error(s) occurred when processing your timesheet";
      showNotification(String.format(errorMessage, errors.size()));

      final Accordion accordion = new Accordion();
      final UnorderedList errorList = new UnorderedList(errors.stream().map(ListItem::new).toArray(ListItem[]::new));

      accordion.add("Error(s)", errorList).addThemeVariants(DetailsVariant.FILLED);
      accordion.close();
      add(accordion);
    }
  }
}
