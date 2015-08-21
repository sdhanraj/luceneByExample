package net.luceneByExample.core;

import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.tika.Tika;
import org.apache.tika.detect.Detector;
import org.apache.tika.detect.TextDetector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.txt.TXTParser;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by srinivas_dhanraj on 8/17/15.
 * class that take the directory as input and generates lucene document for the text files in the directory.
 */
public class TextDocumentIterator implements Iterator<LuceneDocument> {

    private Iterator<File> m_fileIterator;
    private LuceneDocument m_tempLuceneDocument;

    public enum FileFields {NAME, SIZE, CONTENT}

    ;

    private Tika m_tika;

    public TextDocumentIterator(String directory) {
        if (directory == null)
            throw new IllegalArgumentException("Argument should cannot be null");
        File file = new File(directory);
        if (!(file.exists() && file.isDirectory()))
            throw new IllegalArgumentException("Argument passed should be a directory");
        this.m_fileIterator = getFiles(file);
        Detector detector = new TextDetector();
        Parser parser = new TXTParser();
        m_tika = new Tika(detector, parser);
    }

    @Override
    public boolean hasNext() {
        return m_fileIterator.hasNext();
    }

    @Override
    public LuceneDocument next() {
        LuceneDocument luceneDocument = null;
        try {
            File file = m_fileIterator.next();
            InputStream input = new FileInputStream(file);

            Metadata metadata = new Metadata();
            String content = m_tika.parseToString(input, metadata);
            luceneDocument = new LuceneDocument();

            FieldType fieldType = new FieldType();
//TODO: Refactor
            fieldType.setStored(true);
            fieldType.setTokenized(true);
            fieldType.setIndexOptions(IndexOptions.DOCS);
            LuceneField field = new LuceneField(FileFields.NAME.name(), file.getName(), fieldType, LuceneField.FieldEnum.OnlyFacet);
            luceneDocument.addField(field);

            fieldType = new FieldType();
            fieldType.setStored(true);
            fieldType.setTokenized(true);
            fieldType.setIndexOptions(IndexOptions.DOCS);
            field = new LuceneField(FileFields.CONTENT.name(), content, fieldType, LuceneField.FieldEnum.OnlyField);
            luceneDocument.addField(field);

            fieldType = new FieldType();
            fieldType.setStored(true);
            fieldType.setTokenized(true);
            fieldType.setNumericType(FieldType.NumericType.LONG);
            field = new LuceneField(FileFields.SIZE.name(), Long.toString(file.length()), fieldType, LuceneField.FieldEnum.Both);
            luceneDocument.addField(field);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return luceneDocument;

    }

    private Iterator<File> getFiles(File file) {

        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File path) {
                return path.getName().toLowerCase()
                        .endsWith(".txt");
            }
        });
        if (files != null && files.length > 0) {
            return Arrays.asList(files).iterator();
        } else {
            return Collections.emptyIterator();
        }
    }

}
