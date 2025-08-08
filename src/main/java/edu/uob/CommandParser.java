package edu.uob;

import java.util.*;

public class CommandParser {
    private String storageFolderPath;
    private final String commandContent;
    private String queryToken = null;
    private String[] tokens = null;
    private final GrammarChecker grammarChecker;
    private final MessageHandler messageHandler = new MessageHandler();

    public CommandParser(String commandContent, String storageFolderPath) {
        this.storageFolderPath = storageFolderPath;
        this.commandContent = commandContent;
        this.grammarChecker = new GrammarChecker(this.storageFolderPath);
    }

    public String parseCommand() {
        if(this.commandContent.isEmpty()) {
            return messageHandler.errorMessage(1);
        }

        getAllTokens();
        if(!checkQueryValidity()){
            return messageHandler.errorMessage(2);
        }
        if(!checkCorrectPath()){
            return messageHandler.errorMessage(22);
        }
        return checkGrammarValidity();
    }

    private boolean checkQueryValidity(){

        getQueryToken();
        boolean tokenExists = Arrays.asList(TokenKeeper.queryTokens).contains(queryToken);

        if(!tokenExists) {
            return false;
        }
        if(!Arrays.asList(TokenKeeper.queryTokens).contains(queryToken)){
            return false;
        }
        return true;
    }

    private boolean checkCorrectPath(){
        if(!(queryToken.equals("USE") || queryToken.equals("CREATE"))
                && DataManipulator.currentDatabasePath.equals(storageFolderPath)) {
            return false;
        }
        return true;
    }

    private String checkGrammarValidity() {

        if(!grammarChecker.checkSemicolon(tokens)){
            return messageHandler.errorMessage(3);
        }


        return switch (queryToken) {
            case "USE" -> grammarChecker.checkUSE(queryToken, tokens);
            case "CREATE" -> grammarChecker.checkCREATE(queryToken, tokens);
            case "DROP" -> grammarChecker.checkDROP(queryToken, tokens);
            case "ALTER" -> grammarChecker.checkALTER(queryToken, tokens);
            case "INSERT" -> grammarChecker.checkINSERT(queryToken, tokens);
            case "SELECT" -> grammarChecker.checkSELECT(queryToken, tokens);
            case "UPDATE" -> grammarChecker.checkUPDATE(queryToken, tokens);
            case "DELETE" -> grammarChecker.checkDELETE(queryToken, tokens);
            case "JOIN" -> grammarChecker.checkJOIN(queryToken, tokens);
            default -> "Invalid Query Token";
        };
    }

    public void getQueryToken() {
        queryToken = this.commandContent.split(" ")[0];
        queryToken = queryToken.toUpperCase();
    }

    public String [] getAllTokens() {
        tokens = this.commandContent.trim().split("\\s+");

        List<String> tokenList = new ArrayList<>();
        for (String token : tokens) {
            if(!token.trim().isEmpty()) {
                tokenList.add(token);
            }
        }

        if(tokenList.size() > 0 && tokenList.get(tokenList.size() - 1).endsWith(";")) {
            String lastToken = tokenList.get(tokenList.size() - 1);
            if(!lastToken.equals(";")){
                tokenList.set(tokenList.size() - 1, lastToken.substring(0, lastToken.length() - 1));
                tokenList.add(";");
            }
        }
        tokens = new String[tokenList.size()];
        tokens = tokenList.toArray(tokens);
        return tokens;
    }
}

