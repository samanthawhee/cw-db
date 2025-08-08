package edu.uob;

public class CharacterUpper {
    public String [] upCREATE(String [] allTokens) {
        allTokens[0] = allTokens[0].toUpperCase();
        allTokens[1] = allTokens[1].toUpperCase();
        return allTokens;
    }

    public String [] upINSERT(String [] allTokens) {
        allTokens[0] = allTokens[0].toUpperCase();
        allTokens[1] = allTokens[1].toUpperCase();
        allTokens[3] = allTokens[3].toUpperCase();
        return allTokens;
    }

    public String [] upSELECT(String [] allTokens) {
        for(int i = 0; i < allTokens.length; i++){
            if(allTokens[i].equalsIgnoreCase("SELECT") ||allTokens[i].equalsIgnoreCase("FROM")
                    || allTokens[i].equalsIgnoreCase("WHERE")||
                    allTokens[i].equalsIgnoreCase("AND") || allTokens[i].equalsIgnoreCase("OR")) {
                allTokens[i] = allTokens[i].toUpperCase();
            }
        }

        return allTokens;
    }
    public String [] upALTER(String [] allTokens) {
        for(int i = 0; i < allTokens.length; i++){
            if(allTokens[i].equalsIgnoreCase("ALTER") ||allTokens[i].equalsIgnoreCase("TABLE")
                    || allTokens[i].equalsIgnoreCase("DROP")|| allTokens[i].equalsIgnoreCase("ADD")) {
                allTokens[i] = allTokens[i].toUpperCase();
            }
        }

        return allTokens;
    }

    public String [] upJOIN(String [] allTokens) {
        for(int i = 0; i < allTokens.length; i++){
            if(allTokens[i].equalsIgnoreCase("JOIN") ||allTokens[i].equalsIgnoreCase("AND")
                    || allTokens[i].equalsIgnoreCase("ON")) {
                allTokens[i] = allTokens[i].toUpperCase();
            }
        }

        return allTokens;
    }

    public String [] upUPDATE(String [] allTokens) {
        for(int i = 0; i < allTokens.length; i++){
            if(allTokens[i].equalsIgnoreCase("UPDATE") ||allTokens[i].equalsIgnoreCase("SET")
                    || allTokens[i].equalsIgnoreCase("WHERE")) {
                allTokens[i] = allTokens[i].toUpperCase();
            }
        }

        return allTokens;
    }

    public String [] upDELETE(String [] allTokens) {
        for(int i = 0; i < allTokens.length; i++){
            if(allTokens[i].equalsIgnoreCase("DELETE") ||allTokens[i].equalsIgnoreCase("FROM")
                    || allTokens[i].equalsIgnoreCase("WHERE")) {
                allTokens[i] = allTokens[i].toUpperCase();
            }
        }

        return allTokens;
    }
}
