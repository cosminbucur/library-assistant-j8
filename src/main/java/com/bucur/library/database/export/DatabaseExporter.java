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
        } catch (Exception exp) {
            AlertMaker.showErrorMessage(exp);
        }
        return false;
    }

    private void createBackup() throws Exception {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm_ss");
        String backupDir = backupDirectory.getAbsolutePath() + File.separator + LocalDateTime.now().format(dateFormat);
        try (CallableStatement cs = DatabaseHandler.getInstance().getConnection().prepareCall("CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)")) {
            cs.setString(1, backupDir);
            cs.execute();
        }
        File file = new File(backupDir);
        LibraryAssistantUtil.openFileWithDesktop(file);
    }
}
