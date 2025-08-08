package edu.uob;

public class UpdateHelper {

    public String [] getAttributeList(String [] nameValueLit){

        // Get string[] of checkAttributeExist from nameValueLit
        int checkListIndex = 0;
        for(int i = 0; i < nameValueLit.length; i++){
            if(nameValueLit[i].equals("=")){
                checkListIndex++;
            }
        }

        String [] getAttributeList = new String[checkListIndex];
        checkListIndex = 0;
        for(int i = 0; i < nameValueLit.length; i++){
            if(nameValueLit[i].equals("=") && checkListIndex < getAttributeList.length){
                getAttributeList[checkListIndex] = nameValueLit[i - 1];
                checkListIndex++;
            }
        }

        return getAttributeList;
    }

    public String [] getValueList(String [] nameValueLit){

        // Get string[] of checkAttributeExist from nameValueLit
        int checkListIndex = 0;
        for(int i = 0; i < nameValueLit.length; i++){
            if(nameValueLit[i].equals("=")){
                checkListIndex++;
            }
        }

        String [] getAttributeList = new String[checkListIndex];
        checkListIndex = 0;
        for(int i = 0; i < nameValueLit.length; i++){
            if(nameValueLit[i].equals("=") && checkListIndex < getAttributeList.length){
                getAttributeList[checkListIndex] = nameValueLit[i + 1];
                checkListIndex++;
            }
        }

        return getAttributeList;
    }
}
