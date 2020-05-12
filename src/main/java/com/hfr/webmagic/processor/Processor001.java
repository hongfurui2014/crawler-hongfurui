package com.hfr.webmagic.processor;

import com.hfr.bean.Detail;
import com.hfr.dao.DetailDao;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Processor001 implements PageProcessor {
    private Site site = Site.me().setSleepTime(3000);

    private Map<String, Detail> map = new HashMap();

    private DetailDao dao = new DetailDao();

    public Site getSite() {
        return site.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36").setCharset("utf-8");
    }

    public void process(Page page) {
        System.out.println(page.getHtml());
    }

    public static void main(String[] args) {
        Request request = new Request("http://www.yaggzy.org.cn/jyxx/jsgcZbgg");
        request.setMethod(HttpConstant.Method.POST);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("currentPage", "3");

        request.setRequestBody(HttpRequestBody.form(params, "utf-8"));

//        Request request = new Request("");
//        request.setMethod(HttpConstant.Method.POST);
//        request.setRequestBody(HttpRequestBody.json("{'id':1}","utf-8"));

        Spider.create(new Processor001()).addRequest(request).thread(5).run();
    }
}