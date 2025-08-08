package edu.uob;

import java.util.*;

public class TokenKeeper {
    public static final String [] queryTokens = {
            "USE", "CREATE", "DROP", "ALTER",
            "INSERT", "SELECT", "UPDATE", "DELETE", "JOIN"};
    public static final String [] symbol = {
            "!", "#", "$", "%", "&", "(",  ")", "*",
            "+", ",", "-", ".", "/", ":", ";", ">",
            "=", "<", "?", "@", "[", "\\", "]", "^", "_", "`", "{", "}", "~"};
    public static final String [] space = {" "};
    public static final String [] digit = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
    public static final String [] upperCase = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    };
    public static final String [] lowerCase = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z"
    };
    public static final String [] comma = {","};
    public static final String [] booleanLiteral = {"TRUE", "FALSE"};
    public static final String [] booleanOperator = {"AND", "OR"};
    public static final String [] comparator = {"==", ">=", "<=", "!=", ">", "<", "LIKE"};

    public static final String [] CREATE = {"CREATE", "TABLE", "(", ",",")", ";", " "};
    public static final String [] INSERT = {"INSERT", "INTO", "VALUES", "(", ",",")", ";", " "};
    public static final String [] SELECT = {"SELECT", "FROM", "WHERE", "(", ",",")", ";", " "};
    public static final String [] ALTER = {"ALTER", "TABLE", "DROP", "ADD", ";", " "};
    public static final String [] JOIN = {"JOIN", "AND", "ON", ";", " "};
    public static final String [] UPDATE = {"UPDATE", "SET", "WHERE", ";", " "};
    public static final String [] DELETE = {"DELETE", "FROM", "WHERE", ";", " "};

}
