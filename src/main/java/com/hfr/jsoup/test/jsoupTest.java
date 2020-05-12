package com.hfr.jsoup.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class jsoupTest {

    public static void main(String[] args) throws IOException {
        test01();
        test02();
        test03();
    }

    public static void test01() throws IOException {
        Document document = Jsoup.connect("https://search.jd.com/Search?keyword=%E6%89%8B%E6%9C%BA&enc=utf-8&wq=%E6%89%8B%E6%9C%BA&pvid=60332dc2fb264002b91846becc2cbb15").get();
        Elements spacer = document.getElementsByClass("spacer");
        System.out.println(spacer.toString());
    }

    public static void test02(){
        String html = "<p>An <a href='http://example.com/'><b>example</b></a> link.</p>";
        Document doc = Jsoup.parse(html);//解析HTML字符串返回一个Document实现
        Element link = doc.select("a").first();//查找第一个a元素
        String text = doc.body().text(); // "An example link"//取得字符串中的文本
        System.out.println(text);

        String linkHref = link.attr("href"); // "http://example.com/"//取得链接地址
        String linkText = link.text(); // "example""//取得链接地址中的文本
        String linkOuterH = link.outerHtml();
        // "<a href="http://example.com"><b>example</b></a>"
        String linkInnerH = link.html(); // "<b>example</b>"//取得链接内的html内容
    }

    public static void test03() throws IOException {
        Document doc = Jsoup.connect("http://www.open-open.com").get();
        Element link = doc.select("a").first();
        String relHref = link.attr("href");
        System.out.println(relHref);
        String absHref = link.attr("abs:href");
        System.out.println(absHref);
    }
}
