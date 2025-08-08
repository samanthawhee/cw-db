package edu.uob;

import java.util.Arrays;

public class ElementChecker extends GrammarMaker{
    public boolean elementCheckerCREATE( String [] allTokens) {
        String[] symbolTokens = {"CREATE", "TABLE", "(", ",", ")", ";", " "};
        String [] Tokens = super.mergeTokenList(TokenKeeper.lowerCase, TokenKeeper.upperCase);
        Tokens = super.mergeTokenList(Tokens, TokenKeeper.digit);
        Tokens = mergeTokenList(Tokens, symbolTokens);

        String tokenString = String.join(" ", allTokens);
        if (!super.checkList(tokenString, Tokens)) {
            return false;
        }
        return true;
    }

    public boolean elementCheckerINSERT( String [] allTokens) {
        // Check if each attribute is one of "'" [StringLiteral] "'" | [BooleanLiteral] | [FloatLiteral] | [IntegerLiteral] | "NULL"
        boolean allAttributeValid = true;
        String [] attributes = Arrays.copyOfRange(allTokens, 1, allTokens.length);
        for (String token : attributes) {
            allAttributeValid = super.checkValue(token);
        }
        return allAttributeValid;
    }
}


