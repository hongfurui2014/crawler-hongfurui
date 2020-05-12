package com.hfr.httpclient.test;

import com.hfr.bean.Detail;
import com.hfr.dao.DetailDao;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QinZhouHttpClientTest {

    private static Map<String, Detail> map = new HashMap();

    private static DetailDao dao = new DetailDao();

    public static void main(String[] args) {
//        getList("http://ggzyjy.qinzhou.gov.cn/gxqzzbw/jyxx/001001/001001001/MoreInfo.aspx?CategoryNum=001001001");
        getList();
    }

    public static void getList() {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // 创建Post请求
        // 参数
        URI uri = null;
        try {
            // 将参数放入键值对类NameValuePair中,再放入集合中
            List<NameValuePair> params = new ArrayList();
            params.add(new BasicNameValuePair("__EVENTARGUMENT", "2"));
            uri = new URIBuilder().setScheme("http").setHost("ggzyjy.qinzhou.gov.cn").setPort(80)
                    .setPath("/gxqzzbw/jyxx/001001/001001001/MoreInfo.aspx?CategoryNum=001001001").setParameters(params).build();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }

        HttpPost httpPost = new HttpPost(uri);

        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpPost);
            if(response.getStatusLine().getStatusCode() == 200){
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {

                    String page = EntityUtils.toString(responseEntity);

                    System.out.println(page);

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
