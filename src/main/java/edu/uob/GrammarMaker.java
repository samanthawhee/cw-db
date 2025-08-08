package edu.uob;

import java.awt.desktop.SystemEventListener;
import java.util.*;

public class GrammarMaker {
    public boolean checkValue(String plainText) {
        boolean allAttributeValid = false;
        if(plainText.charAt(0) == '\''){
            if(plainText.charAt(plainText.length()-1) != '\''){
                allAttributeValid = false;
            }
            // remove "'" from the string
            String tokenString = plainText.substring(1, plainText.length() - 1);
            if (checkStringLiteral(tokenString)) {
                allAttributeValid = true;
                return allAttributeValid;
            } else {
                allAttributeValid = false;
            }
        }
        if(plainText.charAt(0) != '\'' && plainText.charAt(plainText.length()-1) == '\''){
            allAttributeValid = false;
        }
        if(plainText.charAt(0) != '\'' && plainText.charAt(plainText.length()-1) != '\''
                && checkStringLiteral(plainText) && !checkBooleanLiteral(plainText)){
            allAttributeValid = false;
        }
        if (plainText.equals("NULL") || checkBooleanLiteral(plainText)
                || checkIntegerLiteral(plainText) || checkFloatLiteral(plainText)) {
            allAttributeValid = true;
        }

        return allAttributeValid;
    }

    public boolean checkFloatLiteral(String plainText) {
        char firstChar = plainText.charAt(0);
        // [0] is not digit, - or +
        if(!Character.isDigit(firstChar) && firstChar != '-' && firstChar != '+') {
            return false;
        }
        // check the other char is digit or '.'
        boolean dotFound = false;
        for(int i = 1; i < plainText.length(); i++) {
            char currentChar = plainText.charAt(i);
            if(currentChar == '.'){
                if(dotFound){
                    // Multiple dots found
                    return false;
                }
                dotFound = true;
            } else if(!Character.isDigit(currentChar)) {
                // Invalid char found
                return false;
            }
        }
        if(dotFound) {
            StringBuilder sb = new StringBuilder(plainText);
            sb.setCharAt(0, '9');
            plainText = sb.toString();
            String [] digitParts = sb.toString().split("\\.");

            for(String digit : digitParts) {
                if(digit.isEmpty() || !digit.chars().allMatch(Character::isDigit)) {
                    // Invalid digit found
                    return false;
                }
            }
            return true;
        }
        // '.' does not exist
        return false;
    }

    public boolean checkIntegerLiteral(String plainText) {
        char firsyChar = plainText.charAt(0);
        if(!(Character.isDigit(firsyChar) || firsyChar == '-' || firsyChar == '+')) {
            // [0] is not digit, - or +
            return false;
        }
        // check the other char is digit
        for(int i = 1; i < plainText.length(); i++) {
            if(!Character.isDigit(plainText.charAt(i))) {
                // The other char are not digit
                return false;
            }
            if(plainText.charAt(i) == '.') {
                return false;
            }
        }
        return true;
    }

    public boolean checkWildAttribList(String [] wildAttribList) {
        if(wildAttribList.length == 1 && (wildAttribList[0].equals("*") || checkPlainText(wildAttribList[0]))){
            return true;
        } else if(wildAttribList.length > 1){
            // Remove ","
            List<String> filteredList = new ArrayList<>();
            for(String wildAttrib : wildAttribList) {
                if(!wildAttrib.equals(",")){
                    filteredList.add(wildAttrib);
                }
            }
            String [] filteredArray = filteredList.toArray(new String[0]);

            if(filteredList.isEmpty()) {
                return false;
            }
            for(String token : filteredList) {
                if(checkPlainText(token)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public boolean checkNameValueList(String [] tokens) {

        if(tokens.length < 3){
            return false;
        }
        if(tokens.length== 3){
            // Check tokens[0] is plainText, tokens[1] is "=", tokens[2] is value
            if(!(checkPlainText(tokens[0]) && tokens[1].equals("=") && checkValue(tokens[2]))){
                return false;
            }
        }
        if(tokens.length > 3){
            int equal = 0;
            int comma = 0;
            // if it's a comma
            for(int i = 0; i < tokens.length; i++) {
                if(tokens[i].equals("=")){
                    equal++;
                }
                if(tokens[i].equals(",")){
                    // tokens[index-1] is value, tokens[index+1] is plainText
                    if(!(checkValue(tokens[i-1]) && checkPlainText(tokens[i+1]))){
                        return false;
                    }
                    comma++;
                }
            }
            // Check if comma = equal - 1
            if(comma != equal-1){
                return false;
            }
        }
        return true;
    }

    public boolean checkCondition(String [] tokens) {
        // Check tokens more than 3
        if(tokens.length < 3){
            return false;
        }
        int frontBracket = 0;
        int backBracket = 0;
        for(String token : tokens) {
            if(token.equals("(")){
                frontBracket++;
            }
            if(token.equals(")")){
                backBracket++;
            }
        }
        // Check front bracket = back bracket
        if(frontBracket != backBracket) {
            return false;
        }
        // if no bracket
        if(frontBracket == 0){
            if(!(checkPlainText(tokens[0]) && checkComparator(tokens[1])
                    && checkValue(tokens[2]))) {
                return false;
            }
        }

        // if meet a front bracket
        for(int i = 0; i < tokens.length; i++) {
            if(tokens[i].equals("(")) {
                if(i + 4 < tokens.length && !tokens[i+1].equals("(")) {
                    if(!(checkPlainText(tokens[i+1]) && checkComparator(tokens[i+2])
                            && checkValue(tokens[i+3]) && tokens[tokens.length-1].equals(")"))) {
                        return false;
                    }
                }
            }

            if(checkBooleanOperator(tokens[i])){
                if(i > 0 && i < (tokens.length - 1)) {
                    // Check if BoolOperator [index-1] is ")"and BoolOperator [index+1] is "("
                    if(!(tokens[i-1].equals(")") && tokens[i+1].equals("("))){
                        return false;
                    }
                }
            }

        }
        return true;
    }


    public boolean checkComparator(String plainText) {
        plainText = plainText.toUpperCase();
        String [] allTokens = TokenKeeper.comparator;
        for(String token : allTokens) {
            if(!token.equals(plainText)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkDigitSequence(String plainText) {
        String [] allTokens = TokenKeeper.digit;
        return checkList(plainText, allTokens);
    }

    public boolean checkBooleanOperator(String plainText) {
        plainText = plainText.toUpperCase();
        String [] allTokens = TokenKeeper.booleanOperator;
        for(String token : allTokens) {
            if(token.equals(plainText)) {
                return true;
            }
        }
        return false;
    }
    public boolean checkBooleanLiteral(String plainText) {
        plainText = plainText.toUpperCase();
        String [] allTokens = TokenKeeper.booleanLiteral;
        for(String token : allTokens) {
            if(token.equals(plainText)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkStringLiteral(String plainText) {
        String [] allTokens = mergeTokenList(TokenKeeper.lowerCase, TokenKeeper.upperCase);
        allTokens = mergeTokenList(allTokens, TokenKeeper.digit);
        allTokens = mergeTokenList(allTokens, TokenKeeper.symbol);
        allTokens = mergeTokenList(allTokens, TokenKeeper.space);
        return checkList(plainText, allTokens);
    }
    public boolean checkPlainText(String plainText) {
        String [] allTokens = mergeTokenList(TokenKeeper.lowerCase, TokenKeeper.upperCase);
        allTokens = mergeTokenList(allTokens, TokenKeeper.digit);
        return checkList(plainText, allTokens);
    }


    public String [] mergeTokenList(String [] tokenList01, String [] tokenList02) {
        String[] result = Arrays.copyOf(tokenList01, tokenList01.length + tokenList02.length);
        System.arraycopy(tokenList02, 0, result, tokenList01.length, tokenList02.length);
        return result;
    }
    public boolean checkList(String text, String [] tokenList) {
        Set<String> tokenSet = new HashSet<>(Arrays.asList(tokenList));
        for(char c : text.toCharArray()){
            if(!tokenSet.contains(String.valueOf(c))){
                return false;
            }
        }
        return true;
    }
}
