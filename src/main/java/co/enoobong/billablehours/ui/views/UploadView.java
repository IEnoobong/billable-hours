package co.enoobong.billablehours.ui.views;

import co.enoobong.billablehours.backend.service.TimesheetService;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.FailedEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.dom.DomEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;

@Route("")
@PageTitle("Billable Hours")
public class UploadView extends VerticalLayout implements HasNotifications {

  private static final Logger log = LoggerFactory.getLogger(UploadView.class);

  private final TimesheetService timesheetService;

  public UploadView(TimesheetService timesheetService) {
    this.timesheetService = timesheetService;

    final Header header = new Header();
    header.add(new H1("Please upload your timesheet"));

    final Upload upload = createUpload();

    setSpacing(true);

    add(header, upload);
  }

  private Upload createUpload() {
    final MemoryBuffer receiver = new MemoryBuffer();
    final Upload upload = new Upload(receiver);

    upload.setDropLabel(new Label("Upload timesheet in .csv format"));
    upload.setAcceptedFileTypes("text/csv");
    upload.setMaxFileSize(1024);

    upload.getElement().addEventListener("file-reject", event -> onFileRejected(upload.getMaxFileSize(), event));

    upload.addSucceededListener(event -> onUploadSucceeded(receiver));

    upload.addFailedListener(this::onUploadFailed);

    return upload;
  }

  private void onFileRejected(int maxFileSizeInBytes, DomEvent event) {
    log.info("file rejected {}", event);
    //there could be other reasons but the Java event object is not so flexible
    final String errorMessage = "Maximum upload size exceeded. File cannot be more than %d bytes";
    showNotification(String.format(errorMessage, maxFileSizeInBytes));
  }

  private void onUploadFailed(FailedEvent event) {
    log.warn("uploading failed {}", event.getReason().getMessage());
    showNotification("Upload failed.");
  }

  private void onUploadSucceeded(MemoryBuffer receiver) {
    try {
      timesheetService.generateInvoice(new InputStreamResource(receiver.getInputStream()));
      getUI().ifPresent(ui -> ui.navigate(InvoiceView.class));
    } catch (IOException ex) {
      showNotification("Error occurred when generating invoice");
    }
  }


}
