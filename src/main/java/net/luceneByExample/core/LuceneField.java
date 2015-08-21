package net.luceneByExample.core;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.util.BytesRef;

import java.io.Reader;

/**
 * Created by srinivas_dhanraj on 8/16/15.
 */
public class LuceneField extends Field{
    public static enum FieldEnum {OnlyField, OnlyFacet, Both};
    private FieldEnum m_fieldEnum;


    public LuceneField(String name, FieldType type, FieldEnum fieldEnum) {
        super(name, type);
        this.m_fieldEnum =  fieldEnum;
    }

    public LuceneField(String name, Reader reader, FieldType type, FieldEnum fieldEnum) {
        super(name, reader, type);
        this.m_fieldEnum =  fieldEnum;
    }

    public LuceneField(String name, TokenStream tokenStream, FieldType type, FieldEnum fieldEnum) {
        super(name, tokenStream, type);
        this.m_fieldEnum =  fieldEnum;
    }

    public LuceneField(String name, byte[] value, FieldType type, FieldEnum fieldEnum) {
        super(name, value, type);
        this.m_fieldEnum =  fieldEnum;
    }

    public LuceneField(String name, byte[] value, int offset, int length, FieldType type, FieldEnum fieldEnum) {
        super(name, value, offset, length, type);
        this.m_fieldEnum =  fieldEnum;
    }

    public LuceneField(String name, BytesRef bytes, FieldType type, FieldEnum fieldEnum) {
        super(name, bytes, type);
        this.m_fieldEnum =  fieldEnum;
    }

    public LuceneField(String name, String value, FieldType type, FieldEnum fieldEnum) {
        super(name, value, type);
        this.m_fieldEnum =  fieldEnum;
    }

    public FieldEnum getFieldEnum(){
        return m_fieldEnum;
    }
}
