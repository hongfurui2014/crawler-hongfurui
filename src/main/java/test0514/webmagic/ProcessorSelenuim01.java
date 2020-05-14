package test0514.webmagic;

import com.hfr.bean.Detail;
import com.hfr.dao.DetailDao;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * webmagic selenuim处理js动态渲染后的html页面
 */
public class ProcessorSelenuim01 implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(3000).setCharset("utf-8");

    private static Map<String, Detail> map = new HashMap();

    private static DetailDao detailDao = new DetailDao();

    private int pageNum = 1;

    private int maxNum = 1;

    public Site getSite() {
        return site.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
    }

    public void process(Page page) {
        String currentUrl = page.getUrl().toString();
        String pageHtml = page.getHtml().toString();

        try {
            Document doc = Jsoup.parse(pageHtml);
            if (currentUrl.contains("index")) {

                Elements elements = doc.select("div#Area_5577 > ul > li");

                for (Element e : elements) {

                    String list_title = e.select("a").text();
                    String list_link = e.select("a").attr("href");
                    list_link = "http://www.agrs.cgs.gov.cn/jryw/ggl" + list_link.substring(1, list_link.length());
                    String list_time = e.select("span").text();

                    page.addTargetRequest(list_link);
                    break;
                }

                //获取总页数
                String totalNumStr = "";

                Matcher matcher = Pattern.compile("共(\\d+)页").matcher(pageHtml);
                if (matcher.find()) {
                    totalNumStr = matcher.group(1);
                    maxNum = Integer.parseInt(totalNumStr);
                }

                //翻页
                if (pageNum < maxNum) {
                    //添加翻页链接
//                    page.addTargetRequest("http://www.agrs.cgs.gov.cn/jryw/ggl/index_" + pageNum + ".html");
                    pageNum++;
                }
            } else {
                //详情页
                String detail_title = doc.select("div.c_caption").text();
//                String detail_time = doc.select("p.article-information > span:nth-child(1)").text();
                String detail_contont = doc.select("div.TRS_Editor").html();

                System.out.println(detail_title + " - " + detail_contont);
                System.out.println("============================================================");

                //入库
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        System.setProperty("selenuim_config", "D:\\code\\idea\\crawler-hongfurui\\src\\main\\resources\\config.ini");

        Spider.create(new ProcessorSelenuim01())
                .addUrl("http://www.agrs.cgs.gov.cn/jryw/ggl/index.html")
                .setDownloader(new SeleniumDownloader("D:\\soft_ware\\chromedriver.exe"))
                .thread(1)
                .run();
    }
}
