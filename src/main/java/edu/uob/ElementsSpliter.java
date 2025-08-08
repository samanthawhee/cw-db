package edu.uob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ElementsSpliter extends GrammarMaker{


    public String [] splitElements(String [] allTokens, String [] queryKeyWordsList) {
        List<String> temp = new ArrayList<>();

        for (String token : allTokens) {
            StringBuilder currentToken = new StringBuilder();

            for(char c : token.toCharArray()) {
                String str = String.valueOf(c);

                if(Arrays.asList(queryKeyWordsList).contains(str)) {
                    if(currentToken.length() > 0) {
                        temp.add(currentToken.toString());
                        currentToken.setLength(0);
                    }
                    temp.add(str);
                } else {
                    currentToken.append(c);
                }
            }

            if(currentToken.length() > 0) {
                temp.add(currentToken.toString());
            }
        }
        String[] tableAttribute = temp.toArray(new String[temp.size()]);
        return tableAttribute;
    }

    // Remove the attributes which exist in String [] queryKeyWordsList from String [] tableAttribute
    public String [] splitAttribute(String [] tableAttribute, String [] queryKeyWordsList) {
        List<String> filteredTokens = new ArrayList<>();
        for(String token : tableAttribute) {
            if((!Arrays.asList(queryKeyWordsList).contains(token))) {
                filteredTokens.add(token);
            }
        }
        String [] filteredAttributes = filteredTokens.toArray(new String[0]);
        return filteredAttributes;
    }

    public String [] removeKeyWords(String [] tableAttribute, String [] queryKeyWordsList) {
        List<String> filteredTokens = new ArrayList<>();
        for(String token : tableAttribute) {
            boolean isExcluded = false;
            for(String queryKeyWord : queryKeyWordsList) {
                if(token.equals(queryKeyWord)) {
                    isExcluded = true;
                    break;
                }
            }
            if(!isExcluded) {
                filteredTokens.add(token);
            }
        }
        String [] filteredAttributes = filteredTokens.toArray(new String[0]);
        return filteredAttributes;
    }

    public String [] splitOperators(String [] allTokens, String [] queryKeyWordsList){
        List<String> temp = new ArrayList<>();

        List<String> storedOperators = Arrays.asList(queryKeyWordsList);
        storedOperators.sort((a, b)->Integer.compare(b.length(), a.length()));

        for(String token : allTokens) {
            boolean operatorFound = false;

            for(String keyword : storedOperators) {
                if(token.contains(keyword)) {
                    int index = token.indexOf(keyword);

                    String beforeKeyword = token.substring(0, index).trim();
                    if(!beforeKeyword.isEmpty()) {
                        temp.add(beforeKeyword);
                    }

                    temp.add(keyword);

                    String afterKeyword = token.substring(index + keyword.length()).trim();
                    if(!afterKeyword.isEmpty()) {
                        temp.add(afterKeyword);
                    }
                    operatorFound = true;
                    break;
                }
            }

            if(!operatorFound) {
                temp.add(token);
            }
        }
        String[] tableAttribute = temp.toArray(new String[0]);
        return tableAttribute;
    }

    public String [] splitEqual (String [] tokens){
        // Separate equals
        List<String> filteredList = new ArrayList<>();
        for(String token : tokens) {
            if(token.contains("=") && !token.equals("==")){
                String [] parts = token.split("=");
                filteredList.add(parts[0]);
                filteredList.add("=");
                filteredList.add(parts[1]);
            } else{
                filteredList.add(token);
            }
        }
        tokens = filteredList.toArray(new String[0]);
        return tokens;
    }

}



