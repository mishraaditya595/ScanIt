package com.vob.scanner.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
import com.vob.scanner.R;
import com.vob.scanner.adapter.TabAdapter;

import java.io.ByteArrayOutputStream;
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
}
