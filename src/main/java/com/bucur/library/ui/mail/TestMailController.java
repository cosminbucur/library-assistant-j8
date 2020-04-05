package com.bucur.library.ui.mail;

import com.bucur.library.alert.AlertMaker;
import com.bucur.library.data.callback.GenericCallback;
import com.bucur.library.data.model.MailServerInfo;
import com.bucur.library.email.EmailUtil;
import com.bucur.library.util.LibraryAssistantUtil;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 */
public class TestMailController implements Initializable, GenericCallback {

    private static final Logger LOGGER = LogManager.getLogger(TestMailController.class.getName());

    @FXML
    private JFXTextField recipientAddressInput;
    @FXML
    private JFXProgressBar progressBar;

    private MailServerInfo mailServerInfo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setMailServerInfo(MailServerInfo mailServerInfo) {
        this.mailServerInfo = mailServerInfo;
    }

    @FXML
    private void handleStartAction(ActionEvent event) {
        String toAddress = recipientAddressInput.getText();
        if (LibraryAssistantUtil.validateEmailAddress(toAddress)) {
            EmailUtil.sendTestMail(mailServerInfo, toAddress, this);
            progressBar.setVisible(true);
        } else {
            AlertMaker.showErrorMessage("Failed", "Invalid email address!");
        }
    }

    @Override
    public Object taskCompleted(Object val) {
        LOGGER.log(Level.INFO, "Callback received from Email Sender client {}", val);
        boolean result = (boolean) val;
        Platform.runLater(() -> {
            if (result) {
                AlertMaker.showSimpleAlert("Success", "Email successfully sent!");
            } else {
                AlertMaker.showErrorMessage("Failed", "Something went wrong!");
            }
            progressBar.setVisible(false);
        });
        return true;
    }

}
