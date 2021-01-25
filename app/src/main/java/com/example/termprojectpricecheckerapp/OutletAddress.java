package com.example.termprojectpricecheckerapp;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;

public class OutletAddress implements Runnable {
    String addressUrl;
    String shopAddress;
    ArrayList<String> addressList = new ArrayList<>();
    boolean firstOrSecond;
    ArrayList<String> allAddress = new ArrayList<>();
    public OutletAddress(String addressUrl, boolean firstOrSecond){
       this.addressUrl = addressUrl;
       this.firstOrSecond = firstOrSecond;
    }
    public OutletAddress(ArrayList<String> addressList,boolean firstOrSecond){
        this.addressList = addressList;
        this.firstOrSecond = firstOrSecond;
    }
    @Override
    public void run() {
        try{
            if(firstOrSecond){

                for(int i = 0; i < addressList.size(); i++){
                    Document document = Jsoup.connect(addressList.get(i)).get();
                    Elements address = document.select("span[class=LrzXr]");
                    if(!address.isEmpty()){
                        allAddress.add(address.text());
                    }
                    else{
                        allAddress.add("Online Shopping");
                    }
                }

            }else{
                Document doc = Jsoup.connect(addressUrl).get();
                Elements address = doc.select("span[class=LrzXr]");
                if(!address.isEmpty()){
                    shopAddress = address.text();
                }
                else{

                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public ArrayList<String> getAllAddress(){
        return allAddress;
    }

}
