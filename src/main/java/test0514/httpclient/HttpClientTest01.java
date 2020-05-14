package test0514.httpclient;

import com.hfr.bean.Detail;
import com.hfr.dao.DetailDao;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpClientTest01 {

    private static Map<String, Detail> map = new HashMap();

    private static DetailDao detailDao = new DetailDao();

    private static int curPage = 0;

    public static void main(String[] args) {
        doGetList("http://www.agrs.cgs.gov.cn/jryw/ggl/index.html");
    }

    public static void doGetList(String listUrl) {

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(listUrl);
        CloseableHttpResponse response = null;

        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                String page = EntityUtils.toString(entity, "utf-8");

                Document doc = Jsoup.parse(page);
                Elements elements = doc.select("div#Area_5577 > ul > li");

                for (Element e : elements) {

                    String list_title = e.select("a").text();
                    String list_link = e.select("a").attr("href");
                    list_link = "http://www.agrs.cgs.gov.cn/jryw/ggl" + list_link.substring(1, list_link.length());
                    String list_time = e.select("span").text();

//                    System.out.println(list_title + " - " + list_time + " - " + list_link);

                    getDetail(list_link);

                    break;
                }

//                System.out.println("--------------------------");

                //获取总页数
                String totalNumStr = "";

                Matcher matcher = Pattern.compile("countPage = (\\d+)//").matcher(page);
                if (matcher.find()) {
                    totalNumStr = matcher.group(1);
                }

                if (!totalNumStr.equals("")) {
                    int totalNum = Integer.parseInt(totalNumStr);
//                    System.out.println("总页数：" + totalNum);
                    if (curPage < totalNum) {
                        curPage++;
//                        doGetList("http://www.agrs.cgs.gov.cn/jryw/ggl/index_" + curPage + ".html");
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
            response = httpClient.execute(httpGet);

            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                String page = EntityUtils.toString(entity, "utf-8");

                Document doc = Jsoup.parse(page);

                String detail_title = doc.select("div.c_caption").text();
                String detail_time = doc.select("span.c_info_name").text();
                String detail_content = doc.select("div.c_body").text();

                System.out.println(detail_title + " - " + detail_time + " - " + detail_content);

                //入库
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
