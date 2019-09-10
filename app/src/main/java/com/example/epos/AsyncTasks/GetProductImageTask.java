package com.example.epos.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
//import com.bumptech.glide.Glide;
import com.example.epos.Models.Product;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import es.dmoral.toasty.Toasty;

public class GetProductImageTask extends AsyncTask<Void, Void, String> {

    private Product product;
    private ImageView image;
    private Context mContext;

    public GetProductImageTask(Product product, ImageView image, Context context) {
        this.product = product;
        this.image = image;
        this.mContext = context;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        String url = getImage();
        Log.d("GetProductImageTask", url);
        return url;
    }


    @Override
    protected void onPostExecute(String url) {
        super.onPostExecute(url);

  /**      Glide
                .with(mContext)
                .load(url)
                .centerCrop()
                .into(image); */

    }
    private String getImage() {
        Document doc = null;
        try{
            doc = Jsoup.connect("https://www.google.com/search?tbm=isch&q="+this.product.getName()+" "+this.product.getCategory()).get();
        }catch (IOException e){
            e.printStackTrace();
        }
        String image = doc.select("img").get(0).attr("data-src");
        return image;
    }


}
