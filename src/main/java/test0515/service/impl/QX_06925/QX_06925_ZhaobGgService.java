package test0515.service.impl.QX_06925;

import com.hfr.bean.Detail;
import com.hfr.dao.DetailDao;
import com.hfr.webmagic.processor.Processor002;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QX_06925_ZhaobGgService implements PageProcessor {
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

                    d.setID(id);
                    d.setSOURCE_NAME("安徽省港航集团有限公司-通知公告");
                    d.setDETAIL_LINK(list_link);
                    d.setPAGE_TIME(list_time);
                    d.setLIST_TITLE(list_title);
                    d.setCREATE_BY("洪福锐_processor");

                    map.put(id, d);

//                    //添加详情页
//                    page.addTargetRequest(list_link);
                }

                //获取总页数
//                String totalNumStr = "";
//
//                Matcher matcher = Pattern.compile("页次<i>(\\d+)</i>/(\\d+)</span>").matcher(pageHtml);
//                if (matcher.find()) {
//                    totalNumStr = matcher.group(2);
//                    maxNum = Integer.parseInt(totalNumStr);
//                }
//
//                //翻页
//                if (pageNum < maxNum) {
//                    pageNum++;
//                    //添加翻页链接
//                    Request request = new Request("http://www.ahsgh.com/ahghjtweb/web/list");
//                    request.setMethod(HttpConstant.Method.POST);
//
//                    Map<String, Object> params = new HashMap();
//                    params.put("listPage", "list");
//                    params.put("intCurPage", pageNum + "");
//                    params.put("intPageSize", "10");
//                    params.put("strColId", "20782f569264489f87995ad0773ff626");
//                    params.put("strWebSiteId", "4c5fcf57602b48a0acde5a4ef3ede48d");
//
//                    request.setRequestBody(HttpRequestBody.form(params, "utf-8"));
//
//                    //page.addTargetRequest(request);
//                }
            } else {
                //详情页
                String id = currentUrl.substring(currentUrl.indexOf("=") + 1, currentUrl.indexOf("&"));

                String detail_title = doc.select("div.article-conca.clearfix > h2").text();
                String detail_time = doc.select("p.article-information > span:nth-child(1)").text();
                detail_time = detail_time.substring(detail_time.indexOf("：") + 1);

                Element detailEle = doc.select("div.article-conca.clearfix").get(0);

                Elements attachEls = detailEle.select("div.attachment a");

                //获取附件
                if (attachEls.size() > 0) {
                    Element attach = detailEle.select("div.attachment a").get(0);
                    String href = attach.attr("href");
                    href = href.substring(href.lastIndexOf("(") + 1, href.lastIndexOf(")"));
                    String[] hrefArr = href.split(",");
                    String attachUrl = "http://www.ahsgh.com/ahghjtweb/affix/download?filePath=" + hrefArr[0].substring(1, hrefArr[0].length() - 1) + "&fileName=" + hrefArr[1].substring(1, hrefArr[1].length() - 1);
                    System.out.println(attachUrl);
                    detailEle.select("div.attachment a").get(0).attr("href", attachUrl);
                }

                String detail_content = detailEle.html();

                Detail d = map.get(id);
                d.setDETAIL_TITLE(detail_title);
                d.setDETAIL_CONTENT(detail_content);

                //入库
                if (detailDao.findDetailById(id) == null) {
                    detailDao.saveDetail(d);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        Spider.create(new QX_06925_ZhaobGgService()).addUrl("https://www.jinshishi.gov.cn/czj/zhdt/tzgg")
                .thread(1)
                .run();
    }
}
