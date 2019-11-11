package com.example.epos.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import es.dmoral.toasty.Toasty;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextPaint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.epos.Adapters.OrderListAdapter;
import com.example.epos.Models.Category;
import com.example.epos.Models.Product;
import com.example.epos.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.StructuredQuery;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {
    private static final String PRODUCTS = "Products";


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /** document reference to the currently logged in user*/
    private CollectionReference productRef;

    private List<Product> products;

    private List<Category> categories;

    private List<String> groups;



    /**
     * the navigation drawer of the activity
     */
    private Drawer result;

    private SwipeRefreshLayout swipe;

    private RecyclerView orderList;
    /**
     * the toolbar of the activity
     */
    private Toolbar toolbar;

    private OrderListAdapter adapter;
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
                                Intent mainActivity = new Intent(OrderListActivity.this, MainActivity.class);
                                startActivity(mainActivity);
                                break;
                            case 2:
                                result.closeDrawer();
                                break;
                            case 3:
                                result.closeDrawer();
                                Intent productList = new Intent(OrderListActivity.this, ProductListActivity.class);
                                startActivity(productList);
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
        setContentView(R.layout.activity_order_list);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navbar_setup();

        orderList = findViewById(R.id.recycler_view);
        swipe = findViewById(R.id.swipe);

        productRef = FirebaseFirestore.getInstance().collection("Products");
        products = new ArrayList<>();
        groups = new ArrayList<>();
        categories = new ArrayList<>();

        loadUsersIngredients();

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe.setRefreshing(false);
                    }
                }, 4000);

                refresh();

            }
        });


        orderList.setLayoutManager(new LinearLayoutManager(this));

    }


    /**
     * Loads all the ingredients the user has
     */
    public void loadUsersIngredients() {
        productRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot documentSnapshots = task.getResult();
                    if (documentSnapshots!=null) {
                        if (documentSnapshots.isEmpty()) {
                            Toasty.info(getApplicationContext(), "No items to order #32", Toasty.LENGTH_LONG, true).show();

                        } else {
                            List<Product> items = documentSnapshots.toObjects(Product.class);;
                            populateGroups(items);
                        }
                    } else {
                        Toasty.info(getApplicationContext(), "No items to order #21", Toasty.LENGTH_LONG, true).show();
                    }
                } else {
                    Toasty.error(getApplicationContext(), "Error getting order list!!!", Toasty.LENGTH_LONG, true).show();
                }

            }
        });
    }

    public void populateGroups(List<Product> items) {
        products = new ArrayList<>();
        groups = new ArrayList<>();
        categories = new ArrayList<>();

        for (Product product: items) {
            if (!groups.contains(product.getGroup())) {
                groups.add(product.getGroup());
            }
        }

        for (String group: groups) {
            for (Product product: items) {
                if (group.equals(product.getGroup())) {
                    products.add(product);
                }
            }
            categories.add(new Category(group, products));
            products = new ArrayList<>();
        }


        adapter = new OrderListAdapter(categories);
        orderList.setAdapter(adapter);
    }

    public void refresh() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.print) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            // Check if we have write permission
                            int permission = ActivityCompat.checkSelfPermission(OrderListActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                            if (permission != PackageManager.PERMISSION_GRANTED) {
                                // We don't have permission so prompt the user
                                ActivityCompat.requestPermissions(
                                        OrderListActivity.this,
                                        PERMISSIONS_STORAGE,
                                        REQUEST_EXTERNAL_STORAGE
                                );
                            } else {
                                createPdf();
                            }
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(OrderListActivity.this);
            builder.setMessage("Would you like to generate a PDF file?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

            return true;
        }

        if (id == R.id.delete_all) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            CollectionReference mProductsRef = FirebaseFirestore.getInstance().collection(PRODUCTS);
                            for (final Category category: categories) {
                                for (final Product product: category.getItems()) {
                                    mProductsRef.document(product.getName().replace("/", ""))
                                            .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            category.getItems().remove(product);
                                            if (category.getItemCount()==0) {
                                                categories.remove(category);
                                            }

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toasty.error(OrderListActivity.this, "Check Your Internet Connection", Toasty.LENGTH_LONG, true).show();

                                        }
                                    });
                                }
                            }
                            refresh();

                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(OrderListActivity.this);
            builder.setMessage("Would you like to delete the whole list?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }

        return super.onOptionsItemSelected(item);
    }


    private void createPdf(){
        // create a new document
        PdfDocument document = new PdfDocument();
        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(210, 297, 1).create();
        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        TextPaint header = new TextPaint();
        header.setAntiAlias(true);
        header.setTextSize(6);
        header.setFlags(Paint.FAKE_BOLD_TEXT_FLAG);
        header.setColor(Color.BLACK);

        TextPaint body = new TextPaint();
        body.setAntiAlias(true);
        body.setTextSize(4);
        body.setColor(Color.BLACK);

        TextPaint additional = new TextPaint();
        additional.setAntiAlias(true);
        additional.setTextSize(3);
        additional.setColor(getResources().getColor(R.color.colorDarkerGrey));


        int x = 20; int y = 20;
        for (Category category: categories) {
            canvas.drawText(category.getTitle(), x, y, header);
            int i = y+8;
            if (i>270) {
                if (x==110) {
                    document.finishPage(page);
                    pageInfo = new PdfDocument.PageInfo.Builder(210, 297, 2).create();
                    page = document.startPage(pageInfo);
                    canvas = page.getCanvas();
                    x = 20;
                    i = 20;
                } else {
                    x = 110;
                    i = 20;
                }

            }
            for (Product product: category.getItems()) {
                String row = product.getName() + "   " + product.getPrice();

                String[] arr = splitStringEvery(row, 40);
                for (String str: arr) {
                    canvas.drawText(str, x, i, body);
                    i=i+5;
                    if (i>270) {
                        if (x==110) {
                            document.finishPage(page);
                            pageInfo = new PdfDocument.PageInfo.Builder(210, 297, 2).create();
                            page = document.startPage(pageInfo);
                            canvas = page.getCanvas();
                            x = 20;
                            i = 20;
                        } else {
                            x = 110;
                            i = 20;
                        }

                    }
                }

                if (!product.getAdditional().equals("")) {
                   // i = i + 4;
                    String[] arr2 = splitStringEvery(product.getAdditional(), 50);
                    for (String str: arr2) {
                        canvas.drawText(product.getAdditional(), x, i, additional);
                        i = i + 5;
                        if (i>270) {
                            if (x==110) {
                                document.finishPage(page);
                                pageInfo = new PdfDocument.PageInfo.Builder(210, 297, 2).create();
                                page = document.startPage(pageInfo);
                                canvas = page.getCanvas();
                                x = 20;
                                i = 20;
                            } else {
                                x = 110;
                                i = 20;
                            }

                        }
                    }
                }

            }
            y = i + 10;
            if (y>270) {
                if (x==110) {
                    document.finishPage(page);
                    pageInfo = new PdfDocument.PageInfo.Builder(210, 297, 2).create();
                    page = document.startPage(pageInfo);
                    canvas = page.getCanvas();
                    x = 20;
                    y = 20;
                } else {
                    x = 110;
                    y = 20;
                }
            }
        }
        document.finishPage(page);

        // write the document content
        String directory_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        File file = new File(directory_path,"/report.pdf");
        try {
            document.writeTo(new FileOutputStream(file));
            Toasty.info(this, "Check your downloads folder", Toasty.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("main", "error "+e.toString());
            Toasty.error(this, "Something wrong: " + e.toString(), Toasty.LENGTH_LONG).show();
        }
        // close the document
        document.close();
    }

    public String[] splitStringEvery(String s, int interval) {
        int arrayLength = (int) Math.ceil(((s.length() / (double)interval)));
        String[] result = new String[arrayLength];

        int j = 0;
        int lastIndex = result.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            result[i] = s.substring(j, j + interval);
            j += interval;
        } //Add the last bit
        result[lastIndex] = s.substring(j);

        return result;
    }
}
