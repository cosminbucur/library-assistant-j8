package com.bucur.library.ui.settings;

import com.bucur.library.alert.AlertMaker;
import com.bucur.library.data.model.MailServerInfo;
import com.bucur.library.database.DataHelper;
import com.bucur.library.database.DatabaseHandler;
import com.bucur.library.database.export.DatabaseExporter;
import com.bucur.library.ui.mail.TestMailController;
import com.bucur.library.util.LibraryAssistantUtil;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger(DatabaseHandler.class.getName());
    @FXML
    private JFXTextField nDaysWithoutFine;
    @FXML
    private JFXTextField finePerDay;
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXTextField serverName;
    @FXML
    private JFXTextField smtpPort;
    @FXML
    private JFXTextField emailAddress;
    @FXML
    private JFXPasswordField emailPassword;
    @FXML
    private JFXCheckBox sslCheckbox;
    @FXML
    private JFXSpinner progressSpinner;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initDefaultValues();
    }

    @FXML
    private void handleSaveButtonAction(ActionEvent event) {
        int ndays = Integer.parseInt(nDaysWithoutFine.getText());
        float fine = Float.parseFloat(finePerDay.getText());
        String uname = username.getText();
        String pass = password.getText();

        Preferences preferences = Preferences.getPreferences();
        preferences.setnDaysWithoutFine(ndays);
        preferences.setFinePerDay(fine);
        preferences.setUsername(uname);
        preferences.setPassword(pass);

        Preferences.writePreferenceToFile(preferences);
    }

    private Stage getStage() {
        return ((Stage) nDaysWithoutFine.getScene().getWindow());
    }

    private void initDefaultValues() {
        Preferences preferences = Preferences.getPreferences();
        nDaysWithoutFine.setText(String.valueOf(preferences.getnDaysWithoutFine()));
        finePerDay.setText(String.valueOf(preferences.getFinePerDay()));
        username.setText(String.valueOf(preferences.getUsername()));
        String passHash = String.valueOf(preferences.getPassword());
        password.setText(passHash.substring(0, Math.min(passHash.length(), 10)));
        loadMailServerConfigurations();
    }

    @FXML
    private void handleTestMailAction(ActionEvent event) {
        MailServerInfo mailServerInfo = readMailSererInfo();
        if (mailServerInfo != null) {
            TestMailController controller = (TestMailController) LibraryAssistantUtil.loadWindow(getClass().getResource("/fxml/test_mail.fxml"), "Test Email", null);
            controller.setMailServerInfo(mailServerInfo);
        }
    }

    @FXML
    private void saveMailServerConfiguration(ActionEvent event) {
        MailServerInfo mailServerInfo = readMailSererInfo();
        if (mailServerInfo != null) {
            if (DataHelper.updateMailServerInfo(mailServerInfo)) {
                AlertMaker.showSimpleAlert("Success", "Saved successfully!");
            } else {
                AlertMaker.showErrorMessage("Failed", "Something went wrong!");
            }
        }
    }

    private MailServerInfo readMailSererInfo() {
        try {
            MailServerInfo mailServerInfo
                = new MailServerInfo(serverName.getText(), Integer.parseInt(smtpPort.getText()), emailAddress.getText(), emailPassword.getText(), sslCheckbox.isSelected());
            if (!mailServerInfo.validate() || !LibraryAssistantUtil.validateEmailAddress(emailAddress.getText())) {
                throw new InvalidParameterException();
            }
            return mailServerInfo;
        } catch (Exception e) {
            AlertMaker.showErrorMessage("Invalid Entries Found", "Correct input and try again");
            LOGGER.log(Level.WARN, e);
        }
        return null;
    }

    private void loadMailServerConfigurations() {
        MailServerInfo mailServerInfo = DataHelper.loadMailServerInfo();
        if (mailServerInfo != null) {
            LOGGER.log(Level.INFO, "Mail server info loaded from DB");
            serverName.setText(mailServerInfo.getMailServer());
            smtpPort.setText(String.valueOf(mailServerInfo.getPort()));
            emailAddress.setText(mailServerInfo.getEmailID());
            emailPassword.setText(mailServerInfo.getPassword());
            sslCheckbox.setSelected(mailServerInfo.getSslEnabled());
        }
    }

    @FXML
    private void handleDatabaseExportAction(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Location to Create Backup");
        File selectedDirectory = directoryChooser.showDialog(getStage());
        if (selectedDirectory == null) {
            AlertMaker.showErrorMessage("Export cancelled", "No Valid Directory Found");
        } else {
            DatabaseExporter databaseExporter = new DatabaseExporter(selectedDirectory);
            progressSpinner.visibleProperty().bind(databaseExporter.runningProperty());
            new Thread(databaseExporter).start();
        }
    }
}
