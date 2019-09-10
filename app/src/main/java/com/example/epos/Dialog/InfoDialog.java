package com.example.epos.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epos.AsyncTasks.GetProductImageTask;
import com.example.epos.Models.Product;
import com.example.epos.R;
import com.google.api.Distribution;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import java.io.IOException;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import es.dmoral.toasty.Toasty;

public class InfoDialog extends DialogFragment {

    private static final String TAG = "infoDialog";
    private Product product;



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_info, null);

        Bundle mArgs = getArguments();

        if (mArgs != null) {
            this.product = (Product) Objects.requireNonNull(mArgs).getSerializable("Product");
            if (product==null) Toasty.error(getContext(), "Product Not Found", Toasty.LENGTH_LONG, true).show();

        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        final EditText txtName = (EditText) view.findViewById(R.id.txtName);
        final EditText txtPrice = (EditText) view.findViewById(R.id.txtPrice);
        final EditText txtCategory = (EditText) view.findViewById(R.id.txtCategory);
        final EditText txtAdditional = (EditText) view.findViewById(R.id.txtAdditional);

       // final ImageView imgProduct = (ImageView) view.findViewById(R.id.imgProduct);
        final LinearLayout button_layout = (LinearLayout) view.findViewById(R.id.edit_layout);
        Switch edit_switch = (Switch) view.findViewById(R.id.edit_switch);


        if (product != null) {
            txtName.setText(product.getName());
            txtPrice.setText(product.getPrice());
            txtCategory.setText(product.getCategory());
        } else {
            txtName.setHint("Add Product Name");
            txtPrice.setHint("Add Product Price");
            txtCategory.setHint("Add Product Category");
            txtName.setEnabled(true);
            txtPrice.setEnabled(true);
            txtCategory.setEnabled(true);
            txtAdditional.setEnabled(true);

            edit_switch.setVisibility(View.GONE);
        }

        edit_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    txtName.setEnabled(true);
                    txtPrice.setEnabled(true);
                    txtCategory.setEnabled(true);
                    txtAdditional.setEnabled(true);
                } else {
                    txtName.setEnabled(false);
                    txtPrice.setEnabled(false);
                    txtCategory.setEnabled(false);
                    txtAdditional.setEnabled(false);

                }
            }
        });



        builder.setPositiveButton("Add to list", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toasty.success(getContext(), "Added to list", Toast.LENGTH_LONG, true).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        return builder.create();
    }

    private String getImage() {
        Document doc = null;
        try{
            doc = Jsoup.connect("https://www.google.com/search?tbm=isch&q="+this.product).get();
        }catch (IOException e){
            e.printStackTrace();
        }
        String image = doc.select("img").get(0).attr("data-src");
        return image;
    }




}
