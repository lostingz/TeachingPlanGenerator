/**
 * Chsi
 * Created on 2016年3月11日
 */
package com.teach;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.config.URLConfig;
import com.html.HTMLParser;
import com.html.WordUtil;

/**
 * 公共常量
 * 
 * @author zhenggm<a href="mailto:zhenggm@chsi.com.cn">zhenggm</a>
 * @version $Id$
 */
class Constants {
    public static final int MAX_BUFFER_SIZE = 20;
    public static final int NUM_OF_PRODUCER = 8;
    public static final int NUM_OF_CONSUMER = 8;
}

class Producer implements Runnable {
    private BlockingQueue<String> buffer;
    private List<String> list;

    public Producer(BlockingQueue<String> buffer, List<String> list) {
        this.buffer = buffer;
        this.list = list;
    }

    public void run() {
        while (true) {
            for (int i = 0; i < list.size(); i++) {
                String pUrl = list.get(i);
                HTMLParser.getTaskLists(pUrl, buffer);
            }
        }
    }
}

class Consumer implements Runnable {
    private BlockingQueue<String> buffer;
    private String planType;

    public Consumer(BlockingQueue<String> buffer, String type) {
        this.buffer = buffer;
        this.planType = type;
    }

    public void run() {
        while (true) {
            try {
                String id = buffer.take();
                if (id != null) {
                    generateWord(id, planType);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void generateWord(String url, String type) {
        Document doc;
        try {
            doc = HTMLParser.getDocument(url);
            Element baseElem = doc.select("div").get(2).select("tr").get(1).select("td").get(0);
            String title = baseElem.select("p").get(1).text().trim();
            Elements content = baseElem.select("table").select("tr").get(1).select("p");
            if (content.size() > 0) {
                String html = content.html().replaceAll("<strong>", "<strong><br/>")
                        .replaceAll("</strong>", "</strong><br/>");
                WordUtil.convertToWord(html, title, type);
            }
        } catch (IOException e) {
            System.out.println("解析教案详情失败");
        }
    }
}

public class Spider {
    public static void main(String[] args) {
        BlockingQueue<String> buffer = new LinkedBlockingQueue<String>(Constants.MAX_BUFFER_SIZE);
        Map<String, List<String>> category = HTMLParser.getCategory();
        System.out.println("================================教案生成系统================================\n");
        System.out.println("生成的文档存储在D:\\教案 目录下面,按照 班->科目层级 生成,生成过程中可以随时关闭,直接关闭这个黑色的框就停止了\n");
        System.out.println("输入班的类型（键盘输入对应得数字，回车即可）\n");
        System.out.println("1.大班           2.中班           3.小班\n");
        System.out.println("请输入:");
        List<String> urlList = null;
        Scanner sc = new Scanner(System.in);
        int type = sc.nextInt();
        String banType = null;
        if (type == 1) {
            urlList = HTMLParser.getUrlList(URLConfig.DABAN, category);
            banType = URLConfig.DABAN;
        } else if (type == 2) {
            urlList = HTMLParser.getUrlList(URLConfig.ZHONGBAN, category);
            banType = URLConfig.ZHONGBAN;
        } else {
            urlList = HTMLParser.getUrlList(URLConfig.XIAOBAN, category);
            banType = URLConfig.XIAOBAN;
        }
        int length = urlList.size();
        List<String> planTypes = HTMLParser.getPlanTypes(urlList);
        System.out.println("下面是该类型班的所有科目教案\n");
        for (int i = 0; i < length; i++) {
            System.out.println((i + 1) + "." + URLConfig.typeMap.get(planTypes.get(i)));
        }
        System.out.println("\n输入科目的类型（键盘输入对应得数字，回车即可）\n");
        System.out.println("请输入:");
        int planType = sc.nextInt();
        List<String> taskUrlList = new ArrayList<String>();
        taskUrlList.add(urlList.get(planType - 1));
        System.out.println("======教案开始生成，生成过程中可以随时关闭======");
        ExecutorService es = Executors.newFixedThreadPool(Constants.NUM_OF_CONSUMER + Constants.NUM_OF_PRODUCER);
        for (int i = 1; i <= Constants.NUM_OF_PRODUCER; ++i) {
            es.execute(new Producer(buffer, taskUrlList));
        }
        for (int i = 1; i <= Constants.NUM_OF_CONSUMER; ++i) {
            es.execute(new Consumer(buffer, banType + "/" + URLConfig.typeMap.get(planTypes.get(planType - 1))));
        }
    }
}
