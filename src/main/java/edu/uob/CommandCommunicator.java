package edu.uob;

import java.util.Arrays;

public class CommandCommunicator {
    private final String commandContent;
    private String storageFolderPath;

    public CommandCommunicator(String commandContent, String storageFolderPath) {
        this.commandContent = commandContent;
        this.storageFolderPath = storageFolderPath;
    }

    public String getCommandContent() {
        String result = null;
        String queryToken = null;
        CommandParser parser = new CommandParser(this.commandContent, this.storageFolderPath);
        DataManipulator dataManipulator = new DataManipulator(this.storageFolderPath);
        queryToken = parser.parseCommand();
        String[] allTokens = parser.getAllTokens();
        if(Arrays.asList(TokenKeeper.queryTokens).contains(queryToken)) {
            result = dataManipulator.manipulateData(queryToken, allTokens);
            return result;
        }
        return queryToken;
    }
}
