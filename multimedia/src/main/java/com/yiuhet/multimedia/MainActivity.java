package com.yiuhet.multimedia;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.yiuhet.multimedia.audio.AudioActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends Activity {

    private Dialog mDialog;
    private Button mTest;
    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTest = findViewById(R.id.test);
        mImage = findViewById(R.id.iv_test);
        mTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, AudioActivity.class));
                ThreadPoolUtils.executeInCachePool(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = getImageBitmap("http://pic1.win4000.com/wallpaper/c/53cdd1f7c1f21.jpg");
                        runOnUiThread(() -> mImage.setImageBitmap(bitmap));
                    }
                });
            }
        });

    }

    public Bitmap getImageBitmap(String url) {
        URL imgUrl = null;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imgUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void showFullDialog() {
        View myLoginView = LayoutInflater.from(this).inflate(R.layout.dialog_full, null);
        mDialog = new Dialog(this, R.style.FullDialogStyleTranslucentNoTitle);
        mDialog.setContentView(myLoginView);
        mDialog.show();
    }
}
