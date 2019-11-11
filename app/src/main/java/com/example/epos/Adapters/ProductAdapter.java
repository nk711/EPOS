/**
 * ImageListAdapter.java
 */
package com.example.epos.Adapters;

import android.app.Activity;
import android.content.Context;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.epos.Activities.MainActivity;
import com.example.epos.Dialog.InfoDialog;
import com.example.epos.Models.Product;
import com.example.epos.R;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

/**
 *  @author Nithesh Koneswaran
 *  List adapter for ingredients when adding a new recipe
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    public List<Product> products;
    public Context context;
    public String tag;


    public ProductAdapter(List<Product> products, String tag) {
            this.products = products;
            this.tag = tag;
    }


    /**
     * Inflates the row of the recycler view
     * @param parent
     * @param viewType
     * @returns the viewholder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_row_layout, parent, false);
        this.context = parent.getContext();
        return new ViewHolder(view);
    }

    /**
     * Binds data to the ViewHolder,
     * goes through each position and will add the filename and image to the row
     * @param holder
     * @param position
     *              the position of the row
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product current_product =  products.get(holder.getAdapterPosition());
        holder.product_name.setText(current_product.getName());
        holder.product_desc.setText(current_product.getPrice());
        holder.product_category.setText(current_product.getCategory());
       /**
        if (tag=="RecipeOverviewFragment") {
            holder.btnDelete.setVisibility(View.GONE);
        } */

    }


    /**
     *
     * @returns Size of the list
     */
    @Override
    public int getItemCount() {
        return this.products.size();
    }

    /**
     * @param position
     *          Deletes the specified position of the item in the recycler view
     */
    public void delete(int position) {
        try {
            this.products.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, this.products.size());
        } catch (ArrayIndexOutOfBoundsException e) {

        }
    }


    public void clear() {
        try {
            this.products = new ArrayList<>();
            notifyItemRangeChanged(0, this.products.size());
        } catch (ArrayIndexOutOfBoundsException e) {

        }
    }

    public Product getItem(int position ) {
        if (products.size()==0) {
            return null;
        }
        return this.products.get(position);
    }



    /**
     * Defines the attributes and behaviour of a view holder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * Holds the view
         */
        View mView;

        /** The components of the view         */
        public TextView product_name;
        public TextView product_desc;
        public TextView product_category;
        public ImageView btnDelete;
        public LinearLayout row;

        public ViewHolder(View itemView) {
            super(itemView);
            mView= itemView;
            /** Initialising the components of the view */
             row = (LinearLayout) mView.findViewById(R.id.row);
             product_name = (TextView) mView.findViewById(R.id.txtName);
             product_desc = (TextView) mView.findViewById(R.id.txtPrice);
             product_category = (TextView) mView.findViewById(R.id.txtCategory);

             row.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     try {
                         Product product = products.get(getAdapterPosition());
                         Bundle args = new Bundle();
                         args.putSerializable("Product", product);

                         InfoDialog dialog = new InfoDialog();
                         dialog.setArguments(args);
                         dialog.show( ((AppCompatActivity)context).getSupportFragmentManager(), "InfoDialog");

                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                 }
             });
        }

        public void getActivity() {


        }
    }
}
