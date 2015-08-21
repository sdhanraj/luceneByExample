package net.luceneByExample.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by srinivas_dhanraj on 8/16/15.
 */
public class LuceneDocument {

    private Map<String, LuceneField> fieldMap = new HashMap<>();

    public boolean addField(LuceneField field){
        if(fieldMap.containsKey(field.name())){
            return false;
        }else{
            fieldMap.put(field.name(), field);
            return true;
        }

    }

    public boolean removeField(LuceneField field){
        if(!fieldMap.containsKey(field.name())){
            return false;
        }else{
            fieldMap.remove(field.name());
            return true;
        }
    }

    public boolean removeField(String fieldName){
        if(!fieldMap.containsKey(fieldName)){
            return false;
        }else{
            fieldMap.remove(fieldName);
            return true;
        }
    }

    public Iterator<LuceneField> getFields(){
        return fieldMap.values().iterator();
    }
}
