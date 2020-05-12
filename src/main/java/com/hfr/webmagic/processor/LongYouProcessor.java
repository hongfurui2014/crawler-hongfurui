package com.hfr.webmagic.processor;

import com.hfr.bean.Details;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class LongYouProcessor implements PageProcessor {

    private Site site = Site.me().setSleepTime(3000);

    int pageNum = 1;
    int maxPageNum = 1;

    public Site getSite() {
        return site.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36").setCharset("gbk");
    }

    public void process(Page page) {
        String currentUrl = page.getUrl().toString();

        if (currentUrl.contains("page")) {
            //列表页

            Document doc = Jsoup.parse(page.getHtml().toString());
            Elements elements = doc.select("body > table:nth-child(7) > tbody > tr > td > table:nth-child(2) > tbody > tr > td:nth-child(1) > table:nth-child(4) > tbody > tr > td > table:nth-child(1) > tbody").get(0).select("tr:nth-child(odd)");

            for(Element e : elements){
                String title = e.select("a").attr("title");
                String url = "http://122.227.101.77:9090/view/" + e.select("a").attr("href");
                String date = e.select("tr > td:nth-child(3)").text();

                Details d = new Details();
                d.setTitle(title);
                d.setDate(date);
                d.setUrl(url);

//                System.out.println(d);

                page.addTargetRequest(url);
            }

            //获取到最大页码
            String str = page.getHtml().regex("页次:\\d+/\\d+页").all().get(0);
            pageNum = Integer.parseInt(str.substring(3, str.lastIndexOf("/")));
            System.out.println("当前页码数是：" + pageNum);
            String str2 = str.substring(str.lastIndexOf("/"));
            maxPageNum = Integer.parseInt(str2.substring(1, str2.length() - 1));
            System.out.println("最大页码数是：" + maxPageNum);

            //翻页
            if (pageNum < 5) {
                pageNum++;
                //添加翻页链接
                page.addTargetRequest("http://122.227.101.77:9090/view/index.asp?ClassId=0205&ParentClassId=02&page=" + pageNum);
            }
        } else {
            //详情页
            Document doc = Jsoup.parse(page.getHtml().toString());
            String title = doc.select("#Zoom > p:nth-child(1) > span > strong").text();
            System.out.println(title);
            String time = page.getHtml().regex("\\d+/\\d+/\\d+ \\d+:\\d+:\\d+").all().get(0);
            System.out.println(time);
            String content = doc.select("#Zoom > p:nth-child(2)").html();
            System.out.println(content);

            //入库
        }
    }

    public static void main(String[] args) {
        Spider.create(new LongYouProcessor()).addUrl("http://122.227.101.77:9090/view/index.asp?ClassId=0205&ParentClassId=02&page=1")
                .run();
    }
}
