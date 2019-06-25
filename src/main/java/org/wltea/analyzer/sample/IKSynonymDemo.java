package org.wltea.analyzer.sample;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.wltea.analyzer.lucene.IKSynonymAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

/**
 * @describe:
 * @author: yanbobo
 * @createdate: 2019/6/4 15:22
 */
public class IKSynonymDemo {

    public static void main(String[] args) throws IOException, ParseException {
        Analyzer analyzer = new IKSynonymAnalyzer(true, false);
        String str = "刘一反";
        // displayAllTokenInfo(analyzer, str);
        StringReader reader = new StringReader(str);
        TokenStream toStream = analyzer.tokenStream(str, reader);
        toStream.reset();// 清空流
        PositionIncrementAttribute pia = toStream.getAttribute(PositionIncrementAttribute.class);
        OffsetAttribute oa = toStream.getAttribute(OffsetAttribute.class);
        CharTermAttribute cta = toStream.getAttribute(CharTermAttribute.class);
        TypeAttribute ta = toStream.getAttribute(TypeAttribute.class);
        while (toStream.incrementToken()) {
            System.out.print(pia.getPositionIncrement() + ":");
            System.out.print(cta + "[" + oa.startOffset() + "-" + oa.endOffset() + "]-->" + ta.type() + "\n");
        }
    }

    /**
     * 显示分词后token stream全面的信息
     *
     * @param analyzer
     * @throws IOException
     */
    /*public static void displayAllTokenInfo(Analyzer analyzer, String str) throws IOException {
        StringReader reader = new StringReader(str);
        TokenStream toStream = analyzer.tokenStream(str, reader);
        toStream.reset();// 清空流
        PositionIncrementAttribute pia = toStream.getAttribute(PositionIncrementAttribute.class);
        OffsetAttribute oa = toStream.getAttribute(OffsetAttribute.class);
        CharTermAttribute cta = toStream.getAttribute(CharTermAttribute.class);
        TypeAttribute ta = toStream.getAttribute(TypeAttribute.class);
        while (toStream.incrementToken()) {
            System.out.print(pia.getPositionIncrement() + ":");
            System.out.print(cta + "[" + oa.startOffset() + "-" + oa.endOffset() + "]-->" + ta.type() + "\n");
        }
    }*/

}
