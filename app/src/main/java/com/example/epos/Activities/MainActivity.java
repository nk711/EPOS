package com.example.epos.Activities;
/**
 * Application created to quickly add an item to an ordering list using the item's barcode. This will make it easier for the business to order items that they need
 *
 */
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.epos.AsyncTasks.GetInfoTask;
import com.example.epos.Dialog.InfoDialog;
import com.example.epos.R;
import com.google.firebase.FirebaseApp;
import com.google.zxing.Result;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import es.dmoral.toasty.Toasty;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private static final String TAG = "MainActivity";

    private static final String REGEX = "[0-9]+";
    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;


    /**
     * the navigation drawer of the activity
     */
    private Drawer result;

    /**
     * the toolbar of the activity
     */
    private Toolbar toolbar;

    private Button btnBarcode;
    private Button btnScan;
    private Button btnAdd;

    private EditText txtBarcode;

    private ImageView example;


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

                                break;
                            case 2:
                                result.closeDrawer();
                                Intent orderList = new Intent(MainActivity.this, OrderListActivity.class);
                                startActivity(orderList);
                                break;
                            case 3:
                                result.closeDrawer();
                                Intent productList = new Intent(MainActivity.this, ProductListActivity.class);
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

        build();

    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(MainActivity.this, CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{CAMERA}, REQUEST_CAMERA);
    }


    public void onRequestPermiossionsResult(int requestCode, String permission[], int grantResults[]) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length>0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {
                        Toasty.success(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG, true).show();
                    } else {
                        Toasty.error(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG, true).show();
                        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                displayAlertMessage("You need to allow access for both permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(new String[]{CAMERA} ,REQUEST_CAMERA);
                                        }
                                    });
                            }
                        }
                    }
                }
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if (scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this); //
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();

    }

    @Override
    public void handleResult(Result result) {
        String scanResult = result.getText();
       // AlertDialog.Builder builder = new AlertDialog.Builder(this);
        scannerView.stopCamera();
        new GetInfoTask(MainActivity.this, scanResult, TAG).execute();
    }


    @Override
    public void onBackPressed() {
        if (scannerView.isEnabled()) {
            scannerView.stopCamera();
            this.refresh();
        } else {
            super.onBackPressed();
        }
    }

    public void build() {
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navbar_setup();

        btnBarcode = (Button) findViewById(R.id.btnAdd);
        btnScan = (Button) findViewById(R.id.btnScan);
        btnAdd = (Button) findViewById(R.id.btnAddProduct);
        txtBarcode = (EditText) findViewById(R.id.txtBarcode);
        example = (ImageView) findViewById(R.id.example);
        scannerView = new ZXingScannerView(MainActivity.this);
        scannerView.setEnabled(false);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //args.putString("Image", this.url);
                InfoDialog dialog = new InfoDialog();
                dialog.show(getSupportFragmentManager(), "InfoDialog");
            }
        });

        btnBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String barcode= txtBarcode.getText().toString();
                if (barcode.isEmpty() || !barcode.matches(REGEX)) {
                    Toasty.error(MainActivity.this, "Invalid Barcode, use the example above to help you", Toast.LENGTH_LONG, true).show();
                } else {
                    new GetInfoTask(MainActivity.this, barcode, TAG).execute();
                }
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkPermission()) {
                        Toasty.info(MainActivity.this, "Scan a barcode", Toast.LENGTH_LONG, true).show();
                    } else {
                        // Toasty.info(MainActivity.this, "2", Toast.LENGTH_LONG, true).show();
                        requestPermission();
                    }
                }
                scannerView.setEnabled(true);
                setContentView(scannerView);
            }
        });
    }

    public void refresh() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

}
