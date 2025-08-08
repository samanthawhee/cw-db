package edu.uob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataManipulator {
    private final String storageFolderPath;
    private final DatabaseAccessor databaseAccessor;
    private final MessageHandler messageHandler;
    private final GrammarMaker grammarMaker;
    private final TableAccessor tableAccessor;
    private final ElementsSpliter elementsSpliter;
    private final IndexAccessor indexAccessor;
    public static String currentDatabasePath = null;
    public CharacterUpper characterUpper;
    private final SelectHelper selectHelper;
    private  final UpdateHelper updateHelper;
    private final TokenValidityChecker tokenValidityChecker;
    private final TokenSpliter tokenSpliter;

    public DataManipulator(String storageFolderPath) {
        this.storageFolderPath = storageFolderPath;
        this.messageHandler = new MessageHandler();
        this.grammarMaker = new GrammarMaker();
        this.elementsSpliter = new ElementsSpliter();
        this.characterUpper = new CharacterUpper();
        this.updateHelper = new UpdateHelper();
        this.tokenSpliter = new TokenSpliter();
        this.tokenValidityChecker = new TokenValidityChecker();
        this.selectHelper = new SelectHelper(this.storageFolderPath);
        this.tableAccessor = new TableAccessor(this.storageFolderPath);
        this.indexAccessor = new IndexAccessor(this.storageFolderPath);
        this.databaseAccessor = new DatabaseAccessor(this.storageFolderPath);
        if(currentDatabasePath == null){
            currentDatabasePath = this.storageFolderPath;
        }
    }

    public String manipulateData(String queryToken, String[] allTokens) {
        try {
            return switch (queryToken) {
                case "USE" -> doUSE(queryToken, allTokens);
                case "CREATE" -> doCREATE(queryToken, allTokens);
                case "DROP" -> doDROP(queryToken, allTokens);
                case "ALTER" -> doALTER(queryToken, allTokens);
                case "INSERT" -> doINSERT(queryToken, allTokens);
                case "SELECT" -> doSELECT(queryToken, allTokens);
                case "UPDATE" -> doUPDATE(queryToken, allTokens);
                case "DELETE" -> doDELETE(queryToken, allTokens);
                case "JOIN" -> doJOIN(queryToken, allTokens);
                default -> "Invalid Query Token";
            };
        }catch (Exception e){
            return "Invalid Query Token";
        }

    }

    public String doUSE(String queryToken, String [] allTokens) {
        // Check if the database exists
        if(!databaseAccessor.checkDBExists(allTokens[1])){
            return messageHandler.errorMessage(7);
        }
        // Switch into the database
        databaseAccessor.switchDB(allTokens[1]);
        return messageHandler.successMessage(5);
    }
    public String doCREATE(String queryToken, String [] tokens) {
        String Name = tokens[2];
        tokens = tokenSpliter.splitToken4CREATE(tokens);
        String queryType = tokens[1];
        switch(queryType){
             case "DATABASE":
                 if(databaseAccessor.checkDBExists(tokens[2])){
                     return messageHandler.errorMessage(10);
                 }else if(databaseAccessor.createDB(Name)) {
                     return messageHandler.successMessage(6);
                 }
                 return messageHandler.errorMessage(12);
            case  "TABLE":
                if(tokens.length == 4){
                    if(this.tableAccessor.checkTableExists(tokens[2])) {
                        return messageHandler.errorMessage(10);
                    } else if(tableAccessor.createTable(Name)) {
                        return messageHandler.successMessage(6);
                    }
                    return messageHandler.errorMessage(12);
                } else {
                    tokens = elementsSpliter.splitElements(tokens, TokenKeeper.CREATE);
                    tokens = elementsSpliter.splitAttribute(tokens, TokenKeeper.CREATE);
                    // allTokens[0] = table name
                    if(tableAccessor.createTable(tokens[0])){
                        // allTokens[1-allTokens.length-1] = attribute
                        if(tableAccessor.addTableAttribute(tokens)){

                            return messageHandler.successMessage(6);
                        }
                    } else {
                        return messageHandler.errorMessage(12);
                    }
                    return messageHandler.successMessage(7);
                }

            default:
                return messageHandler.errorMessage(11);
        }
    }
    public String doDROP(String queryToken, String [] tokens) {
        String Name = tokens[2];
        tokens = tokenSpliter.splitToken4DROP(tokens);
        String queryType = tokens[1];
        switch(queryType){
            case "DATABASE":
                if(!databaseAccessor.checkDBExists(tokens[2])){
                    return messageHandler.errorMessage(7);
                }else if(databaseAccessor.deleteDB(Name)) {
                    return messageHandler.successMessage(7);
                }
                return messageHandler.errorMessage(13);
            case  "TABLE":
                if(databaseAccessor.checkDBExists(tokens[2])){
                    return messageHandler.errorMessage(10);
                }else if(tableAccessor.deleteTable(Name)) {
                    return messageHandler.successMessage(7);
                }
                return messageHandler.errorMessage(12);
            default:
                return messageHandler.errorMessage(11);
        }
    }

    public String doINSERT(String queryToken, String [] tokens) throws IOException {
        tokens = tokenSpliter.splitToken4INSERT(tokens);

        // Extract INSERT INTO VALUES ( ) ; stored in the certain cell from attributes
        String [] attributeList = selectHelper.getListFrom2Str("(", ")", tokens);
        attributeList = elementsSpliter.removeKeyWords(tokens, TokenKeeper.INSERT);

        // Check the file exists
        String tableName = attributeList[0];
        if(!this.tableAccessor.checkTableExists(tableName)){
            return messageHandler.errorMessage(7);
        }
        // Check the attribute amount is the same with the values
        int tableAttribute = tableAccessor.getAttributeAmount(tableName);
        int insertAttribute = attributeList.length;
        if(insertAttribute != tableAttribute){
            return messageHandler.errorMessage(16);
        }
        // Remove tableName element
        attributeList = Arrays.copyOfRange(attributeList, 1, attributeList.length);
        // Append index + write the record
        List<String> insertData = indexAccessor.appendIndexToString(tableName, Arrays.asList(attributeList));

        return messageHandler.successMessage(8);
    }
    public String doALTER(String queryToken, String [] tokens) throws IOException {
        tokens = tokenSpliter.splitToken4ALTER(tokens);
        String tableName = tokens[2];
        // Check if the table exists
        if(!tableAccessor.checkTableExists(tableName)){
            return messageHandler.errorMessage(7);
        }
        // Check the executed action : ADD or DROP
        String action = tokens[3];
        String [] tableAttributes = tableAccessor.getTableAttributes(tableName);
        switch (action){
            case "ADD":
                // Check if the attribute exists
                String [] addToken = {tokens[4]};
                if(!tableAccessor.checkAttributeExist(addToken, tableAttributes)){
                    // Add if not found
                    String newAttribute = tokens[4];
                    if(tableAccessor.AddCertainAttribute(tableName, newAttribute)){
                        return messageHandler.successMessage(9);
                    } else {
                        return messageHandler.errorMessage(18);
                    }
                }
                return messageHandler.errorMessage(19);
            case "DROP":
                // Check if the attribute exists
                String [] dropToken = {tokens[4]};
                if(tableAccessor.checkAttributeExist(dropToken, tableAttributes)){
                   // Check the index in the table if found
                }
                // Check the index in the table
                int index = tableAccessor.checkAttributeIndex(dropToken, tableName);
                if(index == -1 ){
                    return messageHandler.errorMessage(17);
                } else {
                    // Delete the column for each row
                    if(tableAccessor.dropAttribute(index, tableName)){
                        return messageHandler.successMessage(11);
                    } else {
                        return messageHandler.errorMessage(18);
                    }
                }
            default:
                return messageHandler.errorMessage(18);
        }
    }

    public String doSELECT(String queryToken, String [] tokens) throws IOException {
        tokens = tokenSpliter.splitTokenSELECT(tokens);

        // Check if it finds WHERE
        boolean ConditionFound = false;
        for(String token : tokens){
            if(token.equals("WHERE")){
                ConditionFound = true;
                break;
            }
        }
        if(ConditionFound){
            // Condition Found

            // Check if the table exists
            String [] tableNameArray = selectHelper.getListFrom2Str("FROM", "WHERE", tokens);
            String tableName = tableNameArray[0];
            if(!tableAccessor.checkTableExists(tableName)){
                return messageHandler.errorMessage(7);
            }

            // Check attribute valid (1 attribute, * and more than 1 attribute)
            String [] wildAttributes = selectHelper.getListFrom2Str("SELECT", "FROM", tokens);
            for(int i = 0; i < wildAttributes.length; i++){
                if(wildAttributes[i].equals("id")){
                    wildAttributes[i] = wildAttributes[i].toUpperCase();
                }
            }

            // Check attribute is just 1
            if(wildAttributes.length == 1 && !wildAttributes[0].equals("*")){
                // Check Attributes exist in the table
                String [] tableAttributes = tableAccessor.getTableAttributes(tableName);
                // Check wildAttributes exist in tableAttributes
                if(!tableAccessor.checkAttributeExist(wildAttributes, tableAttributes)){
                    return messageHandler.errorMessage(17);
                }
                // Get the table with conditions
                String getConditionTable = selectHelper.getConditionTable(true, tableName, tokens);
                // Get the table with certain attributes
                String table = tableAccessor.printTableCertainAttr(getConditionTable, wildAttributes);

                return (messageHandler.successMessage(10) +"\n" + table);
            }

            // Check attribute is *
            if(wildAttributes.length == 1 && wildAttributes[0].equals("*")){
                // Get the table with conditions
                String getConditionTable = selectHelper.getConditionTable(true, tableName, tokens);
                // Get the table with certain attributes
                String table = tableAccessor.printTableCertainAttr(getConditionTable, wildAttributes);
                return (messageHandler.successMessage(10) +"\n" + table);
            }else{
                // Attribute more than 1
                List<String> attributeList = new ArrayList<>();
                for(String wildAttribute : wildAttributes){
                    if(!wildAttribute.equals(",")){
                        attributeList.add(wildAttribute);
                    }
                }
                // Check Attributes exist in the table
                wildAttributes = attributeList.toArray(new String[0]);
                String [] tableAttributes = tableAccessor.getTableAttributes(tableName);
                // Check wildAttributes exist in tableAttributes
                if(!tableAccessor.checkAttributeExist(wildAttributes, tableAttributes)){
                    return messageHandler.errorMessage(17);
                }
                // Get the table with conditions
                String getConditionTable = selectHelper.getConditionTable(true, tableName, tokens);
                // Get the table with certain attributes
                String table = tableAccessor.printTableCertainAttr(getConditionTable, wildAttributes);
                return (messageHandler.successMessage(10) +"\n" + table);

            }

        }else{
            // Condition Not Found

            // Check if the table exists
            String tableName = tokens[tokens.length - 2];
            if(!tableAccessor.checkTableExists(tableName)){
                return messageHandler.errorMessage(7);
            }
            // Check if it has *
            String [] wildAttributes = selectHelper.getListFrom2Str("SELECT", "FROM", tokens);
            for(int i = 0; i < wildAttributes.length; i++){
                if(wildAttributes[i].equals("id")){
                    wildAttributes[i] = wildAttributes[i].toUpperCase();
                }
            }

            if(wildAttributes.length == 1 && !wildAttributes[0].equals("*")){
                // Check Attributes exist in the table
                String [] tableAttributes = tableAccessor.getTableAttributes(tableName);
                // Check wildAttributes exist in tableAttributes
                if(!tableAccessor.checkAttributeExist(wildAttributes, tableAttributes)){
                    return messageHandler.errorMessage(17);
                }
                // Return data for certain attributes
                String wholeTable = tableAccessor.printTable(tableName);
                String table = tableAccessor.printTableCertainAttr(wholeTable, wildAttributes);
                return (messageHandler.successMessage(10) +"\n" + table);
            }

            if(wildAttributes.length == 1 && wildAttributes[0].equals("*")){
                // Return data all attributes
                String table = tableAccessor.printTable(tableName);
                return (messageHandler.successMessage(10) +"\n" + table);
            }else{
                List<String> attributeList = new ArrayList<>();
                for(String wildAttribute : wildAttributes){
                    if(!wildAttribute.equals(",")){
                        attributeList.add(wildAttribute);
                    }
                }
                // Check Attributes exist in the table
                wildAttributes = attributeList.toArray(new String[0]);
                String [] tableAttributes = tableAccessor.getTableAttributes(tableName);
                // Check wildAttributes exist in tableAttributes
                if(!tableAccessor.checkAttributeExist(wildAttributes, tableAttributes)){
                    return messageHandler.errorMessage(17);
                }
                // Return data for certain attributes
                String wholeTable = tableAccessor.printTable(tableName);
                String table = tableAccessor.printTableCertainAttr(wholeTable, wildAttributes);
                return (messageHandler.successMessage(10) +"\n" + table);

            }
        }
    }
    public String doUPDATE(String queryToken, String [] tokens) throws IOException{
        tokens = tokenSpliter.splitToken4UPDATE(tokens);

        // Get tableName, nameValueList and conditionList
        String tableName = tokens[1];
        String [] nameValueLit = selectHelper.getListFrom2Str("SET", "WHERE", tokens);
        String [] conditionLit = selectHelper.getListFrom2Str("WHERE", ";", tokens);
        // Check if the table exists
        if(!tableAccessor.checkTableExists(tableName)){
            return messageHandler.errorMessage(7);
        }
        // Check elements which are set before "=" in nameValueList are existing attributes in the table
        String [] tableAttributes = tableAccessor.getTableAttributes(tableName);

        // Get string[] of checkAttributeExist from nameValueLit
        String [] getAttributeList = updateHelper.getAttributeList(nameValueLit);

        if(!tableAccessor.checkAttributeExist(getAttributeList, tableAttributes)){
            return messageHandler.errorMessage(17);
        }
        // Update the value in the certain columns
        // Get the table with conditions
        String getConditionTable = selectHelper.getConditionTable(false, tableName, tokens);
        String updatedTable = tableAccessor.updateTable(tableName, nameValueLit, getConditionTable);return updatedTable;
    }
    public String doDELETE(String queryToken, String [] tokens) throws IOException{
        tokens = tokenSpliter.splitToken4DELETE(tokens);
        String tableName = tokens[2];
        // Check tokens[0] is DELETE, tokens[1] is FROM, tokens[2] is PlainText, tokens[3] is WHERE
        if(!(tokens[0].equals("DELETE") && tokens[1].equals("FROM")
                && grammarMaker.checkPlainText(tokens[2]) &&tokens[3].equals("WHERE"))){
            return messageHandler.errorMessage(2);
        }
        // Get conditions table
        String [] conditionLit = selectHelper.getListFrom2Str("WHERE", ";", tokens);
        // Update the value in the certain columns
        // Get the table with conditions
        String getConditionTable = selectHelper.getConditionTable(false, tableName, tokens);
        ArrayList<String> conditionTable = tableAccessor.string2ArrayList(getConditionTable);
        try{
            // Delete columns
            tableAccessor.updateData(2, tableName, conditionTable);

        }catch(Exception e){
            return messageHandler.errorMessage(21);
        }

        return "[OK]";
    }

    public String doJOIN(String queryToken, String [] tokens) throws IOException {
        tokens = tokenSpliter.splitToken4JOIN(tokens);

        // Get String [] Attributes
        String [] attributes = elementsSpliter.removeKeyWords(tokens, TokenKeeper.JOIN);
        // Assign table1, table2, attribute1 and attribute2 to each single String
        if(attributes.length == 4){
            String table1 = attributes[0];
            String table2 = attributes[1];
            String attribute1 = attributes[2];
            String attribute2 = attributes[3];
            // CHeck tables exist
            if(!(tableAccessor.checkTableExists(table1) && tableAccessor.checkTableExists(table2))){
                return messageHandler.errorMessage(7);
            }
            // Get attributes from tables
            String [] table1Attributes = tableAccessor.getTableAttributes(table1);
            String [] table2Attributes = tableAccessor.getTableAttributes(table2);
            String [] attribute1List = { attributes[2] };
            String [] attribute2List = { attributes[3] };
            // Check attribute1 exists in table1, attribute2 exists in table2
            if(!(tableAccessor.checkAttributeExist(attribute1List, table1Attributes)
                    && tableAccessor.checkAttributeExist(attribute2List, table2Attributes))){
                return messageHandler.errorMessage(17);
            }
            String joinedTable = tableAccessor.printJointedTable(table1, table2, attribute1, attribute2);
            return messageHandler.successMessage(10) +"\n" + joinedTable;
        } else {
            // attributes.length != 4
            return messageHandler.errorMessage(2);
        }

    }
}
