/**
 * Chsi
 * Created on 2016年3月11日
 */
package com.config;

import java.util.HashMap;
import java.util.Map;


/**
 * @author zhenggm<a href="mailto:zhenggm@chsi.com.cn">zhenggm</a>
 * @version $Id$
 */
public class URLConfig {
    public final static String URL = "http://www.chinajiaoan.cn/";
    public final static String[] categoryIdArr = {"AutoNumber5", "AutoNumber6", "AutoNumber7"};
    public final static String DABAN = "幼儿园大班教案";
    public final static String ZHONGBAN = "幼儿园中班教案";
    public final static String XIAOBAN = "幼儿园小班教案";
    public final static Map<String, String> typeMap = new HashMap<String, String>() {
        {
            put("shuxue", "数学");
            put("kexue", "科学");
            put("zhuti", "主题");
            put("yuyan", "语言");
            put("yinyue", "音乐");
            put("tiyu", "体育");
            put("yingyu", "英语");
            put("meishu", "美术");
            put("zonghe", "综合");
            put("jiankang", "健康");
            put("youxi", "游戏");
        }
    };
}
