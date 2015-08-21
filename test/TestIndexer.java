import net.luceneByExample.core.LuceneDocument;
import net.luceneByExample.core.LuceneField;
import net.luceneByExample.core.LuceneIndexer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.FieldType.NumericType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by srinivas_dhanraj on 8/17/15.
 */
public class TestIndexer{

    @Test
    public void testIndexer() throws IOException, ParseException {

        LuceneIndexer luceneIndexer = new LuceneIndexer(".\\mainIndex", ".\\taxoIndex");
        try {
            LuceneDocument newDoc1 = new LuceneDocument();
            FieldType type = null;
            type = new FieldType();
            type.setStored(true);
            type.setTokenized(false);
            type.setNumericType(NumericType.LONG);

            LuceneField lucField = new LuceneField("ID", "1L", type, LuceneField.FieldEnum.OnlyField);
            newDoc1.addField(lucField);

            type = new FieldType();
            type.setStored(true);
            type.setTokenized(true);
            type.setIndexOptions(IndexOptions.DOCS);
            lucField = new LuceneField("Name", "John Doe", type, LuceneField.FieldEnum.OnlyField);
            newDoc1.addField(lucField);

            type = new FieldType();
            type.setStored(true);
            type.setTokenized(true);
            type.setNumericType(NumericType.INT);
            lucField = new LuceneField("Age", "25", type, LuceneField.FieldEnum.Both);
            newDoc1.addField(lucField);

            luceneIndexer.addDocument(newDoc1);

            LuceneDocument newDoc2 = new LuceneDocument();

            type = new FieldType();
            type.setStored(true);
            type.setTokenized(false);
            type.setNumericType(NumericType.LONG);

            lucField = new LuceneField("ID", "2L", type, LuceneField.FieldEnum.OnlyField);
            newDoc2.addField(lucField);

            type = new FieldType();
            type.setStored(true);
            type.setTokenized(true);
            type.setIndexOptions(IndexOptions.DOCS);
            lucField = new LuceneField("Name", "Jane Doe", type, LuceneField.FieldEnum.OnlyField);
            newDoc2.addField(lucField);

            type = new FieldType();
            type.setStored(true);
            type.setTokenized(true);
            type.setNumericType(NumericType.INT);
            lucField = new LuceneField("Age", "50", type, LuceneField.FieldEnum.Both);
            newDoc2.addField(lucField);

            luceneIndexer.addDocument(newDoc2);
        }finally {
            luceneIndexer.close();
        }

        search(".\\mainIndex", "John");

        search(".\\mainIndex", "Jane");

        search(".\\mainIndex", "Doe");
    }


    public static void search(String indexDir, String q)
            throws IOException, ParseException {

        Path path =  new File(indexDir).toPath();
        Directory dir = FSDirectory.open(path); //3
        DirectoryReader reader = DirectoryReader.open(dir);
        IndexSearcher is = new IndexSearcher(reader);   //3
        Analyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser("Name", analyzer);  //4
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
            System.out.println(doc.get("Name"));  //8
        }

        reader.close();                                //9
    }

    @After
    public void cleanup(){
        File f1 = new File(".\\mainIndex");
        if(f1.exists())
        System.out.println(f1.delete());
        File f2 = new File(".\\taxoIndex");
        if(f2.exists())
        System.out.println(f2.delete());
    }
}
