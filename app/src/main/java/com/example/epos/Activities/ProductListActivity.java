package com.example.epos.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epos.Adapters.ProductAdapter;
import com.example.epos.AsyncTasks.ParseJSONTask;
import com.example.epos.Models.Utility;
import com.example.epos.R;
import com.example.epos.Layout.FastScroller;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;


public class ProductListActivity extends AppCompatActivity {

    private static final String TAG = "ProductListActivity";

    private RecyclerView mRecyclerView;

    private FastScroller fastScroller;

    private ImageButton btnSearch;

    private TextView txtSearch;

    /**
     * the navigation drawer of the activity
     */
    private Drawer result;

    /**
     * the toolbar of the activity
     */
    private Toolbar toolbar;

    /**
     * Add items to the navigation menu
     */
    public void navbar_setup() {
        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Add to Order");
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("Order List");
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName("All Products");

        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.ic_app_logo)
                .build();

        //create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(accountHeader)
                .withSelectedItem(3)
                .addDrawerItems(
                        item1,
                        item2,
                        item3
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D

                        switch ((int) drawerItem.getIdentifier()) {
                            case 1:
                                result.closeDrawer();
                                Intent productList = new Intent(ProductListActivity.this, MainActivity.class);
                                startActivity(productList);
                                break;
                            case 2:
                                result.closeDrawer();
                                // Intent shoppingListIntent = new Intent(HomeActivity.this, ShoppingList.class);
                                //startActivity(shoppingListIntent);
                                break;
                            case 3:
                                result.closeDrawer();
                                break;
                        }
                        return true;
                    }
                })
                .build();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navbar_setup();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        fastScroller = (FastScroller) findViewById(R.id.fastscroller);
        btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        txtSearch = (TextView) findViewById(R.id.txtSearch);


        new ParseJSONTask(this, mRecyclerView, fastScroller, TAG, null).execute();


        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new ParseJSONTask(ProductListActivity.this, mRecyclerView, fastScroller, TAG, txtSearch.getText().toString()).execute();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ParseJSONTask(ProductListActivity.this, mRecyclerView, fastScroller, TAG, txtSearch.getText().toString()).execute();
                Utility.hideKeyboard(ProductListActivity.this);
            }
        });
    }

}
