package com.hfr.httpclient.test;

import com.hfr.bean.Detail;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                        System.out.println(detail_link + " - " + list_title + " - " + page_time);

                        //解析详情页

                    }
                }

                //获取总页数
                String totalNumStr = doc.select("div.mmggxlh a:nth-last-child(2)").text();
                if(totalNumStr != null){
                    int totalNum = Integer.parseInt(totalNumStr);
                    //从第二页开始post请求翻页
                    for(int i = 2; i <= totalNum; i++){
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
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36");
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("currentPage", curNum + ""));

            // 创建form表单对象
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "utf-8");
            formEntity.setContentType("application/x-www-form-urlencoded");

            // 把表单对象设置到httpPost中
            httpPost.setEntity(formEntity);

            response = httpClient.execute(httpPost);

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
                        System.out.println(detail_link + " - " + list_title + " - " + page_time);
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

                String detail_title = doc.select("span#lblTitle").text();

                System.out.println(detail_title);

//                String time = doc.select("#content > div > div.recutit_meta > ul > li:nth-child(3)").text();
//                String detail_time = time.substring(time.indexOf("：") + 1);
//
//                String detail_content = doc.select("#content > div > div.desc_intro.desc_box").html();
//
//
//                String id = detailUrl.substring(detailUrl.lastIndexOf("=") + 1);
//
//                Detail detail = map.get(id);
//                detail.setCREATE_BY("洪福锐");
//                detail.setDETAIL_CONTENT(detail_content);
//                detail.setDETAIL_LINK(detailUrl);
//                detail.setDETAIL_TITLE(detail_title);
//                detail.setSOURCE_NAME("联投置业");
//
//                //入库
//                if (dao.findDetailById(id) == null) {
//                    dao.saveDetail(detail);
//                }
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
