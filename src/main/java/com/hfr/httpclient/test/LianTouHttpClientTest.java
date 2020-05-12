package com.hfr.httpclient.test;

import com.hfr.bean.Detail;
import com.hfr.dao.DetailDao;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LianTouHttpClientTest {

    private static Map<String, Detail> map = new HashMap();

    private static DetailDao dao = new DetailDao();

    public static void main(String[] args) {
        getList("http://caigou.mingyuanyun.com/ltzy/web_developerzone/home/notice?title=&registerEndDateStart=&registerEndDateEnd=&subCompanyId=&projectId=&page=1");
    }

    public static void getList(String listUrl) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(listUrl);

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                // 从响应模型中获取响应实体
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {

                    String page = EntityUtils.toString(responseEntity);

                    Document doc = Jsoup.parse(page);
                    Elements elements = doc.select("#aptList > tbody tr");

                    for (Element e : elements) {

                        String list_title = e.select("p.col_txt > a").attr("title");

                        String list_link = "http://caigou.mingyuanyun.com/" + e.select("p.col_txt > a").attr("href");

                        String list_time = e.select("td:nth-child(2)").text();

                        String id = list_link.substring(list_link.lastIndexOf("=") + 1);

                        Detail d = new Detail();
                        d.setLIST_TITLE(list_title);
                        d.setPAGE_TIME(list_time);
                        d.setID(id);

                        map.put(id, d);

                        getDetail(list_link);
                    }

//                System.out.println("---------------------------------");
                    //翻页
                    //获取下一页链接
                    String nextHref = doc.select("ul.pagination > li:nth-last-child(2) > a").get(0).attr("href");
                    if (nextHref != null) {
                        nextHref = "http://caigou.mingyuanyun.com" + nextHref;
                        getList(nextHref);
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

    public static void getDetail(String detailUrl) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(detailUrl);

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                // 从响应模型中获取响应实体
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {

                    String page = EntityUtils.toString(responseEntity);

                    //详情页
                    Document doc = Jsoup.parse(page);
                    String detail_title = doc.select("#content > div > div.status_bar.end").text();

                    String time = doc.select("#content > div > div.recutit_meta > ul > li:nth-child(3)").text();
                    String detail_time = time.substring(time.indexOf("：") + 1);

                    String detail_content = doc.select("#content > div > div.desc_intro.desc_box").html();


                    String id = detailUrl.substring(detailUrl.lastIndexOf("=") + 1);

                    Detail detail = map.get(id);
                    detail.setCREATE_BY("洪福锐");
                    detail.setDETAIL_CONTENT(detail_content);
                    detail.setDETAIL_LINK(detailUrl);
                    detail.setDETAIL_TITLE(detail_title);
                    detail.setSOURCE_NAME("联投置业");

                    //入库
                    if (dao.findDetailById(id) == null) {
                        dao.saveDetail(detail);
                    }
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
}
