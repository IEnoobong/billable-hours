package co.enoobong.billablehours.ui.views;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.notification.Notification;

import static co.enoobong.billablehours.ui.utils.TimesheetConstants.NOTIFICATION_DURATION;

public interface HasNotifications extends HasElement {

  default void showNotification(String message) {
    Notification.show(message, NOTIFICATION_DURATION, Notification.Position.TOP_END);
  }
}
