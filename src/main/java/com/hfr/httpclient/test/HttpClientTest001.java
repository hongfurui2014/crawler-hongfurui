package com.hfr.httpclient.test;

import org.apache.http.HttpEntity;
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
import java.util.ArrayList;
import java.util.List;

public class HttpClientTest001 {

    public static void main(String[] args) {
        doGetList("http://www.yaggzy.org.cn/jyxx/jsgcZbgg");
    }

    public static void doGetList(String listUrl) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(listUrl);

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity responseEntity = response.getEntity();

                String page = EntityUtils.toString(responseEntity);

                Document doc = Jsoup.parse(page);

                //解析获取标题、时间、链接
                Elements elements = doc.select("li.clearfloat tr");

                for (Element e : elements) {
                    String detail_link = e.select("a").attr("href");
                    String list_title = e.select("a").attr("title");
                    String page_time = e.select("td:nth-last-child(2)").text();

                    if (detail_link != null && !detail_link.equals("")) {
                        detail_link = "http://www.yaggzy.org.cn" + detail_link;
                        //解析详情页
                        getDetail(detail_link);
                    }
                }

                //获取总页数
                String totalNumStr = doc.select("div.mmggxlh a:nth-last-child(2)").text();
                if (totalNumStr != null) {
                    int totalNum = Integer.parseInt(totalNumStr);
                    //从第二页开始post请求翻页
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
            HttpPost httpPost = new HttpPost("http://www.yaggzy.org.cn/jyxx/jsgcZbgg");
            httpPost.addHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            List<NameValuePair> params = new ArrayList();

            params.add(new BasicNameValuePair("currentPage", curNum + ""));
            httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));

            response = httpClient.execute(httpPost);

            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity responseEntity = response.getEntity();

                String page = EntityUtils.toString(responseEntity);

                Document doc = Jsoup.parse(page);

                System.out.println("----------------------------");

                //解析获取标题、时间、链接
                Elements elements = doc.select("li.clearfloat tr");

                for (Element e : elements) {
                    String detail_link = e.select("a").attr("href");
                    String list_title = e.select("a").attr("title");
                    String page_time = e.select("td:nth-last-child(2)").text();

                    if (detail_link != null && !detail_link.equals("")) {
                        detail_link = "http://www.yaggzy.org.cn" + detail_link;
                        //解析详情页
                        getDetail(detail_link);
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
            if (response.getStatusLine().getStatusCode() == 200) {

                String page = EntityUtils.toString(responseEntity);

                //详情页
                Document doc = Jsoup.parse(page);

                String detail_title = doc.select("div.table_title").text();
                String detail_content = doc.select("div.content_all_nr > table").get(0).html();

                System.out.println(detail_title);

//                //入库
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
