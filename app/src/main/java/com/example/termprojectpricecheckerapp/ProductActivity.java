package com.example.termprojectpricecheckerapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.function.Consumer;


public class ProductActivity extends AppCompatActivity{
    ListView listView;
    ArrayAdapter<String> adapter;
    ImageView imageView;
    Intent mapIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        imageView = findViewById(R.id.imageView);
        listView = findViewById(R.id.listView);

        final Intent intent = getIntent();
        String url = "https://www.google.com/search?q=" + intent.getStringExtra("scanResult")
                + "&client=firefox-b-d&source=lnms&tbm=shop&sa=X&ved=2ahUKEwjarYCejoPuAhVKCuwKHX88AskQ_AUoBHoECAkQBg&biw=2144&bih=1047";
        try {
            GetProductInfo getProductInfo = new GetProductInfo(url);
            final Thread thread = new Thread(getProductInfo);
            thread.start();
            thread.join();

            final ShopAndPrice shopAndPrice = new ShopAndPrice(getProductInfo.getUrl());
            Thread thread1 = new Thread(shopAndPrice);
            thread1.start();
            thread1.join();


            ArrayList<String> arrayList = shopAndPrice.getShopAndPriceList();

            ArrayList<String> addressList = new ArrayList<>();
            for(int i = 0; i < arrayList.size(); i++) {
                String[] splitShops = arrayList.get(i).split("   ");
                String shop = splitShops[0];
                shop = shop.replaceAll("&", "%26");
                String address = "https://www.google.com/search?client=firefox-b-d&q=" + shop;
                addressList.add(address);
            }
            OutletAddress outletAddress = new OutletAddress(addressList,true);
            Thread thread2 = new Thread(outletAddress);
            thread2.start();
            thread2.join();

            ArrayList<String> allAddressList = outletAddress.getAllAddress();
            ArrayList<String> tempArrList = new ArrayList<>();
            for(int i = 0; i < allAddressList.size(); i++){
                if(allAddressList.get(i).equals("Online Shopping")){
                   String temp = (String) shopAndPrice.getShopAndPriceList().get(i);
                   temp += "   Online Shopping";
                   shopAndPrice.getShopAndPriceList().set(i,temp);
                }else{
                    tempArrList.add(allAddressList.get(i));
                }
            }

            Picasso.get().load(shopAndPrice.getImageUrl()).into(imageView);
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, shopAndPrice.getShopAndPriceList());
            listView.setAdapter(adapter);

            Distance proximity = new Distance(ProductActivity.this,tempArrList, new Consumer<ArrayList<Integer>>() {
                @Override
                public void accept(ArrayList<Integer> tempKmArrList) {
                    for(int i = 0 , index = 0; i < shopAndPrice.getShopAndPriceList().size(); i++){
                        String value = shopAndPrice.getShopAndPriceList().get(i);
                        if(!value.contains("Online Shopping")){
                            if(index < tempKmArrList.size()){
                                shopAndPrice.getShopAndPriceList().set(i,value + "   " + tempKmArrList.get(index) + " KM");
                                index++;
                            }
                        }

                    }
                    adapter.notifyDataSetChanged();
                }
            });

            Thread thread3 = new Thread(proximity);
            thread3.start();



            mapIntent = new Intent(this, MapsActivity.class);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
                    String[] splitShop = adapterView.getItemAtPosition(position).toString().split("  ");

                    if(splitShop[0].equals("D&R")){
                        splitShop[0] = "d%26r";
                    }
                    String addressUrl = "https://www.google.com/search?client=firefox-b-d&q=" + splitShop[0];
                    OutletAddress outletAddress = new OutletAddress(addressUrl,false);
                    Thread threadForAddress = new Thread(outletAddress);
                    threadForAddress.start();
                    try {
                        threadForAddress.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println(adapterView.getItemAtPosition(position));

                    mapIntent.putExtra("shopAddress",outletAddress.getShopAddress());
                    startActivity(mapIntent);
                }
            });

        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }
}