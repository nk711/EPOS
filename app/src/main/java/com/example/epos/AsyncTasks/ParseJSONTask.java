package com.example.epos.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.example.epos.Adapters.ProductAdapter;
import com.example.epos.Models.Product;
import com.example.epos.Layout.FastScroller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ParseJSONTask extends AsyncTask<Void, Void, List<Product>> {

    private List<Product> products;

    private Context mContext;
    private RecyclerView mRecyclerView;
    private FastScroller fastScroller;
    private ProductAdapter adapter;
    private String query;
    private String tag;


    public ParseJSONTask(Context context, RecyclerView mRecyclerView,FastScroller fastScroller, String tag, String query) {
       this.mContext = context;
       this.mRecyclerView = mRecyclerView;
       this.fastScroller = fastScroller;
       this.tag = tag;
       if (query!= null) this.query = query.toLowerCase();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<Product> doInBackground(Void... voids) {
        this.products = new ArrayList<>();
        String json;
        JSONObject reader;
        JSONArray products;
        try {
            // Reads the product.json file
            InputStream is = mContext.getAssets().open("products.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            //Stores the contents on to a string variable json
            json = new String(buffer, StandardCharsets.UTF_8);
            reader = new JSONObject(json);
            products = reader.getJSONArray("products");
            for (int i =0; i<products.length(); i++) {
                JSONObject obj = products.getJSONObject(i);
                String barcode = obj.getString("Barcode");
                String name = obj.getString("Description");
                String category = obj.getString("Department");
                String price = obj.getString("Price");
                Product item = new Product(barcode, name, category, price);

                if (query==null|| query.equals("")) {
                    this.products.add(item);
                }
                else if (name.toLowerCase().startsWith(query)) {
                    this.products.add(0, item);
                }
                else if (name.toLowerCase().contains(query)){
                    if (this.products.size()==0) this.products.add(item);
                    if (this.products.size()!=0) this.products.add(this.products.size()-1, item);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this.products;
    }

    @Override
    protected void onPostExecute(List<Product> products) {
        super.onPostExecute(products);
        adapter = new ProductAdapter(products, tag);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(adapter);
        fastScroller.setRecyclerView(mRecyclerView);
    }
}
