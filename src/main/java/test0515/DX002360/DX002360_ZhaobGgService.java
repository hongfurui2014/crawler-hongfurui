package test0515.DX002360;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DX002360_ZhaobGgService implements PageProcessor {
    private Site site = Site.me().setSleepTime(3000).setCharset("utf-8");

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
            if (!currentUrl.contains("content")) {

                Elements elements = doc.select("ul.newsList li");

                //移除分割线
                elements = elements.select("li:not(.split)").remove();

                for (Element e : elements) {
                    Detail d = new Detail();

                    String list_title = e.select("a").text();
                    String list_time = e.select("span").text();
                    String list_link = "https://www.jinshishi.gov.cn/" + e.select("a").attr("href");
                    String id = list_link.substring(list_link.lastIndexOf("_") + 1);

                    System.out.println(id + "-" + list_title + " - " + list_time + " - " + list_link);
                    if (detailDao.findDetailById(id) == null) {
                        d.setID(id);
                        d.setSOURCE_NAME("津市市财政局");
                        d.setDETAIL_LINK(list_link);
                        d.setPAGE_TIME(list_time);
                        d.setLIST_TITLE(list_title);
                        d.setCREATE_BY("洪福锐_processor");

                        map.put(id, d);

//                    //添加详情页
                        page.addTargetRequest(list_link);
                    }
                }

                //获取总页数
                String totalNumStr = "";

                Matcher matcher = Pattern.compile("共(\\d+)页").matcher(pageHtml);
                if (matcher.find()) {
                    totalNumStr = matcher.group(1);
                    maxNum = Integer.parseInt(totalNumStr);
                }
                System.out.println("------------------------------------");

                //翻页
                if (pageNum < maxNum) {
                    pageNum++;
//                    page.addTargetRequest("https://www.jinshishi.gov.cn/czj/zhdt/tzgg_" + pageNum);
                }
            } else {
                //详情页
                String id = currentUrl.substring(currentUrl.indexOf("_") + 1);

                System.out.println(id + " - ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        Spider.create(new DX002360_ZhaobGgService()).addUrl("http://www.sciencep.com/zxzx2017/tzgg2017/")
                .thread(1)
                .run();
    }
}
