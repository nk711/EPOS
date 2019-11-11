package com.example.epos.Adapters;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.epos.Models.Category;
import com.example.epos.Models.Product;
import com.example.epos.R;
import com.example.epos.ViewHolders.CategoryViewHolder;
import com.example.epos.ViewHolders.ItemViewHolder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import es.dmoral.toasty.Toasty;

public class OrderListAdapter extends ExpandableRecyclerViewAdapter<CategoryViewHolder, ItemViewHolder> {
    private static final String PRODUCTS = "Products";

    public OrderListAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public CategoryViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_group_layout, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public ItemViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(final ItemViewHolder holder, int flatPosition, final ExpandableGroup group, int childIndex) {
        final Product product = (Product) group.getItems().get(childIndex);
        holder.onBind(product);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Toasty.info(holder.itemView.getContext(), product.getName()+" removed", Toasty.LENGTH_LONG, true).show();

                                CollectionReference mProductsRef = FirebaseFirestore.getInstance().collection(PRODUCTS);

                                mProductsRef.document(product.getName())
                                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        group.getItems().remove(product);
                                        notifyDataSetChanged();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toasty.error(holder.itemView.getContext(), "Check Your Internet Connection", Toasty.LENGTH_LONG, true).show();

                                    }
                                });

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });
    }


    @Override
    public void onBindGroupViewHolder(CategoryViewHolder holder, int flatPosition, ExpandableGroup group) {
        final Category category = (Category) group;
        holder.onBind(category);
    }

}
