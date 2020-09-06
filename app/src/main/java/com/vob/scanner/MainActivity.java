package com.vob.scanner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.gu.toolargetool.TooLargeTool;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
import com.vob.scanner.adapter.TabAdapter;
import com.vob.scanner.constants.Constants;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.vob.scanner.constants.Constants.OPEN_THING;

public class MainActivity extends AppCompatActivity {

    ImageView scannedImageView;
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.main_tablayout);
        viewPager = findViewById(R.id.viewPager);
        tabLayout.addTab(tabLayout.newTab().setText("Scan Documents"));
        tabLayout.addTab(tabLayout.newTab().setText("Copy Text"));
        tabLayout.addTab(tabLayout.newTab().setText("Scan QR"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager(),this,tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        /*scannedImageView = findViewById(R.id.scannedImageView);
        findViewById(R.id.open_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFiles();
            }
        });*/
    }

    public void openFiles() {
        int preference = ScanConstants.OPEN_MEDIA;
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, OPEN_THING);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == OPEN_THING) {
                Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    getContentResolver().delete(uri, null, null);

                    Bitmap compressedBitmap = compressBitmap(bitmap);

                    String image = bitmapToString(bitmap);

                    Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
                    intent.putExtra("image", image);
                    startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*String[] filepath = {MediaStore.Images.Media.DATA};
                @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(uri, filepath, null, null);

                cursor.moveToFirst();
                int coloumnIndex = cursor.getColumnIndex(filepath[0]);
                String myPath = cursor.getString(coloumnIndex);
                cursor.close();

                 */
            }
        }
    }

    public Bitmap compressBitmap(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40 , byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        return compressedBitmap;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }
}
