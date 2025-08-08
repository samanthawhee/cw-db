package edu.uob;

public class TokenValidityChecker {

    private final MessageHandler messageHandler;
    private final GrammarMaker grammarMaker;

    public TokenValidityChecker() {
        this.messageHandler = new MessageHandler();
        this.grammarMaker = new GrammarMaker();
    }

    public boolean checkTokenValidity4USE(String [] tokens) {
        if(tokens.length != 3){
            return false;
        }
        return true;
    }
//    public boolean checkTokenValidity4CREATE(String [] tokens) {}
    public boolean checkTokenValidity4INSERT(String [] tokens) {
        // Check if INSERT INTO VALUES ( ) ; stored in the certain cell
        if(!tokens[0].equals("INSERT")
                || !tokens[1].equals("INTO")
                || !tokens[3].equals("VALUES")
                || !tokens[4].equals("(")
                || !tokens[tokens.length-2].equals(")")
                || !tokens[tokens.length-1].equals(";")){
            return false;
        }
        return true;
    }
    public String checkTokenValidity4ALTER(String [] tokens) {
        String OK = "[OK]";
        // Check the tokens are 6
        if(tokens.length != 6){
            return messageHandler.errorMessage(2);
        }
        // Check the tokens on the certain element: ALTER, TABLE
        if(!(tokens[0].equals("ALTER") || tokens[1].equals("TABLE"))){
            return messageHandler.errorMessage(2);
        }
        // CHeck [2] is "ADD" or "DROP"
        if(!(tokens[3].equals("ADD") || tokens[3].equals("DROP"))){
            return messageHandler.errorMessage(2);
        }
        // CHeck AttributeNAme is PlainText
        if(!grammarMaker.checkPlainText(tokens[tokens.length - 2])){
            return messageHandler.errorMessage(2);
        }
        return OK;
    }
//    public boolean checkTokenValidity4DROP(String [] tokens) {}
    public String checkTokenValidity4JOIN(String [] tokens) {
        String OK = "[OK]";

        // Check tokens has 9 elements
        if(tokens.length != 9){
            return messageHandler.errorMessage(2);
        }
        // Check the certain elements are: JOIN, AND ON AND ;
        if(!(tokens[0].equals("JOIN") && tokens[2].equals("AND")
                && tokens[4].equals("ON") && tokens[6].equals("AND"))){
            return messageHandler.errorMessage(2);
        }
        // Check [1], [3], [5], [7] is plainText
        if(!(grammarMaker.checkPlainText(tokens[1]) && grammarMaker.checkPlainText(tokens[3])
                && grammarMaker.checkPlainText(tokens[5]) && grammarMaker.checkPlainText(tokens[7]))){
            return messageHandler.errorMessage(2);
        }
        return OK;
    }
//    public boolean checkTokenValidity4SELECT(String [] tokens) {}
    public boolean checkTokenValidity4UPDATE(String [] tokens) {
        if(!(tokens[0].equals("UPDATE") && grammarMaker.checkPlainText(tokens[1])
                && tokens[2].equals("SET"))){
            return false;
        }
        return true;
    }
    public boolean checkTokenValidity4DELETE(String [] tokens) {
        if(!(tokens[0].equals("DELETE") && tokens[1].equals("FROM")
                && grammarMaker.checkPlainText(tokens[2]) && tokens[3].equals("WHERE"))){
            return false;
        }
        return true;
    }





}
