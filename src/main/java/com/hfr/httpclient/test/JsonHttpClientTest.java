package com.hfr.httpclient.test;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class JsonHttpClientTest {

    private static int page = 0;

    private static int num = 1;

    public static void main(String[] args) {
        jsonHttpClientTest("https://movie.douban.com/j/search_subjects?type=movie&tag=%E7%83%AD%E9%97%A8&sort=time&page_limit=20&page_start=0");
    }

    /**
     * get 无参
     */
    public static void jsonHttpClientTest(String url) {
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 创建Get请求
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            //System.out.println("--- 响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                String returnJson = EntityUtils.toString(responseEntity);

//                JSONObject json = new JSONObject(returnJson);
//                JSONArray subjects = json.getJSONArray("subjects");
//
//                for(int i = 0; i < subjects.length(); i++){
////                    System.out.println(subjects.get(i));
//                    JSONObject j = new JSONObject(subjects.get(i).toString());
//
//                    System.out.println( num + " - " + j.getString("title") + " - " + j.getString("rate"));
//                    num++;
//                }
//
//                page = page + subjects.length();
//                if(subjects.length() > 0){
//                    jsonHttpClientTest("https://movie.douban.com/j/search_subjects?type=movie&tag=%E7%83%AD%E9%97%A8&sort=time&page_limit=20&page_start=" + page);
//                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpGet != null) {
                try {
                    httpGet.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
