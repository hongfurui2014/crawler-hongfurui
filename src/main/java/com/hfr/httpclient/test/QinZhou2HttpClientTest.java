package com.hfr.httpclient.test;

import com.alibaba.fastjson.JSON;
import com.hfr.bean.Detail;
import com.hfr.bean.User;
import com.hfr.dao.DetailDao;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QinZhou2HttpClientTest {

    private static Map<String, Detail> map = new HashMap();

    private static DetailDao dao = new DetailDao();

    public static void main(String[] args) {
        //doGetList("http://ggzyjy.qinzhou.gov.cn/gxqzzbw/jyxx/001001/001001001/MoreInfo.aspx?CategoryNum=001001001");

        getDetail("http://ggzyjy.qinzhou.gov.cn/gxqzzbw/ZBGG_Detail.aspx?InfoID=f8d3d0f4-a1ab-4d15-8c53-e3b1fc7faa32&CategoryNum=001001001");
    }

    public static void doGetList(String listUrl) {
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

//                    System.out.println(page);

                    Document doc = Jsoup.parse(page);

                    //解析获取标题、时间、链接
                    Elements elements = doc.select("table#MoreInfoList1_DataGrid1 > tbody > tr");

                    for (Element e : elements) {
                        String detail_link = e.select("a").attr("href");
                        String list_title = e.select("a").attr("title");
                        String page_time = e.select("td:nth-child(3)").text();

                        Detail detail = new Detail();
                        detail.setDETAIL_LINK(detail_link);
                        detail.setLIST_TITLE(list_title);
                        detail.setPAGE_TIME(page_time);

//                        System.out.println(detail);
                    }

                    //获取__CSRFTOKEN 和 __VIEWSTATE
                    String __CSRFTOKEN = doc.select("input#__CSRFTOKEN").attr("value");
                    String __VIEWSTATE = doc.select("input#__VIEWSTATE").attr("value");

//                    获取总页数
                    Matcher matcher = Pattern.compile("总页数(\\D*)(\\d+)").matcher(page);
                    if (matcher.find()) {
                        String totalPageStr = matcher.group(2);

                        if (totalPageStr != null) {
                            int totalPage = Integer.parseInt(totalPageStr);
                            System.out.println("总页数：" + totalPage);

                            //从第二页开始用post请求
                            for (int i = 2; i <= 10; i++) {
//                                doPostList(__CSRFTOKEN, __VIEWSTATE, i + "");
                                break;
                            }
                        }
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

    public static void doPostList(String __CSRFTOKEN, String __VIEWSTATE, String pageNum) {
        CloseableHttpClient httpClient = null;

        CloseableHttpResponse response = null;

        try {
            httpClient = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost("http://ggzyjy.qinzhou.gov.cn/gxqzzbw/jyxx/001001/001001001/MoreInfo.aspx?CategoryNum=001001001");
            httpPost.addHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36");
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.setHeader("Upgrade-Insecure-Requests", "1");

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("__CSRFTOKEN", __CSRFTOKEN));
            params.add(new BasicNameValuePair("__VIEWSTATE", __VIEWSTATE));
            params.add(new BasicNameValuePair("__EVENTTARGET", "MoreInfoList1$Pager"));
            params.add(new BasicNameValuePair("__EVENTARGUMENT", pageNum));
            params.add(new BasicNameValuePair("__VIEWSTATEENCRYPTED", ""));

            // 创建form表单对象
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "utf-8");
            formEntity.setContentType("application/x-www-form-urlencoded");

            // 把表单对象设置到httpPost中
            httpPost.setEntity(formEntity);

            response = httpClient.execute(httpPost);

            HttpEntity responseEntity = response.getEntity();

            System.out.println("响应状态为:" + response.getStatusLine().getStatusCode());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
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

    /**
     * 详情
     *
     * @param detailUrl
     */
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
