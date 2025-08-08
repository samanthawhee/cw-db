package edu.uob;

import java.util.ArrayList;
import java.util.Arrays;



public class SelectHelper {
    private String storageFolderPath;
    private final MessageHandler messageHandler;
    private  final TableAccessor tableAccessor;

    public SelectHelper(String storageFolderPath) {
        this.storageFolderPath = storageFolderPath;
        this.messageHandler = new MessageHandler();
        this.tableAccessor  = new TableAccessor(storageFolderPath);
    }
    public String [] getListFrom2Str(String startString, String endString, String [] allTokens) {
        return getListFrom2Str(startString, endString, allTokens, 0, false, new ArrayList<>());
    }

    private String[] getListFrom2Str(String startString, String endString, String [] allTokens, int index, boolean collecting, ArrayList<String> tokenList) {

        if(index >= allTokens.length) {
            return tokenList.toArray(new String[0]);
        }

        String token = allTokens[index];
        if(token.equals(startString)) {
            return getListFrom2Str(startString, endString, allTokens, index + 1, true, tokenList);
        }else if(token.equals(endString)) {
            return tokenList.toArray(new String[0]);
        }else if (collecting) {
            tokenList.add(token);
        }
        return getListFrom2Str(startString, endString, allTokens, index + 1, collecting, tokenList);

    }

    public String [] getListFrom1Str(String startString, String [] allTokens) {
        return getListFrom1Str(startString, allTokens, 0, false, new ArrayList<>());
    }

    private String[] getListFrom1Str(String startString, String [] allTokens, int index, boolean collecting, ArrayList<String> tokenList) {

        if(index >= allTokens.length) {
            return tokenList.toArray(new String[0]);
        }

        String token = allTokens[index];
        if(token.equals(startString)) {
            return getListFrom1Str(startString, allTokens, index + 1, true, tokenList);
        }else if (collecting) {
            tokenList.add(token);
        }
        return getListFrom1Str(startString, allTokens, index + 1, collecting, tokenList);

    }

    public String getConditionTable(boolean updateIndex, String tableName, String [] tokens){
        // Get the String [] condition
        String [] conditions = getListFrom2Str("WHERE", ";", tokens);

        // Check if condition has less than 3 elements (A == 5) or between 3 and 11
        if((conditions.length != 3 && conditions.length < 5)
                || (conditions.length > 5 && conditions.length <11)){
            return messageHandler.errorMessage(20);
        }

        // Check if condition has 3 elements (A == 5) or has 11 elements (A == 5) AND (B <=4)
        if(conditions.length == 3 || conditions.length == 5 ||conditions.length == 11){
            if(conditions.length == 5){
                conditions = getListFrom2Str("(", ")", tokens);
                String result  =  tableAccessor.printBaseConditionTable (updateIndex, tableName, conditions);
                return result;
            }else{
                String result  =  tableAccessor.printBaseConditionTable (updateIndex, tableName, conditions);
                return result;
            }
        } else {
            // If it not
            String result  =  tableAccessor.printMultipleConditionTable (tableName, conditions);
//            return result;
            return " ";
        }
    }
}
