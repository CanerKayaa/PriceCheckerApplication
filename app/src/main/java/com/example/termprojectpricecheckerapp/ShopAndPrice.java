package com.example.termprojectpricecheckerapp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class ShopAndPrice implements Runnable{
    ArrayList<String> priceAndShopsArrList = new ArrayList<>();
    ArrayList<Double> pricesArrList = new ArrayList<>();
    ArrayList<String> shopsArrList = new ArrayList<>();
    String imageUrl;
    String shoppingUrl;
    final StringBuilder stringBuilder = new StringBuilder();

    public ShopAndPrice(String shoppingUrl){
        this.shoppingUrl = shoppingUrl;
    }

    @Override
    public void run() {
        try {
            Document doc = Jsoup.connect(shoppingUrl).get();
            Elements shops = doc.select("a[class=sh-osd__seller-link shntl]");

            for (Element shop : shops) {

                Element shopStr = (Element) shop.childNode(0);
                shopStr.childNode(0);
                String shopCurrent = shopStr.text();
                shopsArrList.add(shopCurrent);
            }

            Elements prices = doc.select("div[class=QXiyfd]");
            for(Element price : prices){
                Element priceStr = (Element) price.childNode(0);
                priceStr.childNode(0);
                String priceCurrent  = priceStr.text();

                String[] splitTry = priceCurrent.split("₺");
                splitTry[1] = splitTry[1].replaceAll(",",".");

                pricesArrList.add(Double.parseDouble(splitTry[1]));

            }

            for(int i = 0; i < shopsArrList.size() && i < pricesArrList.size(); i++){
                String concatShopPrice = shopsArrList.get(i) + pricesArrList.get(i);
                priceAndShopsArrList.add(concatShopPrice);
            }

            priceAndShopsArrList.clear();
            while(!pricesArrList.isEmpty()){
                int index = pricesArrList.indexOf(Collections.min(pricesArrList));
                String concatShopPrice = shopsArrList.get(index) + "   " + "₺" + pricesArrList.get(index);
                priceAndShopsArrList.add(concatShopPrice);
                shopsArrList.remove(index);
                pricesArrList.remove(index);
            }
            System.out.println("Sorted ShopPrice List :  " + priceAndShopsArrList);
            Elements images = doc.select("img[class=sh-div__image sh-div__current]");
            imageUrl = images.attr("src");


        } catch (IOException e) {
            stringBuilder.append("Error : ").append(e.getMessage()).append("\n");
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ArrayList<String > getShopAndPriceList(){
        return priceAndShopsArrList;
    }

}
