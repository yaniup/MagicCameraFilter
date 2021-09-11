package com.jx.livestream;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.jx.livestream.databinding.SplashPageBinding;

public class SplashPage extends AppCompatActivity {
    private final int REQUEST_CODE_READ_WRITE_LOCATION_CAMERA = 10;
    private AlertDialog alertDialog;
    private String userPhoneString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SplashPageBinding splashPageBinding = SplashPageBinding.inflate(getLayoutInflater());
        setContentView(splashPageBinding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("user_info",MODE_PRIVATE);
        userPhoneString = sharedPreferences.getString("user_phone","");
        Log.e("user phone",userPhoneString+"ff");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("鲸醒云播需要权限来正常运行！").setPositiveButton("明白",  new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ActivityCompat.requestPermissions(SplashPage.this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA}, REQUEST_CODE_READ_WRITE_LOCATION_CAMERA);
            }
        }).setNegativeButton("暂时不", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).setCancelable(false);
        alertDialog = builder.create();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            &&ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            alertDialog.show();
            return;
        }

        startPage();
    }

    private void startPage(){
        Log.e("user phone",userPhoneString+"dd");
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!userPhoneString.equals("")&&userPhoneString.length() == 11)
                {
                    Intent jumpProductPage = new Intent(SplashPage.this,ProductShowPage.class);
                    startActivity(jumpProductPage);
                    finish();
                }
                else
                {
                    Intent jumpLoginPage = new Intent(SplashPage.this,LoginPage.class);
                    startActivity(jumpLoginPage);
                    finish();
                }
            }
        },1000);
    }

    public void onStop() {
        super.onStop();
        alertDialog.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("result","true");

        if (grantResults.length == 0) {
            return;
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
                && requestCode == REQUEST_CODE_READ_WRITE_LOCATION_CAMERA) {
            startPage();
        } else {
            finish();
        }
    }
}
