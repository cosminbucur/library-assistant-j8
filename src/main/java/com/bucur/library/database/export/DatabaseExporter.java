package com.bucur.library.database.export;

import com.bucur.library.alert.AlertMaker;
import com.bucur.library.database.DatabaseHandler;
import com.bucur.library.util.LibraryAssistantUtil;
import javafx.concurrent.Task;

import java.io.File;
import java.sql.CallableStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatabaseExporter extends Task<Boolean> {

    private final File backupDirectory;

    public DatabaseExporter(File backupDirectory) {
        this.backupDirectory = backupDirectory;
    }

    @Override
    protected Boolean call() {
        try {
            createBackup();
            return true;
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return false;
    }

    // TODO: make private after test
    public void createBackup() throws Exception {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm_ss");
        String backupDir = backupDirectory.getAbsolutePath() + File.separator + LocalDateTime.now().format(dateFormat);
        String backupZip = backupDir + ".zip";
        try (CallableStatement cs = DatabaseHandler.getInstance().getConnection().prepareCall("BACKUP TO (?)")) {
            cs.setString(1, backupZip);
            cs.execute();
        }
        File file = new File(backupZip);
        LibraryAssistantUtil.openFileWithDesktop(file);
    }
}
