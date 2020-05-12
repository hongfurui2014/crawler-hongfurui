package com.hfr.webmagic.processor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

public class LianTouProcessor implements PageProcessor {

    private Site site = Site.me().setSleepTime(3000);

    private int currentPage = 1;
    private int maxPage = 1;

    public Site getSite() {
        return site.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36").setCharset("utf-8");
    }

    public void process(Page page) {
        String currentUrl = page.getUrl().toString();

//        Document doc = Jsoup.parse(page.getHtml().toString());
        if (currentUrl.contains("notice")) {
            //列表页

//            Elements elements = doc.select("#aptList > tbody tr");

            List<Selectable> list = page.getHtml().xpath("//[@id=\"aptList\"]/tbody/tr").nodes();
            for (Selectable s : list) {
                String title = s.xpath("//td[1]/p/a/@title").get();
                String href = "http://caigou.mingyuanyun.com" + s.xpath("//td[1]/p/a/@href").get();
                String time = s.xpath("//td[2]/p/text()").get();
                //System.out.println(title + " + " + href + " + " + time);

                page.addTargetRequest(href);
            }

//            for (Element e : elements) {
//
//                String title = e.select("td:nth-child(1) > p > a").attr("title");
//
//                String href = "http://caigou.mingyuanyun.com/" + e.select("td:nth-child(1) > p > a").attr("href");
//
//                String time = e.select("td:nth-child(2)").text();
//
//                System.out.println(title + " + " + href + " + " + time);
//
//                //page.addTargetRequest(href);
//            }
//
//            //获取下一页链接
//            String nextHref = "http://caigou.mingyuanyun.com" + doc.select("#aptList > tfoot > tr > td > div > ul > li:nth-last-child(2) > a").get(0).attr("href");
//            page.addTargetRequest(nextHref);

            String s = page.getHtml().regex("共\\d+页").get();
            maxPage = Integer.parseInt(s.substring(1, s.length() - 1));

            if(currentPage < maxPage){
                //翻页
                currentPage++;
                page.addTargetRequest("http://caigou.mingyuanyun.com/ltzy/web_developerzone/home/notice?title=&registerEndDateStart=&registerEndDateEnd=&subCompanyId=&projectId=&page=" + currentPage);
            }
        } else {
            //详情页
//            String title = doc.select("#content > div > div.status_bar.end").text();
//
//            String time = doc.select("#content > div > div.recutit_meta > ul > li:nth-child(3)").text();
//            String date = time.substring(time.indexOf("：") + 1);
//
//            String content = doc.select("#content > div > div.desc_intro.desc_box").html();
//            System.out.println(title + " - " + date + " - " + content);
            String title = page.getHtml().xpath("//[@id=\"content\"]/div/div[1]/h1/text()").get();

            String time = page.getHtml().xpath("//div[@class='recutit_meta']//li[3]/text()").get(); //*[@id="content"]/div/div[2]/ul/li[3]
            String date = time.substring(time.indexOf("：") + 1);

            String content = page.getHtml().xpath("//div[@class='desc_intro desc_box']/html()").get();
            System.out.println(title + " - " + date + " - " + content);

            //入库
        }
    }

    public static void main(String[] args) {
        Spider.create(new LianTouProcessor()).addUrl("http://caigou.mingyuanyun.com/ltzy/web_developerzone/home/notice?title=&registerEndDateStart=&registerEndDateEnd=&subCompanyId=&projectId=&page=1")
                .run();
    }
}
