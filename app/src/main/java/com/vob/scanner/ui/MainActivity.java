package com.vob.scanner.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;
import com.vob.scanner.R;
import com.vob.scanner.adapter.TabAdapter;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseFields();
        setupToolbar();
        setupTabLayout();
    }

    private void setupTabLayout() {
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
    }

    private void setupToolbar() {
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.TextAppearance_AppCompat_Title);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
    }

    private void initialiseFields() {
        tabLayout = findViewById(R.id.main_tablayout);
        viewPager = findViewById(R.id.viewPager);
        toolbar = findViewById(R.id.main_toolbar);
    }
}
