package com.example.epos.AsyncTasks;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.epos.Activities.MainActivity;
import com.example.epos.Dialog.InfoDialog;
import com.example.epos.Models.Product;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import androidx.appcompat.app.AlertDialog;



public class GetInfoTask extends AsyncTask<Void, Void, Product> {
    private Product product;
  //  private String url;
    private Context mContext;
    private String barcode;
    private String tag;


    public GetInfoTask(Context context, String barcode, String tag) {
        this.mContext = context;
        this.barcode = barcode;
        this.tag = tag;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Product doInBackground(Void... voids) {
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
                if (barcode.equals(this.barcode)){
                    this.product = item;
                  //  this.url = getImage();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this.product;
    }

    @Override
    protected void onPostExecute(Product product) {
        super.onPostExecute(product);
        try{
            Bundle args = new Bundle();
            args.putSerializable("Product", product);
          //args.putString("Image", this.url);
            final MainActivity activity = (MainActivity) mContext;
            InfoDialog dialog = new InfoDialog();

            dialog.setArguments(args);
            dialog.show(activity.getSupportFragmentManager(), "InfoDialog");

        } catch (ClassCastException e) {
            Log.d("GetInfoTask", "Can't get the fragment manager with this");
        }
    }

}
