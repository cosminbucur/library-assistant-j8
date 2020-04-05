package com.bucur.library.database.export;

import org.junit.jupiter.api.Test;

import java.io.File;

class DatabaseExporterTest {

    @Test
    void shouldCreateBackupForDb() throws Exception {
        File backupDir = new File("src/test/resources/test-export");
        DatabaseExporter databaseExporter = new DatabaseExporter(backupDir);
        databaseExporter.createBackup();
    }

}