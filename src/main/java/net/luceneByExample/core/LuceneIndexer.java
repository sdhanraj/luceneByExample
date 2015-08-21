package net.luceneByExample.core;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

import static org.apache.lucene.document.FieldType.NumericType.LONG;

/**
 * Created by srinivas_dhanraj on 8/16/15.
 */
public class LuceneIndexer implements Closeable{

    private final  IndexWriter m_writer;
    private final DirectoryTaxonomyWriter m_taxoWriter;
    private final FacetsConfig config = new FacetsConfig();

    public LuceneIndexer(String indexDir, String taxoDirPath) throws IOException {
        Path path = new File(indexDir).toPath();
        Directory dir = FSDirectory.open(path);
        Path taxoPath = new File(taxoDirPath).toPath();
        Directory taxoDir = FSDirectory.open(taxoPath);
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig conf = new IndexWriterConfig(analyzer);
        m_writer = new IndexWriter(dir, conf); //3
        m_taxoWriter = new DirectoryTaxonomyWriter(taxoDir);
    }

    public void addDocument(LuceneDocument document) throws IOException {
        Document doc = new Document();
        Iterator<LuceneField> fieldIterator = document.getFields();
        while(fieldIterator.hasNext()){
            LuceneField nextField = fieldIterator.next();
            LuceneField.FieldEnum fieldEnum = nextField.getFieldEnum();

            switch(fieldEnum){
                case Both:{
                    doc.add(nextField);
                    if(nextField.fieldType().numericType() == LONG){
                        doc.add(new NumericDocValuesField(nextField.name(), Long.valueOf(nextField.stringValue()))); //RangeSearch
                        doc.add(new FacetField(nextField.name(), nextField.stringValue())); //For Facets
                    }else {
                        doc.add(new FacetField(nextField.name(), nextField.stringValue()));
                    }
                    break;
                }
                case OnlyFacet:{
                    if(nextField.fieldType().numericType() == LONG){
                        doc.add(new NumericDocValuesField(nextField.name(), Long.valueOf(nextField.stringValue())));
                    }else {
                        doc.add(new FacetField(nextField.name(), nextField.stringValue()));
                    }
                    break;
                }
                case OnlyField:{
                    doc.add(nextField);
                    break;
                }
            }
        }

        m_writer.addDocument(config.build(m_taxoWriter,doc));
    }

    @Override
    public void close() throws IOException {
        m_writer.close();
        m_taxoWriter.close();
    }

}
