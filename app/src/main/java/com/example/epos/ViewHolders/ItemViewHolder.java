package com.example.epos.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.epos.Models.Product;
import com.example.epos.R;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import java.util.List;

import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import es.dmoral.toasty.Toasty;

public class ItemViewHolder extends ChildViewHolder {
    private TextView itemName;
    private TextView itemPrice;
    private TextView itemAdditional;
    private View bar;
    private RelativeLayout layout;
    public ImageView delete;

    public ItemViewHolder(View itemView) {
        super(itemView);
        itemName = itemView.findViewById(R.id.txt_item);
        itemPrice = itemView.findViewById(R.id.txt_price);
        itemAdditional = itemView.findViewById(R.id.txt_additional);
        bar = itemView.findViewById(R.id.bar);
        layout = itemView.findViewById(R.id.layout);
        delete = itemView.findViewById(R.id.delete);
    }


    public void onBind(Product product) {
        itemName.setText(product.getName());
        itemPrice.setText(product.getPrice());
        itemAdditional.setText(product.getAdditional());

        if (product.getAdditional().equals("")||product.getAdditional()==null) {
            itemAdditional.setVisibility(View.GONE);
        }


        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bar.getAlpha()==1.0f) {
                    bar.animate().alpha(0.0f);
                } else {
                    bar.animate().alpha(1.0f);
                }
            }
        });
    }


}
