package edu.uob;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class IndexAccessor {
    private final String storageFolderPath;

    public IndexAccessor(String storageFolderPath) {
        this.storageFolderPath = storageFolderPath;
    }

    public List<String> appendIndexToString(String tableName, List<String> data) throws IOException {
        TableAccessor tableAccessor = new TableAccessor(this.storageFolderPath);
        AddressGetter addressGetter = new AddressGetter(this.storageFolderPath);
        String tablePath = addressGetter.getTablePath(tableName);
        File file = new File(tablePath);

        if (!file.exists()) {
            return data;
        }
        List<String> updatedDataList = new ArrayList<>();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tablePath, true))) {
            int nextIndex = getCurrentDataAmount(tableName) + 1 ;
            int dataNumber = data.size();

            StringBuilder dataBuilder = new StringBuilder();
            dataBuilder.append(nextIndex);

            for(int i = 0 ; i < dataNumber ; i++) {
                dataBuilder.append("\t").append(data.get(i));
            }
            String updatedData = dataBuilder.toString();
            updatedDataList.add(updatedData);

            writer.write(updatedData);
            writer.newLine();
        } catch (IOException e) {
        }
        return updatedDataList;
    }

    public ArrayList<String> updateIndex (ArrayList<String> table) throws IOException {
        int dataNumber = table.size() - 1; // minus header line
        String [] headLine = table.get(0).split("\\s+");
        int attributeNum = headLine.length;

        if(headLine[0].contains("ID") || headLine[0].contains("id")) {
            // If the first column is id, update the element from [1]
            for (int i = 1; i <= dataNumber; i++) {
                String [] row = table.get(i).split("\t");
                row[0] = String.valueOf(i);
                table.set(i, String.join("\t", row));
            }
        }else{
            // Insert a column to table
            ArrayList<String> newTable = new ArrayList<>();
            // add "id" into first element of table.get(0)
            newTable.add("ID\t" + table.get(0));
            // Add new index and append each row from table
            for (int i = 1; i < table.size(); i++) {
                newTable.add(i + "\t" + table.get(i));
            }
            table = newTable;
        }

        return table;
    }

    private String [] prepenString(int attributeNum, String[] line, String newElement) {
        String [] newLine = new String[attributeNum + 1];
        newLine[0] =  newElement;
        System.arraycopy(line, 0, newLine, 1, attributeNum);
        return newLine;
    }

    public int getCurrentDataAmount(String tableName) throws IOException {
        TableAccessor tableAccessor = new TableAccessor(this.storageFolderPath);
        AddressGetter addressGetter = new AddressGetter(this.storageFolderPath);
        String filePath = addressGetter.getTablePath(tableName);
        File file = new File(filePath);
        if (!file.exists()) {
            return 0;
        }


        String lastLine = null;
        int lastIndex = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;
            while((line = reader.readLine()) != null){
                lastLine = line;
            }

            if(lastLine == null || lastLine.trim().isEmpty()) {
                return 0;
            }

            String [] allColumns = lastLine.split("\\s+");
            if(allColumns[0].equals("ID")){
                lastIndex = 0;
            } else {
                lastIndex = Integer.parseInt(allColumns[0]);
            }

        } catch (IOException e) {
        }
        return lastIndex;
    }
}
