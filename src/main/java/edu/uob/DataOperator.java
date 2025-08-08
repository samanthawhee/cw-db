package edu.uob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class DataOperator  {
    private final GrammarMaker grammarMaker;
    public DataOperator() {
        this.grammarMaker = new GrammarMaker();
    }

    public ArrayList<String> calculateData(String valueType, int attributeIndex, String [] aCondition, ArrayList<String> finalTable){
        String attribute = aCondition[0];
        String operator = aCondition[1];
        String value = aCondition[2];
        int intValue;
        int intData;
        float floatValue;
        float floatData;
        String checkString = value.replace("'", "");

        // Remove headLine
        finalTable.remove(0);
        operator = operator.toUpperCase();

        switch(operator){
            case "==":
                if(valueType.equals("BooleanLiteral")) {
                    Iterator<String> iterator00 = finalTable.iterator();
                    while (iterator00.hasNext()) {
                        String row = iterator00.next();
                        String[] rowData = row.split("\t");
                        if (!rowData[attributeIndex].equalsIgnoreCase(value)) {
                            iterator00.remove();
                        }
                    }
                    break;
                }else if (grammarMaker.checkStringLiteral(checkString)){
                    Iterator<String> iterator00 = finalTable.iterator();
                    while (iterator00.hasNext()) {
                        String row = iterator00.next();
                        String[] rowData = row.split("\t");
                        if (!rowData[attributeIndex].equalsIgnoreCase(value)) {
                            iterator00.remove();
                        }
                    }
                }else{
                    Iterator<String> iterator02 = finalTable.iterator();
                    while(iterator02.hasNext()){
                        String row = iterator02.next();
                        String [] rowData = row.split("\t");
                        String checkValue = rowData[attributeIndex];
                        // Not I and F
                        if(!(grammarMaker.checkIntegerLiteral(checkValue)
                                || grammarMaker.checkFloatLiteral(checkValue))){
                            iterator02.remove();
                        }else if (grammarMaker.checkFloatLiteral(checkValue) && grammarMaker.checkFloatLiteral(value)){
                            // F and F
                            floatData = Float.parseFloat(rowData[attributeIndex]);
                            floatValue = Float.parseFloat(value);
                            if(!(floatData == floatValue)){
                                iterator02.remove();
                            }
                        }else if (grammarMaker.checkIntegerLiteral(checkValue) && grammarMaker.checkIntegerLiteral(value)){
                            // I and I
                            intData = Integer.parseInt(rowData[attributeIndex]);
                            intValue = Integer.parseInt(value);
                            if(!(intData == intValue)){
                                iterator02.remove();
                            }
                        }else if (grammarMaker.checkFloatLiteral(checkValue) && grammarMaker.checkIntegerLiteral(value)) {
                            // F and I
                            floatData = Float.parseFloat(rowData[attributeIndex]);
                            intValue = Integer.parseInt(value);
                            if(!(floatData == intValue)){
                                iterator02.remove();
                            }
                        }else if (grammarMaker.checkIntegerLiteral(checkValue) && grammarMaker.checkFloatLiteral(value)) {
                            // I and F
                            intData = Integer.parseInt(rowData[attributeIndex]);
                            floatValue = Float.parseFloat(value);
                            if((intData == floatValue)){
                                iterator02.remove();
                            }
                        }else {
                            Iterator<String> iterator01 = finalTable.iterator();
                            while(iterator01.hasNext()){
                                row = iterator01.next();
                                rowData = row.split("\t");
                                if(!rowData[attributeIndex].equals(value)){
                                    iterator01.remove();
                                }
                            }
                        }
                    }
                }
                break;
            case "!=":
                if(valueType.equals("BooleanLiteral")) {
                    Iterator<String> iterator00 = finalTable.iterator();
                    while (iterator00.hasNext()) {
                        String row = iterator00.next();
                        String[] rowData = row.split("\t");
                        if (rowData[attributeIndex].equalsIgnoreCase(value)) {
                            iterator00.remove();
                        }
                    }
                    break;
                }else if (grammarMaker.checkStringLiteral(checkString)){
                    Iterator<String> iterator00 = finalTable.iterator();
                    while (iterator00.hasNext()) {
                        String row = iterator00.next();
                        String[] rowData = row.split("\t");
                        if (rowData[attributeIndex].equalsIgnoreCase(value)) {
                            iterator00.remove();
                        }
                    }
                break;
                }else{
                    Iterator<String> iterator02 = finalTable.iterator();
                    while(iterator02.hasNext()){
                        String row = iterator02.next();
                        String [] rowData = row.split("\t");
                        String checkValue = rowData[attributeIndex];
                        // Not I and F
                        if(!(grammarMaker.checkIntegerLiteral(checkValue)
                                || grammarMaker.checkFloatLiteral(checkValue))){
                            iterator02.remove();
                        }else if (grammarMaker.checkFloatLiteral(checkValue) && grammarMaker.checkFloatLiteral(value)){
                            // F and F
                            floatData = Float.parseFloat(rowData[attributeIndex]);
                            floatValue = Float.parseFloat(value);
                            if(!(floatData != floatValue)){
                                iterator02.remove();
                            }
                        }else if (grammarMaker.checkIntegerLiteral(checkValue) && grammarMaker.checkIntegerLiteral(value)){
                            // I and I
                            intData = Integer.parseInt(rowData[attributeIndex]);
                            intValue = Integer.parseInt(value);
                            if(!(intData != intValue)){
                                iterator02.remove();
                            }
                        }else if (grammarMaker.checkFloatLiteral(checkValue) && grammarMaker.checkIntegerLiteral(value)) {
                            // F and I
                            floatData = Float.parseFloat(rowData[attributeIndex]);
                            intValue = Integer.parseInt(value);
                            if(!(floatData != intValue)){
                                iterator02.remove();
                            }
                        }else if (grammarMaker.checkIntegerLiteral(checkValue) && grammarMaker.checkFloatLiteral(value)) {
                            // I and F
                            intData = Integer.parseInt(rowData[attributeIndex]);
                            floatValue = Float.parseFloat(value);
                            if(!(intData != floatValue)){
                                iterator02.remove();
                            }
                        }
                    }
                }
                break;
            case ">=":
                Iterator<String> iterator02 = finalTable.iterator();
                while(iterator02.hasNext()){
                    String row = iterator02.next();
                    String [] rowData = row.split("\t");
                    String checkValue = rowData[attributeIndex];
                    // Not I and F
                    if(!(grammarMaker.checkIntegerLiteral(checkValue)
                            || grammarMaker.checkFloatLiteral(checkValue))){
                        iterator02.remove();
                    }else if (grammarMaker.checkFloatLiteral(checkValue) && grammarMaker.checkFloatLiteral(value)){
                        // F and F
                        floatData = Float.parseFloat(rowData[attributeIndex]);
                        floatValue = Float.parseFloat(value);
                        if(!(floatData >= floatValue)){
                            iterator02.remove();
                        }
                    }else if (grammarMaker.checkIntegerLiteral(checkValue) && grammarMaker.checkIntegerLiteral(value)){
                        // I and I
                        intData = Integer.parseInt(rowData[attributeIndex]);
                        intValue = Integer.parseInt(value);
                        if(!(intData >= intValue)){
                            iterator02.remove();
                        }
                    }else if (grammarMaker.checkFloatLiteral(checkValue) && grammarMaker.checkIntegerLiteral(value)) {
                        // F and I
                        floatData = Float.parseFloat(rowData[attributeIndex]);
                        intValue = Integer.parseInt(value);
                        if(!(floatData >= intValue)){
                            iterator02.remove();
                        }
                    }else if (grammarMaker.checkIntegerLiteral(checkValue) && grammarMaker.checkFloatLiteral(value)) {
                        // I and F
                        intData = Integer.parseInt(rowData[attributeIndex]);
                        floatValue = Float.parseFloat(value);
                        if(!(intData >= floatValue)){
                            iterator02.remove();
                        }
                    }
                }
                break;
            case "<=":
                Iterator<String> iterator03 = finalTable.iterator();
                while(iterator03.hasNext()){
                    String row = iterator03.next();
                    String [] rowData = row.split("\t");
                    String checkValue = rowData[attributeIndex];
                    // Not I and F
                    if(!(grammarMaker.checkIntegerLiteral(checkValue)
                            || grammarMaker.checkFloatLiteral(checkValue))){
                        iterator03.remove();
                    }else if (grammarMaker.checkFloatLiteral(checkValue) && grammarMaker.checkFloatLiteral(value)){
                        // F and F
                        floatData = Float.parseFloat(rowData[attributeIndex]);
                        floatValue = Float.parseFloat(value);
                        if(!(floatData <= floatValue)){
                            iterator03.remove();
                        }
                    }else if (grammarMaker.checkIntegerLiteral(checkValue) && grammarMaker.checkIntegerLiteral(value)){
                        // I and I
                        intData = Integer.parseInt(rowData[attributeIndex]);
                        intValue = Integer.parseInt(value);
                        if(!(intData <= intValue)){
                            iterator03.remove();
                        }
                    }else if (grammarMaker.checkFloatLiteral(checkValue) && grammarMaker.checkIntegerLiteral(value)) {
                        // F and I
                        floatData = Float.parseFloat(rowData[attributeIndex]);
                        intValue = Integer.parseInt(value);
                        if(!(floatData <= intValue)){
                            iterator03.remove();
                        }
                    }else if (grammarMaker.checkIntegerLiteral(checkValue) && grammarMaker.checkFloatLiteral(value)) {
                        // I and F
                        intData = Integer.parseInt(rowData[attributeIndex]);
                        floatValue = Float.parseFloat(value);
                        if(!(intData <= floatValue)){
                            iterator03.remove();
                        }
                    }
                }
                break;
            case ">":
                Iterator<String> iterator04 = finalTable.iterator();
                while(iterator04.hasNext()){
                    String row = iterator04.next();
                    String [] rowData = row.split("\t");
                    String checkValue = rowData[attributeIndex];
                    // Not I and F
                    if(!(grammarMaker.checkIntegerLiteral(checkValue)
                            || grammarMaker.checkFloatLiteral(checkValue))){
                        iterator04.remove();
                    }else if (grammarMaker.checkFloatLiteral(checkValue) && grammarMaker.checkFloatLiteral(value)){
                        // F and F
                        floatData = Float.parseFloat(rowData[attributeIndex]);
                        floatValue = Float.parseFloat(value);
                        if(!(floatData > floatValue)){
                            iterator04.remove();
                        }
                    }else if (grammarMaker.checkIntegerLiteral(checkValue) && grammarMaker.checkIntegerLiteral(value)){
                        // I and I
                        intData = Integer.parseInt(rowData[attributeIndex]);
                        intValue = Integer.parseInt(value);
                        if(!(intData > intValue)){
                            iterator04.remove();
                        }
                    }else if (grammarMaker.checkFloatLiteral(checkValue) && grammarMaker.checkIntegerLiteral(value)) {
                        // F and I
                        floatData = Float.parseFloat(rowData[attributeIndex]);
                        intValue = Integer.parseInt(value);
                        if(!(floatData > intValue)){
                            iterator04.remove();
                        }
                    }else if (grammarMaker.checkIntegerLiteral(checkValue) && grammarMaker.checkFloatLiteral(value)) {
                        // I and F
                        intData = Integer.parseInt(rowData[attributeIndex]);
                        floatValue = Float.parseFloat(value);
                        if(!(intData > floatValue)){
                            iterator04.remove();
                        }
                    }
                }
                break;
            case "<":
                Iterator<String> iterator05 = finalTable.iterator();
                while(iterator05.hasNext()){
                    String row = iterator05.next();
                    String [] rowData = row.split("\t");
                    String checkValue = rowData[attributeIndex];
                    // Not I and F
                    if(!(grammarMaker.checkIntegerLiteral(checkValue)
                            || grammarMaker.checkFloatLiteral(checkValue))){
                        iterator05.remove();
                    }else if (grammarMaker.checkFloatLiteral(checkValue) && grammarMaker.checkFloatLiteral(value)){
                        // F and F
                        floatData = Float.parseFloat(rowData[attributeIndex]);
                        floatValue = Float.parseFloat(value);
                        if(!(floatData < floatValue)){
                            iterator05.remove();
                        }
                    }else if (grammarMaker.checkIntegerLiteral(checkValue) && grammarMaker.checkIntegerLiteral(value)){
                        // I and I
                        intData = Integer.parseInt(rowData[attributeIndex]);
                        intValue = Integer.parseInt(value);
                        if(!(intData < intValue)){
                            iterator05.remove();
                        }
                    }else if (grammarMaker.checkFloatLiteral(checkValue) && grammarMaker.checkIntegerLiteral(value)) {
                        // F and I
                        floatData = Float.parseFloat(rowData[attributeIndex]);
                        intValue = Integer.parseInt(value);
                        if(!(floatData < intValue)){
                            iterator05.remove();
                        }
                    }else if (grammarMaker.checkIntegerLiteral(checkValue) && grammarMaker.checkFloatLiteral(value)) {
                        // I and F
                        intData = Integer.parseInt(rowData[attributeIndex]);
                        floatValue = Float.parseFloat(value);
                        if(!(intData < floatValue)){
                            iterator05.remove();
                        }
                    }
                }
                break;
            case "LIKE":
                value = value.replaceAll("^'|'$", "");
                Iterator<String> iterator06 = finalTable.iterator();
                while(iterator06.hasNext()){
                    String row = iterator06.next();
                    String [] rowData = row.split("\t");
                    if(!rowData[attributeIndex].contains(value)){
                        iterator06.remove();
                    }
                }
                break;
            default:
                return null;
        }

    return finalTable;
    }
}


