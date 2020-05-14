package test0514.htmlunit;

import com.hfr.utils.HtmlunitUtils;
import org.jsoup.nodes.Document;

public class HtmlunitUtilsTest01 {

    public static void main(String[] args) {
        testGetHtmlPageResponse();
    }

    private static final String TEST_URL = "http://ent.sina.com.cn/film/";

    public static void testGetHtmlPageResponse() {
        HtmlunitUtils httpUtils = HtmlunitUtils.getInstance();
        httpUtils.setTimeout(3000);
        httpUtils.setWaitForBackgroundJavaScript(3000);
        try {
            String htmlPageStr = httpUtils.getHtmlPageResponse(TEST_URL);
            //TODO
            System.out.println(htmlPageStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testGetHtmlPageResponseAsDocument() {
        HtmlunitUtils httpUtils = HtmlunitUtils.getInstance();
        httpUtils.setTimeout(3000);
        httpUtils.setWaitForBackgroundJavaScript(3000);
        try {
            Document document = httpUtils.getHtmlPageResponseAsDocument(TEST_URL);
            //TODO
            System.out.println(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
