package edu.uob;

import java.io.File;
import java.nio.file.*;

public class DatabaseAccessor {
    private final String storageFolderPath;
    private final AddressGetter addressGetter;
    private final MessageHandler messageHandler;

    public DatabaseAccessor(String storageFolderPath) {
        this.storageFolderPath = storageFolderPath;
        this.addressGetter = new AddressGetter(this.storageFolderPath);
        this.messageHandler = new MessageHandler();
    }

    public boolean checkDBExists(String databaseName) {
        String dbPath = addressGetter.getDatabasePath(databaseName);
        File dbFile = new File(dbPath);
        return dbFile.exists() && dbFile.isDirectory();
    }

    public boolean createDB(String databaseName) {
        String dbPath = addressGetter.getDatabasePath(databaseName);
        File dbFile = new File(dbPath);
        dbFile.mkdirs();
        switchDB(databaseName);
        return (dbFile.exists()) && dbFile.isDirectory();
    }

    public boolean switchDB(String databaseName) {
        DataManipulator.currentDatabasePath = addressGetter.getDatabasePath(databaseName);
        return true;
    }
    public boolean deleteDB(String databaseName) {
        String dbPath = addressGetter.getDatabasePath(databaseName);
        Path tablePath = Paths.get(dbPath);
        File dbFile = new File(dbPath);
        if(!dbFile.exists()) {
            return false;
        }

        if(!dbFile.isDirectory()) {
            return false;
        }
        // Database is empty
        if(dbFile.list().length == 0) {
            return false;
        } else {

            File [] files = dbFile.listFiles();
            if(files != null) {
                for(File file : files) {
                    file.delete();
                }
            }
        }
        if(dbFile.exists()) {
            if(dbFile.delete()) {
                // Database deleted successfully
            } else {
                // Database deletion failed
            }
        }
        // Path not found
        if((addressGetter.getLastDatabasePath(databaseName)).equals(messageHandler.errorMessage(14))){
            return false;
        }
        return !dbFile.exists() && !dbFile.isDirectory();
    }
}
