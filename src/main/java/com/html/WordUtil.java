/**
 * Chsi
 * Created on 2016年3月14日
 */
package com.html;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.rtf.RtfWriter2;

/**
 * @author zhenggm<a href="mailto:zhenggm@chsi.com.cn">zhenggm</a>
 * @version $Id$
 */
public class WordUtil {
    public static void convertToWord(String html, String title, String type) {
        OutputStream out = null;
        try {
            String subFolder = "";
            if (StringUtils.isNotBlank(type)) {
                subFolder = type + "/";
            }
            File dir = new File("E:/教案/" + subFolder);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File f = new File("E:/教案/" + subFolder + "/" + title + ".doc");
            if (!f.exists()) {
                f.createNewFile();
            }
            out = new FileOutputStream(f);
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Document document = new Document();
        document.setPageSize(PageSize.A4);
        RtfWriter2.getInstance(document, out);
        document.open();
        StyleSheet style = new StyleSheet();
        style.loadStyle("<br/>", null);
        style.loadStyle("<strong>", null);
        try {
            List htmlList = HTMLWorker.parseToList(new StringReader(html), style);
            for (int i = 0; i < htmlList.size(); i++) {
                Element e = (Element) htmlList.get(i);
                document.add(e);
            }
            document.close();
            System.out.println("生成" + title + ".doc完成");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
