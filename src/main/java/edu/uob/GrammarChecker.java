package edu.uob;


import java.util.Arrays;

public class GrammarChecker {
    private final String storageFolderPath;
    private final MessageHandler messageHandler = new MessageHandler();
    private final TokenKeeper tokenKeeper = new TokenKeeper();
    private final GrammarMaker grammarMaker;
    private final ElementChecker elementChecker;
    private final ElementsSpliter elementsSpliter;
    private final CharacterUpper characterUpper;
    private final SelectHelper selectHelper;
    private final TokenSpliter tokenSpliter;
    private final TokenValidityChecker tokenValidityChecker;
    public GrammarChecker(String storageFolderPath) {
        this.storageFolderPath = storageFolderPath;
        this.grammarMaker = new GrammarMaker();
        this.elementChecker = new ElementChecker();
        this.elementsSpliter = new ElementsSpliter();
        this.characterUpper = new CharacterUpper();
        this.tokenSpliter = new TokenSpliter();
        this.tokenValidityChecker = new TokenValidityChecker();
        this.selectHelper = new SelectHelper(storageFolderPath);
    }

    public String checkUSE(String queryToken, String [] tokens) {
        if(!tokenValidityChecker.checkTokenValidity4USE(tokens)){
            return messageHandler.errorMessage(4);
        }
        return queryToken;
    }
    public String checkCREATE(String queryToken, String [] tokens) {
        tokens = tokenSpliter.splitToken4CREATE(tokens);
        String queryType = tokens[1];
        switch(queryType){
            case "DATABASE":
                if(tokens.length != 4){
                    return messageHandler.errorMessage(8);
                }
                if(!grammarMaker.checkPlainText(tokens[2])) {
                    return messageHandler.errorMessage(9);
                }
                return queryToken;
            case "TABLE":
                if(tokens.length == 4){
                    if(!grammarMaker.checkPlainText(tokens[2])) {
                        return messageHandler.errorMessage(9);
                    }
                    return queryToken;
                } else {
                    if(!this.elementChecker.elementCheckerCREATE(tokens)){
                        return messageHandler.errorMessage(13);
                    }
                    if(!tokens[3].equals("(") && !tokens[tokens.length-2].equals(")")){
                        return messageHandler.errorMessage(12);
                    }
                    return queryToken;
                }
            default:
                return messageHandler.errorMessage(11);
        }
    }
    public String checkDROP(String queryToken, String [] tokens) {
        tokens = tokenSpliter.splitToken4DROP(tokens);
        String queryType = tokens[1];
        switch(queryType){
            case "DATABASE":
                if(tokens.length != 4){
                    return messageHandler.errorMessage(8);
                }
                if(!grammarMaker.checkPlainText(tokens[2])) {
                    return messageHandler.errorMessage(9);
                }
                return queryToken;
            case "TABLE":
                if(tokens.length != 4){
                    return messageHandler.errorMessage(8);
                }
                if(!grammarMaker.checkPlainText(tokens[2])) {
                    return messageHandler.errorMessage(9);
                }
                return queryToken;
            default:
                return messageHandler.errorMessage(11);
        }
    }
    public String checkINSERT(String queryToken, String [] tokens) {
        tokens = tokenSpliter.splitToken4INSERT(tokens);

        if(!tokenValidityChecker.checkTokenValidity4INSERT(tokens)){
            return messageHandler.errorMessage(2);
        }
        // Extract INSERT INTO VALUES ( ) ; stored in the certain cell from attributes
        String [] attributeList = elementsSpliter.splitAttribute(tokens, TokenKeeper.INSERT);
        attributeList = elementsSpliter.removeKeyWords(tokens, TokenKeeper.INSERT);
        // Check if each attribute is one of "'" [StringLiteral] "'" | [BooleanLiteral] | [FloatLiteral] | [IntegerLiteral] | "NULL"
        if(!this.elementChecker.elementCheckerINSERT(attributeList)){
            return messageHandler.errorMessage(15);
        }
        return queryToken;
    }
    public String checkSELECT(String queryToken, String [] tokens) {
        tokens = tokenSpliter.splitTokenSELECT(tokens);
        // Check FROM exists
        int fromCount = 0;
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if(token.equals("FROM")){
                fromCount++;
            }

            // Return false if more than 1 following by a PlainText and the PlainText is not "LIKE"
                // select from, b, from table where (from>3) AND (from like from)
                // From cam following by ',' PainText Comparator ')'
            if(fromCount > 1){
                String nextToken = tokens[i+1];
                if(!(nextToken.equals(",") || nextToken.equals(")") || nextToken.equals("\'")
                    || grammarMaker.checkPlainText(nextToken)) || grammarMaker.checkComparator(nextToken)){
                    return messageHandler.errorMessage(2);
                }
            }
        }

        if(fromCount == 0){
            // FROM not found
            return messageHandler.errorMessage(2);
        }

        // Put the tokens between SELECT and FROM into String [] WildAttribList
        String [] wildAttribList = selectHelper.getListFrom2Str("SELECT", "FROM", tokens);

        // Check is WildAttribList valid
        // Check if it contains WHERE
        boolean withCondition = false;
        if(grammarMaker.checkWildAttribList(wildAttribList)){
            for(String token : tokens){
                if(token.equals("WHERE")){
                    withCondition = true;
                    break;
                }else{
                    withCondition = false;
                }
            }
        } else {
            return messageHandler.errorMessage(2);
        }


        if(withCondition){
            // YES (With Condition)
            // Check if the token between FROM amd WHERE is a PlainTEXT
            String [] tableName = selectHelper.getListFrom2Str("FROM", "WHERE", tokens);
            if(!(tableName.length == 1 && grammarMaker.checkPlainText(tableName[0]))){
                return messageHandler.errorMessage(2);
            } else {
                // Put the tokens after WHERE into String [] condition
                String [] condition = selectHelper.getListFrom2Str("WHERE",";", tokens);
                // Check if condition valid
                if(grammarMaker.checkCondition(condition)){
                    return queryToken;
                }else{
                    return messageHandler.errorMessage(2);
                }
            }

        }else{
            // NO (Without Condition)
            // Check if the token after FROM is PLAINTEXT. and the token after FROM is the last token in the string list
            for(int i = 0; i < tokens.length - 1; i++){
                String token = tokens[i];
                if(token.equals("FROM")){
                    if(grammarMaker.checkPlainText(tokens[i+1]) && i+1 == tokens.length -2){return queryToken;
                    }
                    return messageHandler.errorMessage(2);
                }
            }
            return messageHandler.errorMessage(2);
        }
    }

    public String checkALTER(String queryToken, String [] tokens) {
        tokens = tokenSpliter.splitToken4ALTER(tokens);
        if(!tokenValidityChecker.checkTokenValidity4ALTER(tokens).equals("[OK]")){
            return messageHandler.errorMessage(2);
        }
        return queryToken;
    }

    public String checkUPDATE(String queryToken, String [] tokens) {
        tokens = tokenSpliter.splitToken4UPDATE(tokens);
        if(!tokenValidityChecker.checkTokenValidity4UPDATE(tokens)){
            return messageHandler.errorMessage(2);
        }
        // Check tokens has "WHERE"
        boolean withWHERE = false;
        for(int i = 0; i < tokens.length - 1; i++){
            if(tokens[i].equals("WHERE")){
                if(!grammarMaker.checkPlainText(tokens[i+1])){
                    return messageHandler.errorMessage(2);
                }else if (!grammarMaker.checkValue(tokens[i-1])){

                    return messageHandler.errorMessage(2);
                }
                withWHERE = true;
            }
        }
        if(!withWHERE){
            return messageHandler.errorMessage(2);
        }
        // Get string [] NameValueList
        String [] nameValueList = selectHelper.getListFrom2Str("SET", "WHERE", tokens);
        nameValueList = elementsSpliter.splitElements(tokens, TokenKeeper.comma);
        // Check if NameValueList is valid
        if(!grammarMaker.checkNameValueList(nameValueList)){
            return messageHandler.errorMessage(2);
        }else{
            // Get string [] Condition
            String [] condition = selectHelper.getListFrom2Str("WHERE",";", tokens);
            // Check if Condition is valid
            if(!grammarMaker.checkCondition(condition)){
                return messageHandler.errorMessage(2);
            }
        }
        return queryToken;
    }
    public String checkDELETE(String queryToken, String [] tokens) {
        tokens = tokenSpliter.splitToken4DELETE(tokens);
        if(!tokenValidityChecker.checkTokenValidity4DELETE(tokens)){
            return messageHandler.errorMessage(2);
        }
        String [] conditions = selectHelper.getListFrom2Str("WHERE",";", tokens);
        if(!grammarMaker.checkCondition(conditions)){
            return messageHandler.errorMessage(2);
        }
        return queryToken;
    }
    public String checkJOIN(String queryToken, String [] tokens) {
        tokens = tokenSpliter.splitToken4JOIN(tokens);

        if(!(tokenValidityChecker.checkTokenValidity4JOIN(tokens).equals("[OK]"))){
            return messageHandler.errorMessage(2);
        }
        return queryToken;
    }

    public boolean checkSemicolon(String [] allTokens) {
        if(allTokens.length == 0) {
            return false;
        }
        String lastToken = allTokens[allTokens.length-1];
        return lastToken.trim().endsWith( ";");
    }

}
