package com.example.epos.Models;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.epos.Activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class Utility {

    public static final List<Product> products = new ArrayList<>();

    public static List<Product> get_json(Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open("products.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read();
            is.close();
            json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);
            for (int i =0; i<jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String barcode = obj.getString("barcode");
                String name = obj.getString("Description");
                String category = obj.getString("Price");
                String price = obj.getString("Barcode");
                Product item = new Product(barcode, name, category, price);
                products.add(item);
                Toasty.info(context, item.getName(), Toast.LENGTH_LONG, true).show();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return products;
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
