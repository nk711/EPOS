package com.example.epos.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epos.Activities.MainActivity;
import com.example.epos.AsyncTasks.GetProductImageTask;
import com.example.epos.Models.Product;
import com.example.epos.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.Distribution;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import es.dmoral.toasty.Toasty;

public class InfoDialog extends DialogFragment implements  AdapterView.OnItemSelectedListener {

    private static final String TAG = "infoDialog";
    private static final String PRODUCTS = "Products";
    private Product product;
    /** gets a reference of the firebase storage */
    private CollectionReference mProductsRef;



    private Activity mActivity;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_info, null);

        mProductsRef = FirebaseFirestore.getInstance().collection(PRODUCTS);

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
        final Spinner txtGroup = (Spinner) view.findViewById(R.id.txtGroup);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mActivity, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        txtGroup.setAdapter(adapter);


        txtGroup.setOnItemSelectedListener(this);

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


        builder.setPositiveButton("Add to list", null);
        builder.setNegativeButton("Cancel",null);

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                         @Override
                                         public void onDismiss(DialogInterface dialog) {
                                            exit();
                                         }
                                     });

        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positive = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                Button negative = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);

                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                                if(txtName.getText().toString().equals("")) {
                                    Toasty.error(mActivity, "Add a name", Toast.LENGTH_SHORT, true).show();
                                } else {
                                    Product new_product = new Product();
                                    if (product!=null) new_product.setBarcode(product.getBarcode());
                                    new_product.setName(txtName.getText().toString());
                                    new_product.setCategory(txtCategory.getText().toString());
                                    new_product.setPrice(txtPrice.getText().toString());
                                    new_product.setAdditional(txtAdditional.getText().toString());
                                    if (txtGroup.getSelectedItemPosition()==0) {
                                        new_product.setGroup("Other");
                                    } else {
                                        new_product.setGroup(txtGroup.getSelectedItem().toString());
                                    }
                                    mProductsRef.document(new_product.getName().replace("/","")).set(new_product).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toasty.success(mActivity, "Added to list Successfully", Toast.LENGTH_LONG, true).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toasty.error(mActivity, "Check Your Internet Connection", Toast.LENGTH_SHORT, true).show();
                                        }
                                    });
                                    dialog.dismiss();
                                    exit();
                                }

                    }
                });

                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        exit();
                    }
                });
            }
        });
        return dialog;
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

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Log.d("ABSDIALOGFRAG", "Exception", e);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            mActivity =(Activity) context;
        }
    }

    public void exit() {
        if (mActivity.getClass().getSimpleName().equals("MainActivity")) {
            Intent intent = new Intent(mActivity, MainActivity.class);
            mActivity.overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            mActivity.startActivity(intent);
        }
    }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String item = parent.getItemAtPosition(position).toString();

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

    }
}
