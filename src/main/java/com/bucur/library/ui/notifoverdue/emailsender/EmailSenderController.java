package com.bucur.library.ui.notifoverdue.emailsender;

import com.bucur.library.alert.AlertMaker;
import com.bucur.library.data.callback.GenericCallback;
import com.bucur.library.data.model.MailServerInfo;
import com.bucur.library.database.DataHelper;
import com.bucur.library.email.EmailUtil;
import com.bucur.library.ui.notifoverdue.NotificationItem;
import com.bucur.library.ui.settings.Preferences;
import com.bucur.library.util.LibraryAssistantUtil;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller class
 */
public class EmailSenderController implements Initializable {

    @FXML
    private JFXProgressBar progressBar;
    @FXML
    private Text text;

    private List<NotificationItem> list;
    private StringBuilder emailText = new StringBuilder();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Scanner scanner = new Scanner(getClass().getResourceAsStream(LibraryAssistantUtil.MAIL_CONTENT_LOC));
            while (scanner.hasNext()) {
                emailText.append(scanner.nextLine()).append("\n");
            }
            System.out.println(emailText);
        } catch (Exception e) {
            Logger.getLogger(EmailSenderController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void setNotificationRequestData(List<NotificationItem> list) {
        this.list = list;
    }

    public Stage getStage() {
        return (Stage) progressBar.getScene().getWindow();
    }

    public void start() {
        if (emailText == null || emailText.toString().isEmpty()) {
            AlertMaker.showErrorMessage("Failed", "Failed to parse email format");
            getStage().close();
        }
        new EmailSenderHelper().start();
    }

    class EmailSenderHelper extends Thread implements GenericCallback {

        private final AtomicBoolean flag = new AtomicBoolean(true);
        private final MailServerInfo mailServerInfo = DataHelper.loadMailServerInfo();

        @Override
        public void run() {
            final int size = list.size();
            int count = 0;

            Iterator iterator = list.iterator();
            while (iterator.hasNext() && flag.get()) {
                count++;
                NotificationItem item = (NotificationItem) iterator.next();
                String reportDate = LibraryAssistantUtil.getDateString(new Date());
                String bookName = item.getBookName();
                String issueDate = item.getIssueDate();
                Integer daysUsed = item.getDayCount();
                String finePerDay = String.valueOf(Preferences.getPreferences().getFinePerDay());
                String amount = item.getFineAmount();
                String emailContent = String.format(emailText.toString(), reportDate, bookName, issueDate, daysUsed, finePerDay, amount);
                EmailUtil.sendMail(mailServerInfo, item.getMemberEmail(), emailContent, "Library Assistant Overdue Notification", this);
                flag.set(false);
                updateUI(size, count);
            }
            Platform.runLater(() -> {
                text.setText("Process Completed!");
                progressBar.setProgress(1);
            });
        }

        @Override
        public Object taskCompleted(Object val) {
            flag.set(true);
            return null;
        }

        private void updateUI(int size, int count) {
            Platform.runLater(() -> {
                text.setText(String.format("Notifying %d/%d", count, size));
                progressBar.setProgress((double) count / (double) size);
            });
        }
    }

}
