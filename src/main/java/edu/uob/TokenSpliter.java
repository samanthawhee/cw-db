package edu.uob;

public class TokenSpliter {
    private final CharacterUpper characterUpper;
    private final ElementsSpliter elementsSpliter;

    public TokenSpliter() {
        this.characterUpper = new CharacterUpper();
        this.elementsSpliter = new ElementsSpliter();
    }
    public String [] splitToken4CREATE(String [] tokens) {
        tokens = characterUpper.upCREATE(tokens);
        tokens = elementsSpliter.splitElements(tokens, TokenKeeper.CREATE);
        return tokens;
    }
    public String [] splitToken4INSERT(String [] tokens) {
        tokens = characterUpper.upINSERT(tokens);
        tokens = elementsSpliter.splitElements(tokens, TokenKeeper.INSERT);
        return tokens;
    }
    public String [] splitToken4ALTER(String [] tokens) {
        tokens = characterUpper.upALTER(tokens);
        tokens = elementsSpliter.splitElements(tokens, TokenKeeper.ALTER);
        return tokens;
    }
    public String [] splitToken4DROP(String [] tokens) {
        tokens = characterUpper.upCREATE(tokens);
        tokens = elementsSpliter.splitElements(tokens, TokenKeeper.CREATE);
        return tokens;
    }
    public String [] splitToken4JOIN(String [] tokens) {
        tokens = characterUpper.upJOIN(tokens);
        tokens = elementsSpliter.splitElements(tokens, TokenKeeper.JOIN);
        return tokens;
    }
    public String [] splitTokenSELECT(String [] tokens) {
        tokens = elementsSpliter.splitElements(tokens, TokenKeeper.SELECT);
        tokens = elementsSpliter.splitOperators(tokens, TokenKeeper.booleanOperator);
        tokens = elementsSpliter.splitOperators(tokens, TokenKeeper.comparator);
        tokens = characterUpper.upSELECT(tokens);
        return tokens;
    }
    public String [] splitToken4UPDATE(String [] tokens) {
        tokens = elementsSpliter.splitElements(tokens, TokenKeeper.UPDATE);
        tokens = elementsSpliter.splitOperators(tokens, TokenKeeper.booleanOperator);
        tokens = elementsSpliter.splitOperators(tokens, TokenKeeper.comparator);
        tokens = characterUpper.upUPDATE(tokens);
        tokens = elementsSpliter.splitEqual(tokens);
        tokens = elementsSpliter.splitOperators(tokens, TokenKeeper.comma);
        return tokens;
    }
    public String [] splitToken4DELETE(String [] tokens) {
        tokens = elementsSpliter.splitElements(tokens, TokenKeeper.DELETE);
        tokens = elementsSpliter.splitOperators(tokens, TokenKeeper.booleanOperator);
        tokens = elementsSpliter.splitOperators(tokens, TokenKeeper.comparator);
        tokens = characterUpper.upDELETE(tokens);
        return tokens;
    }
}
