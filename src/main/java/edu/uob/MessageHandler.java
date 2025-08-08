package edu.uob;


public class MessageHandler {

    public String errorMessage(Integer errorCode) {
        return switch (errorCode) {
            case 1 -> "[ERROR] 001 : Command not found";
            case 2 -> "[ERROR] 002 : Invalid Query Token";
            case 3 -> "[ERROR] 003 : Semicolon not found";
            case 4 -> "[ERROR] 004 : Searching table only can be one";
            case 5 -> "[ERROR] 005 : Invalid Tokens";
            case 6 -> "[ERROR] 006 : Invalid Tokens";
            case 7 -> "[ERROR] 007 : Database/table not found";
            case 8 -> "[ERROR] 008 : Only can create one table at once";
            case 9 -> "[ERROR] 009 : Database/table names only can be digits and letters";
            case 10 -> "[ERROR] 010 : Database/table exists";
            case 11 -> "[ERROR] 011 : Create query should be DATABASE or TABLE";
            case 12 -> "[ERROR] 012 : Failed to create database/table";
            case 13 -> "[ERROR] 013 : Failed to delete database/table";
            case 14 -> "[ERROR] 014 : Path not found";
            case 15 -> "[ERROR] 015 : Invalid inserted values";
            case 16 -> "[ERROR] 016 : Invalid inserted value numbers";
            case 17 -> "[ERROR] 017 : Attribute not found";
            case 18 -> "[ERROR] 018 : Failed to alter table";
            case 19 -> "[ERROR] 019 : Attribute exists";
            case 20 -> "[ERROR] 020 : Invalid conditions";
            case 21 -> "[ERROR] 021 : Failed to delete";
            case 22 -> "[ERROR] 022 : Use or create database before accessing data";
            default -> "[ERROR] 009 : Unknown Error";
        };
    }

    public String successMessage(Integer successCode) {
        return switch (successCode) {
            case 1 -> "[OK] 001 : The query token is valid";
            case 2 -> "[OK] 002 : The query grammar is valid";
            case 3 -> "[OK] 003 : Semicolon found";
            case 4 -> "[OK] 004 : Tokens are valid";
            case 5 -> "[OK] 005 : Database found";
            case 6 -> "[OK] 006 : Database/table created";
            case 7 -> "[OK] 007 : Database/table deleted";
            case 8 -> "[OK] 008 : Data inserted";
            case 9 -> "[OK] 009 : Attribute inserted";
            case 10 -> "[OK]";
            case 11 -> "[OK] 011 : Attribute dropped";
            default -> "[OK] 007 : Unknown Success";
        };
    }
}
