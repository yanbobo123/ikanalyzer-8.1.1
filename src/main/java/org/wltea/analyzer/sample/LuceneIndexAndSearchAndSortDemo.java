package org.wltea.analyzer.sample;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: lucene 排序+高亮显示
 * @ClassName: LuceneIndexAndSearchAndSortDemo
 * @Author: yanbobo
 * @CreateDate: 2019/6/24 13:25
 */
public class LuceneIndexAndSearchAndSortDemo {

    public static void main(String[] args) {

        List<QA> qas = new ArrayList<QA>();
        qas.add(new QA(1, "我是谁", 1));
        qas.add(new QA(2, "你是谁", 2));
        qas.add(new QA(3, "他是谁", 3));
        qas.add(new QA(4, "咱们", 4));
        qas.add(new QA(5, "孩子不学习怎么办", 1));
        qas.add(new QA(6, "怎么办不不怎么办不不不吃饭怎么办", 2));
        qas.add(new QA(7, "怎么办不不怎么办不不不吃饭怎么办", 5));
        qas.add(new QA(8, "食欲差怎么办", 4));
        qas.add(new QA(9, "脖子痛怎么办", 5));
        qas.add(new QA(10, "上课不听讲怎么办", 6));
        qas.add(new QA(11, "上课不听讲怎么办", 8));
        qas.add(new QA(12, "上课不听讲怎么办", 9));
        qas.add(new QA(13, "上课不听讲怎么办", 10));
        qas.add(new QA(14, "上课不听讲怎么办", 11));
        qas.add(new QA(15, "上课不听讲怎么办", 12));
        qas.add(new QA(16, "上课不听讲怎么办", 13));
        qas.add(new QA(17, "上课不听讲怎么办", 14));
        qas.add(new QA(18, "上课不听讲怎么办", 15));
        qas.add(new QA(19, "上课不听讲怎么办", 16));
        qas.add(new QA(20, "上课不听讲怎么办", 17));
        qas.add(new QA(21, "上课不听讲怎么办", 5));

        String fieldName = "text";

        Analyzer analyzer = new IKAnalyzer(false);

        Directory directory = null;
        IndexWriter iwriter = null;
        IndexReader ireader = null;
        IndexSearcher isearcher = null;
        try {
            // 建立内存索引对象
            directory = new RAMDirectory();
            // 索引存放目录
            // 存放到文件系统中
            //directory = FSDirectory.open((new File("f:/test/indextest")).toPath());

            // 配置IndexWriterConfig
            IndexWriterConfig iwConfig = new IndexWriterConfig(analyzer);
            iwConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            iwriter = new IndexWriter(directory, iwConfig);
            // 写入索引
            FieldType type = null;
            Field field = null;
            for (QA qa : qas) {
                Document doc = new Document();
                //doc.add(new StoredField("id", qa.getId()));//存储普通字段
                doc.add(new StringField("id", qa.getId() + "", Field.Store.YES));//存储普通字段
                doc.add(new TextField(fieldName, qa.getContent(), Field.Store.YES));//存储分词并“DOCS_AND_FREQS_AND_POSITIONS”索引字段

                doc.add(new StoredField("sort", qa.getNum()));//存储普通字段
                //支持排序
                doc.add(new NumericDocValuesField("sort", qa.getNum()));
                /*FieldType  numericDocValuesType = new FieldType();
                numericDocValuesType.setTokenized(false);
                numericDocValuesType.setIndexOptions(IndexOptions.NONE);
                numericDocValuesType.setStored(true);
                numericDocValuesType.setDocValuesType(DocValuesType.NUMERIC);
                numericDocValuesType.setDimensions(1, Integer.BYTES);
                numericDocValuesType.freeze();
                doc.add(new Field("sort",qa.getNum()+"",numericDocValuesType));//支持排序*/
                //doc.add(new StringField("sort", qa.getNum() + "", Field.Store.YES));//存储不分词并“DOCS”索引字段
                // 上架时间：数值，排序需要
                /*long upShelfTime = System.currentTimeMillis();
                doc.add(new NumericDocValuesField("upShelfTime", upShelfTime));*/

                /*type = new FieldType();
                type.setStored(true);
                type.setTokenized(false);
                type.setIndexOptions(IndexOptions.NONE);
                type.freeze();
                field = new Field("id", qa.getId()+"", type);
                doc.add(field);

                type = new FieldType();
                type.setStored(true);
                type.setTokenized(true);
                type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS );
                type.freeze();
                field = new Field(fieldName, qa.getContent()+"", type);
                doc.add(field);

                type = new FieldType();
                type.setStored(true);
                type.setTokenized(false);
                type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS  );
                type.freeze();
                field = new Field("sort", qa.getNum()+"", type);
                doc.add(field);*/
                iwriter.addDocument(doc);
            }
            //更新document
            /*Document doc = new Document();
            doc.add(new StringField("id", 1 + "", Field.Store.YES));//存储普通字段
            doc.add(new TextField(fieldName, "你是谁", Field.Store.YES));//存储分词并“DOCS_AND_FREQS_AND_POSITIONS”索引字段
            doc.add(new StoredField("sort", 5));//存储普通字段
            doc.add(new NumericDocValuesField("sort", 5));
            long id = iwriter.updateDocument(new Term("id", "1"), doc);*/
            //删除document
            //long id = iwriter.deleteDocuments(new Term("id", 1 + ""));
            iwriter.forceMergeDeletes();
            // 刷新
            iwriter.flush();
            // 提交
            iwriter.commit();


            // 搜索过程**********************************
            // 实例化搜索器
            ireader = DirectoryReader.open(directory);
            isearcher = new IndexSearcher(ireader);


            String keyword = "是谁";
            // 使用QueryParser查询分析器构造Query对象
            QueryParser qp = new QueryParser(fieldName, analyzer);
            //qp.setDefaultOperator(QueryParser.AND_OPERATOR);//默认是或的关系
            Query query = qp.parse(keyword);
            System.out.println("Query = " + query);
            int start = 0;
            int pageSize = 30;
            //自定义排序
            /*Sort sort = new Sort(new SortField("sort",SortField.Type.INT,true));
            TopFieldCollector results= TopFieldCollector.create(sort,start+pageSize,Integer.MAX_VALUE);*/
            //Sort sort = new Sort(new SortField("score", SortField.Type.SCORE, false), new SortField("sort", SortField.Type.INT, true));
            //打分降序排序+指定字段sort降序排序
            Sort sort = new Sort(SortField.FIELD_SCORE, new SortField("sort", SortField.Type.INT, true));
            TopFieldCollector results = TopFieldCollector.create(sort, start + pageSize, Integer.MAX_VALUE);
            // 搜索相似度最高的5条记录
            //TopDocs topDocs = isearcher.search(query, isearcher.count(query), sort , true);
            isearcher.search(query, results);//执行搜索
            TopDocs topDocs = results.topDocs(start, start + pageSize);//开始的地方和取得的条数  可以用来分页
            System.out.println("命中：" + topDocs.totalHits);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            // 高亮------------------------------
            Formatter htmlFormatter = null;
            if (true)
                htmlFormatter = new SimpleHTMLFormatter(
                        "$$", "$$");
            else
                htmlFormatter = new SimpleHTMLFormatter("", "");

            QueryScorer scorer = new QueryScorer(query);

            Encoder encoder = new SimpleHTMLEncoder();
            Fragmenter fragmenter = new SimpleFragmenter(Integer.MAX_VALUE);
            Highlighter highlighter = new Highlighter(htmlFormatter, scorer);
            highlighter.setTextFragmenter(fragmenter);

            // 输出结果
            for (int i = 0; i < scoreDocs.length; i++) {

                Document targetDoc = isearcher.doc(scoreDocs[i].doc);
                String bestFragment = highlighter.getBestFragment(analyzer, fieldName, targetDoc.get(fieldName));
                targetDoc.removeFields(fieldName);
                targetDoc.add(new TextField(fieldName, bestFragment, Field.Store.YES));
                System.out.println("内容：" + targetDoc.toString() + "," + scoreDocs[i].toString());
                //System.out.println("内容：" + targetDoc.get("id") + ":" + targetDoc.get(fieldName)+ ":" + targetDoc.get("sort")+"," + scoreDocs[i].toString());
            }
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (LockObtainFailedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (iwriter != null) {
                try {
                    iwriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ireader != null) {
                try {
                    ireader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (directory != null) {
                try {
                    directory.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}


class QA {

    private Integer id;

    private String content;

    private Integer num;

    public QA(Integer id, String content, Integer num) {
        this.id = id;
        this.content = content;
        this.num = num;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}