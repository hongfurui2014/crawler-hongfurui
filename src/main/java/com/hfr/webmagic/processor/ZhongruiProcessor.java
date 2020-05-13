package com.hfr.webmagic.processor;

import com.hfr.bean.Detail;
import com.hfr.dao.DetailDao;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.HashMap;
import java.util.Map;

public class ZhongruiProcessor implements PageProcessor {

    private Site site = Site.me().setSleepTime(3000);

    private Map<String, Detail> map = new HashMap();

    private DetailDao dao = new DetailDao();

    public Site getSite() {
        return site.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36").setCharset("utf-8");
    }

    public void process(Page page) {
        String currentUrl = page.getUrl().toString();

        Document doc = Jsoup.parse(page.getHtml().toString());
        try {
            if (currentUrl.contains("lists")) {
                //列表页
                Elements elements = doc.select("div.zhaob");

                for (Element e : elements) {
                    String list_title = e.select("a").text();
                    String list_link = "http://www.chiwayland.com" + e.select("a").attr("href");
                    String time = e.select("div.date").get(0).text();
                    String list_time = time.substring(time.lastIndexOf("：") + 1);

                    String id = list_link.substring(list_link.lastIndexOf("=") + 1);

                    Detail d = new Detail();
                    d.setLIST_TITLE(list_title);
                    d.setPAGE_TIME(list_time);
                    d.setID(id);

                    map.put(id, d);

                    page.addTargetRequest(list_link);
                }

                //翻页
                String nextUrl = doc.select("a.next").get(0).attr("href");
                if (nextUrl != null) {
                    nextUrl = "http://www.chiwayland.com/" + doc.select("a.next").get(0).attr("href");
                    page.addTargetRequest(nextUrl);
                }

            } else {
                //详情页
                String detail_title = doc.select("div.title").text();
                String detail_time = doc.select("div.time").text();
                String detail_content = doc.select("div.content").html();

                String id = currentUrl.substring(currentUrl.lastIndexOf("=") + 1);

                Detail detail = map.get(id);
                detail.setCREATE_BY("洪福锐-");
                detail.setDETAIL_CONTENT(detail_content);
                detail.setDETAIL_LINK(currentUrl);
                detail.setDETAIL_TITLE(detail_title);
                detail.setSOURCE_NAME("中瑞投资");

                //入库
                if (dao.findDetailById(id) == null) {
                    dao.saveDetail(detail);
                }

        }
            } catch(Exception e){
                e.printStackTrace();
            }
    }

    public static void main(String[] args) {
        Spider.create(new ZhongruiProcessor()).addUrl("http://www.chiwayland.com/index.php?m=content&c=index&a=lists&catid=230&page=1")
                .run();
    }

}
