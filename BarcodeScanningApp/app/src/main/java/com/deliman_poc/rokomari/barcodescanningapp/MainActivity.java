package com.deliman_poc.rokomari.barcodescanningapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission_group.CAMERA;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private static final int REQUEST_CAMERA=1;
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView=new ZXingScannerView(this);

        setContentView(scannerView);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                Toast.makeText(MainActivity.this,"Permission is granted!",Toast.LENGTH_LONG).show();
            }
            else
            {

                buildDialog(MainActivity.this).show();

               // requestPermission();
            }
        }
    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED);
    }


    private void requestPermission()
    {
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{CAMERA},REQUEST_CAMERA);
    }



    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case REQUEST_CAMERA:
                if(grantResults.length>0)
                {
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted)
                    {
                        Toast.makeText(MainActivity.this,"Permission Granted",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();

                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                        {
                            if(shouldShowRequestPermissionRationale(CAMERA))
                            {

                                displayAlertMessage("You need to allow access for both permissions",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                                                {
                                                    requestPermissions(new String[]{CAMERA},REQUEST_CAMERA);
                                                }
                                            }
                                        });

                                return;
                            }
                        }
                    }
                }

                break;
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                scannerView=new ZXingScannerView(this);
                setContentView(scannerView);
            }

            scannerView.setResultHandler(this);
            scannerView.startCamera();
        }
        else
        {
            requestPermission();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {


        final String scanResult=result.getText();

        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        builder.setTitle("Scan Result");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //scannerView.resumeCameraPreview(MainActivity.this);

                Intent intent=new Intent(MainActivity.this,ScanningResultActivity.class);
                intent.putExtra("scanned_text",scanResult);
                startActivity(intent);

            }
        });



        builder.setMessage(scanResult);
        AlertDialog alertDialog=builder.create();
        alertDialog.show();


    }


    public void displayAlertMessage(String merssage,DialogInterface.OnClickListener listener)
    {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(merssage)
                .setPositiveButton("OK",listener)
                .setNegativeButton("Cancel",null)
                .create()
                .show();
    }




    public android.app.AlertDialog.Builder buildDialog(Context c)
    {
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(c);
        builder.setTitle("Permission Required");
        builder.setMessage("You need to allow camera permission.Press ok to exit");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);

            }
        });

        return builder;
    }

}
