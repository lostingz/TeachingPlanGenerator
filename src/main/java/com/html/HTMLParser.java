/**
 * Chsi
 * Created on 2016年3月11日
 */
package com.html;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.config.URLConfig;

/**
 * @author zhenggm<a href="mailto:zhenggm@chsi.com.cn">zhenggm</a>
 * @version $Id$
 */
public class HTMLParser {
    public static Map<String, List<String>> getCategory() {
        Map<String, List<String>> category = new HashMap<String, List<String>>();
        try {
            Document doc = getDocument(URLConfig.URL);
            String[] tagIdArr = URLConfig.categoryIdArr;
            for (int i = 0; i < tagIdArr.length; i++) {
                Elements elements = doc.select("#" + tagIdArr[i] + " a");
                String title = elements.first().text();
                List<String> linkList = new ArrayList<String>();
                for (int j = 1; j < elements.size(); j++) {
                    Element link = elements.get(j);
                    String href = link.attr("href");
                    if (!href.contains(".htm")) {
                        linkList.add(URLConfig.URL + href);
                    }
                }
                category.put(title, linkList);
            }
        } catch (IOException e) {
            System.out.println("获取网页失败");
        }
        return category;
    }

    public static Document getDocument(String url) throws IOException {
        Document doc = Jsoup.connect(url).userAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)").timeout(2000)
                .get();
        return doc;
    }

    public static List<String> getUrlList(String key, Map<String, List<String>> category) {
        return category.get(key);
    }

    public static List<String> getPlanTypes(List<String> list) {
        List<String> result = new ArrayList<String>();
        int len = list.size();
        for (int i = 0; i < len; i++) {
            String url = list.get(i);
            result.add(url.substring(url.lastIndexOf("=") + 1, url.length()));
        }
        return result;
    }

    public static void getTaskLists(String pageUrl, BlockingQueue<String> buffer) {
        try {
            Document doc = getDocument(pageUrl);
            String baseUrl = pageUrl.substring(0, pageUrl.lastIndexOf("/") + 1);
            String href = doc.select("form a").get(1).attr("href");
            String pSize = href.substring(href.lastIndexOf("=") + 1, href.length());
            int pageSize = 0;
            if (StringUtils.isNotBlank(pSize)) {
                pageSize = Integer.valueOf(pSize);
            }
            for (int i = 1; i <= pageSize; i++) {
                String currentUrl = pageUrl + "&page=" + i;
                Document currentDoc = getDocument(currentUrl);
                Elements elems = currentDoc.select("div table").get(5).select("tr");
                for (int j = 4; j < elems.size(); j++) {
                    try {
                        buffer.put(baseUrl + elems.get(j).select("td").get(2).select("a").attr("href"));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("获取待爬取列表失败");
        }
    }

    public static void main(String[] args) {
        long t = System.currentTimeMillis();
        // getTaskLists("http://www.chinajiaoan.cn/you3/jiaoan1.asp?owen1=shuxue");
        // getCategory();
        List<String> list = getUrlList(URLConfig.DABAN, getCategory());
        list = getPlanTypes(list);
        /*
         * int i = 0; for (String string : list) { getTaskLists(string); }
         */
        System.out.println(System.currentTimeMillis() - t);
    }
}
