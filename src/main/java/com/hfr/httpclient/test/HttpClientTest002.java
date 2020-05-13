package com.hfr.httpclient.test;

import com.hfr.bean.Detail;
import com.hfr.dao.DetailDao;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpClientTest002 {

    private static Map<String, Detail> map = new HashMap();

    private static DetailDao detailDao = new DetailDao();

    public static void main(String[] args) {
        doGetList("http://www.ahsgh.com/ahghjtweb/web/list?strColId=20782f569264489f87995ad0773ff626&strWebSiteId=4c5fcf57602b48a0acde5a4ef3ede48d");
    }

    public static void doGetList(String listUrl) {

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(listUrl);
        CloseableHttpResponse response = null;

        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                String page = EntityUtils.toString(response.getEntity());
                Document doc = Jsoup.parse(page);
                Elements elements = doc.select("ul.tab01open.tab01tca > li");

                for (Element e : elements) {
                    Detail d = new Detail();

                    String list_title = e.select("h2.fl").text();
                    String list_time = e.select("i.fr").text();
                    String list_link = e.select("a").attr("href");
                    String id = "";

                    list_link = list_link.substring(list_link.lastIndexOf("(") + 1, list_link.lastIndexOf(")"));
                    String[] linkArr = list_link.split(",");
                    if (linkArr != null) {
                        id = linkArr[0].substring(1, linkArr[0].length() - 1);

                        list_link = "http://www.ahsgh.com/ahghjtweb/web/view?strId=" + id
                                + "&strColId=" + linkArr[1].substring(1, linkArr[1].length() - 1)
                                + "&strWebSiteId=" + linkArr[2].substring(1, linkArr[2].length() - 1);

                        id = id + "-httpclient";
                        d.setID(id);
                    }

                    d.setSOURCE_NAME("安徽省港航集团有限公司-通知公告");
                    d.setDETAIL_LINK(list_link);
                    d.setPAGE_TIME(list_time);
                    d.setLIST_TITLE(list_title);
                    d.setCREATE_BY("洪福锐_httplient");

                    map.put(id, d);
                    getDetail(list_link, id);
                }

                //获取总页数
                String totalNumStr = "";

                Matcher matcher = Pattern.compile("页次<i>(\\d+)</i>/(\\d+)</span>").matcher(page);
                if (matcher.find()) {
                    totalNumStr = matcher.group(2);
                }

                if (!totalNumStr.equals("")) {
                    int totalNum = Integer.parseInt(totalNumStr);
                    System.out.println("总页数：" + totalNum);
                    for (int i = 2; i <= totalNum; i++) {
                        doPostList(i);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void doPostList(int curNum) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;

        try {
            httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost("http://www.ahsgh.com/ahghjtweb/web/list");
            httpPost.addHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");

            List<NameValuePair> params = new ArrayList();

            params.add(new BasicNameValuePair("listPage", "list"));
            params.add(new BasicNameValuePair("intCurPage", curNum + ""));
            params.add(new BasicNameValuePair("intPageSize", "10"));
            params.add(new BasicNameValuePair("strColId", "20782f569264489f87995ad0773ff626"));
            params.add(new BasicNameValuePair("strWebSiteId", "4c5fcf57602b48a0acde5a4ef3ede48d"));
            params.add(new BasicNameValuePair("nowPage", "1"));
            httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));

            response = httpClient.execute(httpPost);

            if (response.getStatusLine().getStatusCode() == 200) {
                String page = EntityUtils.toString(response.getEntity());
                Document doc = Jsoup.parse(page);
                Elements elements = doc.select("ul.tab01open.tab01tca > li");

                for (Element e : elements) {
                    Detail d = new Detail();

                    String list_title = e.select("h2.fl").text();
                    String list_time = e.select("i.fr").text();
                    String list_link = e.select("a").attr("href");
                    String id = "";

                    list_link = list_link.substring(list_link.lastIndexOf("(") + 1, list_link.lastIndexOf(")"));
                    String[] linkArr = list_link.split(",");
                    if (linkArr != null) {
                        id = linkArr[0].substring(1, linkArr[0].length() - 1);

                        list_link = "http://www.ahsgh.com/ahghjtweb/web/view?strId=" + id
                                + "&strColId=" + linkArr[1].substring(1, linkArr[1].length() - 1)
                                + "&strWebSiteId=" + linkArr[2].substring(1, linkArr[2].length() - 1);

                        id = id + "-httpclient";
                        d.setID(id);
                    }

                    d.setSOURCE_NAME("安徽省港航集团有限公司-通知公告");
                    d.setDETAIL_LINK(list_link);
                    d.setPAGE_TIME(list_time);
                    d.setLIST_TITLE(list_title);
                    d.setCREATE_BY("洪福锐_httplient");

                    map.put(id, d);
                    getDetail(list_link, id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void getDetail(String detailUrl, String id) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(detailUrl);

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == 200) {
                String page = EntityUtils.toString(response.getEntity());

                Document doc = Jsoup.parse(page);

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
        } finally {
            try {
                // 释放资源
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
