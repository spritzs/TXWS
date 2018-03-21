package cn.txws.board;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Administrator on 2018/1/29 0029.
 */

public class NaviActivity extends AppCompatActivity {

    Handler mHandler=new Handler();
    public static boolean isFirst=true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity);
        checkSDPermission();
        if(isFirst){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(NaviActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            },1000);
        }else{
            Intent intent = new Intent(NaviActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void checkSDPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
//            } else {
//                new AlertDialog.Builder(this)
//                        .setTitle("")
//                        .setCancelable(false)
//                        .setMessage("请授予SD卡读写权限")
//                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                finish();
//                            }
//                        })
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Intent settingIntent = new Intent(
//                                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                                        Uri.fromParts("package", getPackageName(), null)
//                                );
//                                startActivityForResult(settingIntent, 0xF);
//                            }
//                        })
//                        .create().show();
//            }
        }
    }
}
