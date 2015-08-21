package net.luceneByExample;

import net.luceneByExample.core.LuceneDocument;
import net.luceneByExample.core.LuceneIndexer;
import net.luceneByExample.core.TextDocumentIterator;
import net.luceneByExample.core.TextDocumentIterator.FileFields;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.*;
import org.apache.lucene.facet.range.LongRange;
import org.apache.lucene.facet.range.LongRangeFacetCounts;
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by srinivas_dhanraj on 8/17/15.
 */
public class TestDocumentCreator {

    public static final String INDEX = ".//index1//";
    public static final String TAXODIR = ".//taxodir1//";

    final LongRange BYTES = new LongRange("Bytes", 0, true, 999, true);
    final LongRange KBYTES = new LongRange("Kilo Bytes", 1000, true, 1000*999, true);
    final LongRange MBYTES = new LongRange("Mega Bytes", 1000*1000, true, 1000*1000*999, true);


    @Test
    public void filesFromSingleDirectory() throws Exception {
        LuceneIndexer luceneIndexer = new LuceneIndexer(INDEX, TAXODIR);
        try {
            TextDocumentIterator docCreator = new TextDocumentIterator("//Users//srinivas_dhanraj//Documents//Lucene//data");
            while (docCreator.hasNext()) {
                LuceneDocument nextDoc = docCreator.next();
                luceneIndexer.addDocument(nextDoc);
            }
        }finally {
            luceneIndexer.close();
        }
        //search(".//index1//", "makes");
        //List<FacetResult> facetResult = facetsWithSearch(".//index1//", ".//taxodir1//", "makes");
        //List<FacetResult> facetResult = facetSearch();
        System.out.println("Facet Search");
        List<FacetResult> facetResult = facetSearch("make");
        for(FacetResult f : facetResult){
            if(f != null)
            System.out.println(f.toString());
        }

        System.out.println("Facet Range Search");
        facetResult = facetRangeSearch("make");
        for(FacetResult f : facetResult){
            if(f != null)
                System.out.println(f.toString());
        }


    }



    private Iterator<File> getFiles(String dataDir)
            throws Exception {

        File[] files = new File(dataDir).listFiles(new FileFilter() {
            @Override
            public boolean accept(File path) {
                return path.getName().toLowerCase()
                        .endsWith(".txt");
            }
        });
        if(files != null && files.length > 0) {
            return Arrays.asList(files).iterator();
        }else{
            return Collections.emptyIterator();
        }
    }

    public static void search(String indexDir, String q)
            throws IOException, ParseException {

        Path path =  new File(indexDir).toPath();
        Directory dir = FSDirectory.open(path); //3
        DirectoryReader reader = DirectoryReader.open(dir);
        IndexSearcher is = new IndexSearcher(reader);   //3
        Analyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser(FileFields.CONTENT.name(), analyzer);  //4
        Query query = parser.parse(q);              //4
        long start = System.currentTimeMillis();
        TopDocs hits = is.search(query, 10); //5
        long end = System.currentTimeMillis();

        System.err.println("Found " + hits.totalHits +   //6
                " document(s) (in " + (end - start) +        // 6
                " milliseconds) that matched query '" +     // 6
                q + "':");                                   // 6

        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = is.doc(scoreDoc.doc);               //7
            System.out.println(doc.get(FileFields.CONTENT.name()));  //8
        }

        reader.close();                                //9
    }


    private List<FacetResult> facetSearch(String q) throws IOException, ParseException {
        Path path = new File(INDEX).toPath();
        Directory dir = FSDirectory.open(path);

        Path pathTaxo = new File(TAXODIR).toPath();
        Directory dirTaxo = FSDirectory.open(pathTaxo);
        DirectoryReader indexReader = DirectoryReader.open(dir);
        TaxonomyReader taxoReader = new DirectoryTaxonomyReader(dirTaxo);
        IndexSearcher searcher = new IndexSearcher(indexReader);

        Analyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser(FileFields.CONTENT.name(), analyzer);
        Query query = parser.parse(q);

        FacetsCollector fc = new FacetsCollector();
        searcher.search(query, fc);

        FacetsConfig config = new FacetsConfig();

        // Retrieve results
        List<FacetResult> results = new ArrayList<FacetResult>();

        Facets facets = new FastTaxonomyFacetCounts(taxoReader, config, fc);

        results.add(facets.getTopChildren(10, FileFields.NAME.name()));
        results.add(facets.getTopChildren(10, FileFields.SIZE.name()));

        indexReader.close();
        taxoReader.close();

        return results;
    }

    private List<FacetResult> facetRangeSearch(String q) throws IOException, ParseException {
        Path path = new File(INDEX).toPath();
        Directory dir = FSDirectory.open(path);

        Path pathTaxo = new File(TAXODIR).toPath();
        Directory dirTaxo = FSDirectory.open(pathTaxo);
        DirectoryReader indexReader = DirectoryReader.open(dir);
        TaxonomyReader taxoReader = new DirectoryTaxonomyReader(dirTaxo);
        IndexSearcher searcher = new IndexSearcher(indexReader);

        Analyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser(FileFields.CONTENT.name(), analyzer);
        Query query = parser.parse(q);

        FacetsCollector fc = new FacetsCollector();
        searcher.search(query, fc);

        FacetsConfig config = new FacetsConfig();

        // Retrieve results
        List<FacetResult> results = new ArrayList<FacetResult>();

        Facets facets = new LongRangeFacetCounts(FileFields.SIZE.name(), fc,BYTES, KBYTES, MBYTES);

        //results.add(facets.getTopChildren(10, FileFields.NAME.name()));
        results.add(facets.getTopChildren(10, FileFields.SIZE.name()));

        indexReader.close();
        taxoReader.close();


        //results.add(facets.getTopChildren(10, FileFields.NAME.name()));
        results.add(facets.getTopChildren(10, FileFields.SIZE.name()));

        indexReader.close();
        taxoReader.close();

        return results;
    }

    @After
    public void cleanup() throws IOException {

        File f1 = new File(".//index1//");
        if(f1.exists())
            FileUtils.forceDelete(f1);
        File f2 = new File(".//taxodir1//");
        if(f2.exists())
            FileUtils.forceDelete(f2);
    }
}
