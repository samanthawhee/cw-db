package edu.uob;

import java.io.File;

public class AddressGetter {
    private final String storageFolderPath;

    public AddressGetter(String storageFolderPath) {
        this.storageFolderPath = storageFolderPath;
    }

    public String getDatabasePath(String databaseName) {
        // Can't change this.storageFolderPath !!!
        return this.storageFolderPath + File.separator + databaseName;
    }

    public String getLastDatabasePath(String databaseName) {
        String path = getDatabasePath(databaseName);
        if (path != null && path.length() >=3) {
            path = path.substring(0, path.length() - 3);
            return path;
        }
        return "[ERROR] 014 : Path not found";
    }

    public String getTablePath(String tableName) {
        return DataManipulator.currentDatabasePath + File.separator + tableName + ".tab";
    }

    public String getTableWithDatabase(String databasePath, String tableName) {
        return  databasePath + File.separator + tableName + ".tab";
    }

}
