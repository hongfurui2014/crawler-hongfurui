package com.hfr.httpclient.test;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
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

public class ZhongruiHttpClientTest {

    public static void main(String[] args) {
        getList("http://www.chiwayland.com/index.php?m=content&c=index&a=lists&catid=230&page=1");
    }

    public static void getList(String listUrl) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(listUrl);

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {

                String page = EntityUtils.toString(responseEntity);

                Document doc = Jsoup.parse(page);
                Elements elements = doc.select("div.zhaob");

                for (Element e : elements) {
                    String title = e.select("a").text();
                    String href = "http://www.chiwayland.com" + e.select("a").attr("href");
                    String time = e.select("div.date").get(0).text();
                    String date = time.substring(time.lastIndexOf("：") + 1);

                    System.out.println(title + " - " + href + " - " + date);

                    getDetail(href);

                    break;
                }

                System.out.println("-------------------------");

                //翻页
//                String nextUrl = "http://www.chiwayland.com/" + doc.select("a.next").get(0).attr("href");
//                getList(nextUrl);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
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

    public static void getDetail(String detailUrl) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(detailUrl);

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {

                String page = EntityUtils.toString(responseEntity);

                //详情页
                Document doc = Jsoup.parse(page);
                String title = doc.select("div.title").text();
                String time = doc.select("div.time").text();
                String content = doc.select("div.content").html();

                System.out.println(title + " + " + time + " + " + content);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
