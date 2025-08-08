package edu.uob;

import java.io.*;
import java.util.*;

public class TableAccessor {
    private String storageFolderPath;
    private AddressGetter addressGetter;
    private final IndexAccessor indexAccessor;
    private final MessageHandler messageHandler;
    private final GrammarMaker grammarMaker;
    private final UpdateHelper updateHelper;
    private final DataOperator dataOperator;

    public TableAccessor(String storageFolderPath) {
        this.storageFolderPath = storageFolderPath;
        this.addressGetter = new AddressGetter(this.storageFolderPath);
        this.messageHandler = new MessageHandler();
        this.grammarMaker = new GrammarMaker();
        this.indexAccessor = new IndexAccessor(this.storageFolderPath);
        this.updateHelper = new UpdateHelper();
        this.dataOperator = new DataOperator();
    }

    public boolean checkTableExists(String tableName) {
        String tablePath = addressGetter.getTablePath(tableName);
        File table = new File(tablePath);
        return table.exists();
    }

    public boolean createTable(String tableName) {
        String dbPath = DataManipulator.currentDatabasePath;
        String tablePath = addressGetter.getTableWithDatabase(dbPath, tableName);
        File tableFile = new File(tablePath);
        try{
            if(tableFile.createNewFile()){
                return tableFile.exists();
            } else {
                return false;
            }
        }catch (IOException e){
            return false;
        }
    }


    public boolean deleteTable(String tableName) {

        String dbPath = DataManipulator.currentDatabasePath;

        String tablePath = addressGetter.getTableWithDatabase(dbPath, tableName);

        File tableFile = new File(tablePath);
        if(!tableFile.exists()){
            return false;
        }

        if(tableFile.delete()){
            if(tableFile.exists()){
                return false;
            }
            return true;
        } else {
            return false;
        }
    }
    public boolean checkAttributeExist(String [] tokens, String [] tableAttributes) throws IOException {
        Set<String> tableAttrSet = new HashSet<String>(Arrays.asList(tableAttributes));

        for(String token : tokens){
            if(!tableAttrSet.contains(token)){
                return false;
            }
        }
        return true;
    }

    public String updateTable(String tableName, String [] nameValueLit, String getConditionTable)throws IOException{
        String updatedTable = "[OK]";
        String [] attributeList = updateHelper.getAttributeList(nameValueLit);
        String [] valueList = updateHelper.getValueList(nameValueLit);

        ArrayList<String>finalTable = string2ArrayList(getConditionTable);
        int [] indexList = new int[attributeList.length];
        String [] headLine = finalTable.get(0).split("\\s+");

        // Convert ArrayList to String
        String result = String.join("\n", finalTable);

        // Find the index of the string
        int index = 0;
        for(int i = 0; i < attributeList.length; i++){
            for(int j = 0; j < headLine.length; j++){
                if(attributeList[i].equals(headLine[j])){
                    indexList[index] = j;
                    index++;
                }
            }
        }

        int rows = finalTable.size() - 1;
        for(int j = 1; j <= rows; j++){
            String [] row = finalTable.get(j).split("\t");
            for(int i = 0; i < indexList.length; i++){
                row[indexList[i]] = valueList[i];
                finalTable.set(j, String.join("\t", row));
            }
        }

        // Convert ArrayList to String
        result = String.join("\n", finalTable);

        updateData(1, tableName, finalTable);

        return updatedTable;
    }

    public void updateData(int checkAction, String tableName, ArrayList<String> filteredTable)throws IOException{
        String tablePath = addressGetter.getTablePath(tableName);
        File file = new File(tablePath);
        if (!file.exists()) {
            return;
        }

        // Write content into lines
        ArrayList<String> originalTable = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(tablePath))) {
            String line;
            while((line = reader.readLine()) != null){
                originalTable.add(line);
            }

        } catch (IOException e) {
            return;
        }

        if(originalTable.isEmpty()){
            return;
        }

        // Convert ArrayList to String
        String result = String.join("\n", originalTable);

        if(checkAction == 1){
            //update data
            // Loop through original table, if it finds the index the same with filtered table
               // replace the row[i] with filteredTable[j]
            if(!filteredTable.isEmpty() && filteredTable.size() <= originalTable.size()){
                for(int i = 0; i <  originalTable.size(); i++){
                    for(int j = 0; j < filteredTable.size(); j++){
                        String [] originalRow = originalTable.get(i).split("\t");
                        String [] filteredRow = filteredTable.get(j).split("\t");
                        String originalIndex = originalRow[0];
                        String filteredIndex = filteredRow[0];
                        if(originalIndex.equals(filteredIndex)){
                            originalTable.set(i, String.join("\t", filteredRow));
                        }
                    }
                }
            }else{
                return;
            }
        }else if (checkAction == 2){
            // Delete dada
            // Loop through original table, if it finds the index the same with filtered table
            // delete the row in original table
            Iterator<String> iterator = originalTable.iterator();
            while(iterator.hasNext()){
                String [] originalRow = iterator.next().split("\t");
                String originalIndex = originalRow[0];

                for(String filteredRowStr : filteredTable){
                    String [] filteredRow = filteredRowStr.split("\t");
                    String filteredIndex = filteredRow[0];

                    if(originalIndex.equals(filteredIndex)){
                        iterator.remove();
                        break;
                    }
                }
            }
            String [] tableNameList = getTableAttributes(tableName);
            originalTable = addHeadLine(tableNameList, originalTable);
        }else{
            return;
        }

        // Convert ArrayList to String
        result = String.join("\n", originalTable);

        try(Writer writer = new BufferedWriter(new FileWriter(tablePath))){
            for(String modifiedLine : originalTable){
                writer.write(modifiedLine + "\n");
            }
        } catch (Exception e) {
            return;
        }
    }

    public boolean AddCertainAttribute(String tableName, String newAttribute) throws IOException {
        String tablePath = addressGetter.getTablePath(tableName);

        File file = new File(tablePath);
        if (!file.exists()) {
            return false;
        }

        List<String> lines = new ArrayList<>();
        // Write content into lines
        try (BufferedReader reader = new BufferedReader(new FileReader(tablePath))) {
            String line;
            while((line = reader.readLine()) != null){
                lines.add(line);
            }

        } catch (IOException e) {
            return false;
        }

        // Add new attribute
        if(!lines.isEmpty()){
            lines.set(0, lines.get(0) + "\t" + newAttribute);
        }

        for(int i = 1; i < lines.size(); i++){
            lines.set(i, lines.get(i) + "\t");
        }

        // Write back to the file
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(tablePath))){
            for(String modifiedLine : lines){
                writer.write(modifiedLine + "\n");
            }
        }catch (IOException e){
            return false;
        }

        return true;
    }

    public String printTable(String tableName) throws IOException {
        String filePath = addressGetter.getTablePath(tableName);
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        StringBuilder tableContent = new StringBuilder();


        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line = reader.readLine()) != null) {
                tableContent.append(line).append("\n");
            }

        } catch (IOException e) {
        }

        return tableContent.toString().trim();
    }

    public String printTableCertainAttr(String getConditionTable, String [] wildAttributes) throws IOException {

        ArrayList<String> originalTable = new ArrayList<>();
        ArrayList<String> finalTable = new ArrayList<>();
        originalTable = string2ArrayList(getConditionTable);
        if(wildAttributes.length == 1 && wildAttributes[0].equals("*")){
            return getConditionTable;
        }

        int [] indexList = new int[wildAttributes.length];
        String [] headLine = originalTable.get(0).split("\\s+");

        // Find the index of the string
        int index = 0;
        for(int i = 0; i < wildAttributes.length; i++){
            for(int j = 0; j < headLine.length; j++){
                if(wildAttributes[i].equals(headLine[j])){
                    indexList[index] = j;
                    index++;
                }
            }
        }

        int dataNum = originalTable.size() - 1;

        for(int i = 1; i <= dataNum; i++){
            String[] row = originalTable.get(i).split("\\s+");
            StringBuilder selectedRow = new StringBuilder();

            for(int j = 0; j < indexList.length; j++){
                int headLineIndex = indexList[j];
                selectedRow.append(row[headLineIndex]);
                selectedRow.append("\t");
            }
            finalTable.add(selectedRow.toString());
        }

        finalTable = addHeadLine(wildAttributes, finalTable);
        finalTable = indexAccessor.updateIndex(finalTable);
        // Convert ArrayList to String
        String result = String.join("\n", finalTable);
        return result;
    }

    public String printBaseConditionTable (boolean updateIndex, String tableName, String[] conditions){
        ArrayList<String> conditionList = new ArrayList<>();
        try{
            conditionList = separateBaseCondition(tableName, conditions);
            if(conditionList.size() == 1 || conditionList.size() == 3) {
                String finalTable = findBaseConditionTable(updateIndex, tableName, conditionList);
                return finalTable;
            } else {
                return " ";
            }
        }catch (Exception e){
            return null;
        }
    }

    private ArrayList<String> separateBaseCondition(String tableName, String[] condition){
        // Separate condition into lines recursively
        ArrayList<String> conditionList = new ArrayList<>();
        int conditionAmount = 0;
        // Check if the string is boolean operator
        for(String token : condition){
            if(grammarMaker.checkBooleanOperator(token)){
                // If is, put the string into conditionList
                conditionList.add(token);
                conditionAmount++;
            }
        }

        // If the string is a plaintext
        for(int i = 0; i < condition.length; i++){
            // If the string is a plaintext
            if(grammarMaker.checkPlainText(condition[i])){
                // Check if conditionLis(index + 1) is Comparator and conditionLis(index + 2) is Value
                if(i >= 0 && (i + 2) < condition.length){
                    if(grammarMaker.checkComparator(condition[i+1]) && grammarMaker.checkValue(condition[i+2])){
                        // If is, put the string into conditionList
                        String conditionFound =condition[i] + " " + condition[i+1] + " " + condition[i+2];
                        conditionList.add(conditionFound);
                        conditionAmount++;
                    }
                }
            }
        }
        return conditionList;
    }

    private String findBaseConditionTable (boolean updateIndex, String tableName, ArrayList<String> conditionList) throws IOException{
        String finalTable = null;
        if(conditionList.size() == 0){
            return null;
        }
        // Read the table filteredAttributeTable
        // Check if conditionList.get(0) contains boolean operator
        boolean booleanOperatorFound = false;
        for(String token : conditionList.get(0).split("\t")){
            if(grammarMaker.checkBooleanOperator(token)){
                booleanOperatorFound = true;
            }
        }

        // Check if conditionList.size() == 1 and conditionList.get(0) doesn't contain boolean operator : (A == 5)
        if(conditionList.size() == 1){
            if(booleanOperatorFound) {
                return null;
            } else {
                String [] aCondition = conditionList.get(0).split(" ");
                finalTable = filteredTable(updateIndex, tableName, aCondition);
                return finalTable;
            }
        } else if (conditionList.size() == 3){
            // Check if the in the list is more than 1 and conditionList[0] contains boolean operator : (A == 5) AND (B <=4)
            if(!booleanOperatorFound) {
                return null;
            } else {
                // Make sure the condition is not duplicated
                if(conditionList.get(1).equals(conditionList.get(2))){
                    return null;
                }
                // Convert conditionList(0) and conditionList(1) to String [] condition1 and String [] condition2
                String [] condition1 = conditionList.get(1).split("\\s+");
                String [] condition2 = conditionList.get(2).split("\\s+");

                // Get the table for both conditions
                String conditionTable1 = filteredTable(false, tableName, condition1);
                String conditionTable2 = filteredTable(false, tableName, condition2);

                // Check conditionList(0) is AND or OR
                if(conditionList.get(0).equals("AND")){
                    finalTable = andTable (conditionTable1, conditionTable2);
                    return finalTable;
                }else{
                    finalTable = orTable (conditionTable1, conditionTable2);
                    return finalTable;
                }
            }
        } else {
            return null;
        }
    }

    private String andTable (String conditionTable1, String conditionTable2){

        ArrayList<String> table1 = string2ArrayList(conditionTable1);
        ArrayList<String> table2 = string2ArrayList(conditionTable2);
        ArrayList<String> finalTable = new ArrayList<>();

        for(int i = 0; i < table1.size(); i++){
            for(int j = 0; j < table2.size(); j++){
                String [] row1 = table1.get(i).split("\t");
                String [] row2 = table2.get(j).split("\t");
                if(row1[0].equals(row2[0]) && row1[1].equals(row2[1])){
                    finalTable.add(String.join("\t", row1));
                }
            }
        }

        // Update index
        try{
            finalTable = indexAccessor.updateIndex(finalTable);
        }catch (Exception e){
            return null;
        }

        String result = String.join("\n", finalTable);
        return result;
    }
    private String orTable (String conditionTable1, String conditionTable2){
        ArrayList<String> finalTable = string2ArrayList(conditionTable1);
        ArrayList<String> table2 = string2ArrayList(conditionTable2);
        String [] headLine = finalTable.get(0).split("\t");
        finalTable.remove(0);
        table2.remove(0);

        finalTable.addAll(table2);
        Set<String> found = new HashSet<>();
        ArrayList<String> finalTableWithoutDuplicates = new ArrayList<>();

        for(String row : finalTable){
            String [] rowArray = row.split("\t");
            String currentIndex = rowArray[0];
            if(!found.contains(currentIndex)){
                finalTableWithoutDuplicates.add(row);
                found.add(currentIndex);
            }
        }
        finalTable = finalTableWithoutDuplicates;

        String result = String.join("\n", finalTable);

        finalTable = addHeadLine(headLine, finalTable);
        // Update index
        try{
            finalTable = indexAccessor.updateIndex(finalTable);
        }catch (Exception e){
            return null;
        }

        result = String.join("\n", finalTable);

        return result;
    }

    private String filteredTable(boolean updateIndex, String tableName, String [] aCondition) throws IOException{
        ArrayList<String> finalTable = new ArrayList<>();
        finalTable = string2ArrayList(printTable(tableName));
        String [] headLine = finalTable.get(0).split("\t");
        String attribute = aCondition[0];
        String operator = aCondition[1];
        String value = aCondition[2];
        int attributeIndex = 0;

        // Check the attribute is in the headLine and attributeIndex
        int headLineLength = headLine.length;
        boolean attributeFound = false;
        for(int i = 0; i < headLine.length; i++){

            if(headLine[i].contains(attribute)){
                attributeFound  = true;
                attributeIndex = i;
            }
        }
        if(!attributeFound){
            return null;
        }

        // Check value and operator are compatible
        String valueType = checkValueType(value);
        if(!checkOperatorValid(valueType, operator)){
            return null;
        }

        // Remove unqualified data
        finalTable = RemoveUnqualifiedData(updateIndex, valueType, attributeIndex, aCondition, headLine, finalTable);
        // Convert ArrayList to String
        String result = String.join("\n", finalTable);
        return result;
    }

    private boolean checkOperatorValid (String valueType, String operator){
        switch(valueType){
            case "StringLiteral":
                if(!(operator.equals("==") ||operator.equals("!=") || operator.equalsIgnoreCase("LIKE"))){
                    return false;
                }
                return true;
            case "BooleanLiteral":
                // Check if operator type is valid
                if(!(operator.equals("==") || operator.equals("!="))) {
                    return false;
                }
                return true;
            case "IntegerLiteral":
                // Check if operator type is valid
                if(!(grammarMaker.checkComparator(operator) && !operator.equals("LIKE"))) {
                    return false;
                }
                return true;
            case "FloatLiteral":
                // Check if operator type is valid
                if(!(grammarMaker.checkComparator(operator) && !operator.equals("LIKE"))) {
                    return false;
                }
                return true;
            case "Null":
                // Check if operator type is valid
                if(!(operator.equals("==") || operator.equals("!="))) {
                    return false;
                }
                return true;
            default:
                return false;
        }
    }

    private ArrayList<String> RemoveUnqualifiedData(boolean updateIndex, String valueType, int attributeIndex, String [] aCondition, String [] headLine, ArrayList<String> finalTable) throws IOException{
        finalTable = dataOperator.calculateData(valueType, attributeIndex, aCondition, finalTable);

        finalTable = addHeadLine(headLine, finalTable);

        if(updateIndex){
            // Update index
            try{
                finalTable = indexAccessor.updateIndex(finalTable);
            }catch (Exception e){
                return null;
            }
            return finalTable;
        }else{
            return finalTable;
        }
    }

    private ArrayList<String> addHeadLine(String [] headLine, ArrayList<String> finalTable){
        String headLineRow = String.join("\t", headLine);
        finalTable.add(0, headLineRow);
        return finalTable;
    }

    private String checkValueType (String value){
        if(value.contains("\'")) {
            String checkStringLiteral = value.replace("\'", "");
            if (grammarMaker.checkStringLiteral(checkStringLiteral)) {
                return "StringLiteral";
            }
        }
        if(grammarMaker.checkBooleanLiteral(value)){
            return "BooleanLiteral";
        }
        if(grammarMaker.checkIntegerLiteral(value)){
            return "IntegerLiteral";
        }
        if(grammarMaker.checkFloatLiteral(value)){
            return "FloatLiteral";
        }
        if(value.equals("NULL")){
            return "Null";
        }
        return "Not A Value";
    }

    public ArrayList<String> string2ArrayList(String table){

        ArrayList<String> finalTable = new ArrayList<>();
        String [] rows = table.split("\n");
        for(String row : rows){
            ArrayList<String> rowElement = new ArrayList<>(Arrays.asList(row.split("\n")));
            finalTable.addAll(rowElement);
        }
        for(String row : finalTable){
        }
        return finalTable;
    }

    public String printMultipleConditionTable (String tableName, String[] conditions){
        String finalTable  = null;
        return finalTable;
    }

    private ArrayList<String> separateMultipleCondition(String tableName, String[] condition){
        ArrayList<String> list = new ArrayList<>();
        return list;
    }

    public String printJointedTable(String tableName1, String tableName2, String attribute1, String attribute2) throws IOException {
        String tablePath1 = addressGetter.getTablePath(tableName1);
        File table1 = new File(tablePath1);
        if (!table1.exists()) {
            return null;
        }

        String tablePath2 = addressGetter.getTablePath(tableName2);
        File table2 = new File(tablePath2);
        if (!table2.exists()) {
            return null;
        }
        List<String[]> tableData1 = readTableData(table1);
        List<String[]> tableData2 = readTableData(table2);

        int index1 = getColumnIndex(tableData1.get(0), attribute1);
        int index2 = getColumnIndex(tableData2.get(0), attribute2);

        if(index1 == -1 || index2 == -1){
            return null;
        }

        StringBuilder joinedTable = new StringBuilder();

        // Write attribute names of table1 and table2 into joinedTable except of ID, attribute1 and attribute2
        String [] header1 = tableData1.get(0);
        String [] header2 = tableData2.get(0);
        for(int i = 1; i < header1.length; i++){
            if(!header1[i].equals("ID") && !header1[i].equals(attribute1)){
                joinedTable.append(tableName1).append(".").append(header1[i]).append("\t");
            }
        }

        for(int i = 1; i < header2.length; i++){
            if(!header2[i].equals("ID") && !header2[i].equals(attribute2)){
                joinedTable.append(tableName2).append(".").append(header2[i]).append("\t");
            }
        }

        joinedTable.append("\n");

        Map<String, String[]> table2Map = new HashMap<>();
        // Check if each row[index1] in table1 equals each row[index2] in table2
        for(String[] row1 : tableData1){
            for(String[] row2 : tableData2){
                if(row1[index1].equals(row2[index2])){
                    for(int i = 1; i < row1.length; i++){
                        if(!header1[i].equals("ID") && !header1[i].equals(attribute1)){
                            joinedTable.append(row1[i]).append("\t");
                        }
                    }
                    for(int i = 1; i < row2.length; i++){
                        if(!header2[i].equals("ID") && !header2[i].equals(attribute2)){
                            joinedTable.append(row2[i]).append("\t");
                        }
                    }
                    joinedTable.append("\n");
                }else{
                }
            }
        }
        String finalTable = joinedTable.toString();
        ArrayList<String> finalTable1 = string2ArrayList(finalTable);
        indexAccessor.updateIndex(finalTable1);
        String result = String.join("\n", finalTable1);

        return result;
    }

    private int getColumnIndex(String [] headerRow, String attribute){
        for(int i = 0; i < headerRow.length; i++){
            if(headerRow[i].equals(attribute)){
                return i;
            }
        }
        return -1;
    }
    private List<String[]> readTableData(File table) throws IOException {
        List<String[]> tableData = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(table))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] rowData = line.split("\t");
                tableData.add(rowData);
            }
        }catch (IOException e){
            return null;
        }
        return tableData;
    }


    public void writeTable(String tableName, List<String> data) throws IOException {

        String dbPath = DataManipulator.currentDatabasePath;
        String tablePath = addressGetter.getTableWithDatabase(dbPath, tableName);
        File tableFile = new File(tablePath);
        if (!tableFile.exists()) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tablePath, true))) {
            for(String row : data) {
                writer.write(row);
                writer.newLine();
            }
            writer.flush();
        } catch (IOException e) {
        }
    }

    public boolean addTableAttribute(String [] allTokens) {
        String tableName = allTokens[0];
        allTokens[0] = "ID";
        List<String> testList = Arrays.asList(allTokens);
        String row = String.join("\t", testList);
        List<String> rowList = new ArrayList<>();
        rowList.add(row);
        try{
            writeTable(tableName, rowList);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public int getAttributeAmount(String tableName) {
        int amount = 0;
        String filePath = addressGetter.getTablePath(tableName);
        File file = new File(filePath);
        if (!file.exists()) {
            return 0;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String firstLine = reader.readLine();
            if(firstLine != null){
                String[] attributes = firstLine.split("\t");
                amount = attributes.length;
            }

        } catch (IOException e) {
        }
        return amount;
    }

    public String [] getTableAttributes(String tableName) {
        String filePath = addressGetter.getTablePath(tableName);
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        String[] attributes = new String[0];
        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String firstLine = reader.readLine();
            if(firstLine != null){
                attributes = firstLine.split("\t");
            }

        } catch (IOException e) {
        }
        return attributes;
    }

    public int checkAttributeIndex(String [] Token, String tableName) {
        int index = -1;
        String filePath = addressGetter.getTablePath(tableName);
        File file = new File(filePath);

        if (!file.exists()) {
            return index;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String firstLine = reader.readLine();

            if(firstLine != null){
                String[] attributes = firstLine.split("\t");
                for(int i = 0; i < attributes.length; i++){
                    if(attributes[i].equals(Token[0])){
                        index = i;
                    }
                }
            }
        } catch (IOException e) {
        }
        return index;
    }

    public boolean dropAttribute(int index, String tableName) throws IOException {
        String tablePath = addressGetter.getTablePath(tableName);
        File file = new File(tablePath);

        if (!file.exists()) {
            return false;
        }

        List<String> modifiedLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            while((line = reader.readLine()) != null){
                String [] columns = line.split("\t");

                if(index < 0 || index >= columns.length){
                    return false;
                }

                List<String> updatedColumns = new ArrayList<>(Arrays.asList(columns));
                updatedColumns.remove(index);

                modifiedLines.add(String.join("\t", updatedColumns));
            }

        } catch (IOException e) {
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            for(String line : modifiedLines){
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
        }
        return true;
    }
}
