package com.example.termprojectpricecheckerapp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class GetProductInfo implements Runnable {
    String url;
    final StringBuilder stringBuilder = new StringBuilder();

    public GetProductInfo(String url) throws InterruptedException {
        this.url = url;
        stringBuilder.append("https://www.google.com");
    }
    @Override
    public void run() {

        try {
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.select("a[href]");
            for (Element link : links) {

                String linkStr = link.attr("href");
                if(linkStr.startsWith("/shopping/product/") && linkStr.endsWith("#online")){
                    stringBuilder.append(linkStr);
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }  

    public String getUrl() {
        return stringBuilder.toString();
    }
}
