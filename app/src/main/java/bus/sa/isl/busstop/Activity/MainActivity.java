package bus.sa.isl.busstop.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.io.Serializable;

import bus.sa.isl.busstop.Adpater.MainTabPagerAdapter;
import bus.sa.isl.busstop.Fragment.MainTab1;
import bus.sa.isl.busstop.Fragment.MainTab2;
import bus.sa.isl.busstop.Fragment.MainTab3;
import bus.sa.isl.busstop.Fragment.MainTab4;
import bus.sa.isl.busstop.R;
import bus.sa.isl.busstop.Set.Font;
import bus.sa.isl.busstop.Set.PropertyManager;

public class MainActivity extends Font {
    final int TAB_NOTICE = 0;
    final int TAB_LINE = 1;

    private TabLayout tabLayout;
    private TextView affiliation;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private MainTabPagerAdapter mainTabPagerAdapter;
    private FloatingActionButton floatingActionButton;

    private MainTab1 mTabFragment1;
    private MainTab2 mTabFragment2;
    private MainTab3 mTabFragment3;
    private MainTab4 mTabFragment4;
    private int selectTab;
    int type;
    PropertyManager propertyManager = PropertyManager.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        final String getAff = intent.getStringExtra("affiliation");


        affiliation = (TextView) findViewById(R.id.affiliation);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingBtn);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        affiliation.setText(getAff);

        type = propertyManager.getType();


        if(type==2){
            floatingActionButton.setVisibility(View.INVISIBLE);
        }

        // getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.main_bar);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                switch (selectTab) {
                    case TAB_NOTICE:
                        intent = new Intent(getApplicationContext(), NoticeWirteActivity.class);
                        startActivity(intent);
                        break;
                    case TAB_LINE:
                        intent = new Intent(getApplicationContext(), MapsActivity.class);
                        intent.putExtra("affiliation", getAff);
                        startActivity(intent);
                        break;
                }
            }
        });

        tabLayout.setupWithViewPager(viewPager);
        setTabViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectTab = tab.getPosition();
                switch (tab.getPosition()) {
                    case 0:
                        if (type == 1) {
                            floatingActionButton.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 1:
                        if (type == 1) {
                            floatingActionButton.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 2:
                        floatingActionButton.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        floatingActionButton.setVisibility(View.INVISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


    }


    private void setTabViewPager(ViewPager viewPager) {
        mainTabPagerAdapter = new MainTabPagerAdapter(getSupportFragmentManager());
        mTabFragment1 = MainTab1.newInstance();
        mTabFragment2 = MainTab2.newInstance();
        mTabFragment3 = MainTab3.newInstance();
        mTabFragment4 = MainTab4.newInstance();

        mainTabPagerAdapter.addFragment(mTabFragment1);
        mainTabPagerAdapter.addFragment(mTabFragment2);
        mainTabPagerAdapter.addFragment(mTabFragment3);
        mainTabPagerAdapter.addFragment(mTabFragment4);

        viewPager.setAdapter(mainTabPagerAdapter);

    }

}
