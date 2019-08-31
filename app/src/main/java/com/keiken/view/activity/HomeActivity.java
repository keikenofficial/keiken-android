package com.keiken.view.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.keiken.R;
import com.keiken.view.IOnBackPressed;
import com.keiken.view.NotScrollableViewPager;
import com.keiken.view.ViewPagerAdapter;
import com.keiken.view.fragment.ExperiencesFragment;
import com.keiken.view.fragment.HomeFragment;
import com.keiken.view.fragment.MessagingFragment;
import com.keiken.view.fragment.ProfileFragment;
import com.keiken.view.fragment.SavedFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private NotScrollableViewPager viewPager;
    private MenuItem prevMenuItem;
    private HomeFragment homeFragment;
    private ExperiencesFragment experiencesFragment;
    private ProfileFragment profileFragment;
    private SavedFragment savedFragment;
    private MessagingFragment messagingFragment;
    private ViewPagerAdapter viewPagerAdapter;
    private Animation fadeOut, fadeIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        bottomNavigationView = findViewById(R.id.navigation);
        viewPager = findViewById(R.id.viewpager);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
                fadeOut.setDuration(30);
                fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
                fadeIn.setDuration(300);

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        if (bottomNavigationView.getSelectedItemId() != R.id.navigation_home ) {
                            viewPagerAdapter.getItem(viewPager.getCurrentItem()).getView().startAnimation(fadeOut);
                            viewPager.setCurrentItem(0, false);
                            viewPagerAdapter.getItem(viewPager.getCurrentItem()).getView().startAnimation(fadeIn);
                        }
                        break;
                    case R.id.navigation_experiences:
                        if (bottomNavigationView.getSelectedItemId() != R.id.navigation_experiences ) {
                            viewPagerAdapter.getItem(viewPager.getCurrentItem()).getView().startAnimation(fadeOut);
                            viewPager.setCurrentItem(1, false);
                            viewPagerAdapter.getItem(viewPager.getCurrentItem()).getView().startAnimation(fadeIn);
                        }
                        break;
                    case R.id.navigation_profile:
                        if (bottomNavigationView.getSelectedItemId() != R.id.navigation_profile ) {
                            viewPagerAdapter.getItem(viewPager.getCurrentItem()).getView().startAnimation(fadeOut);
                            viewPager.setCurrentItem(2, false);
                            viewPagerAdapter.getItem(viewPager.getCurrentItem()).getView().startAnimation(fadeIn);
                        }
                        break;
                    case R.id.navigation_favourites:
                        if (bottomNavigationView.getSelectedItemId() != R.id.navigation_favourites ) {
                            viewPagerAdapter.getItem(viewPager.getCurrentItem()).getView().startAnimation(fadeOut);
                            viewPager.setCurrentItem(3, false);
                            viewPagerAdapter.getItem(viewPager.getCurrentItem()).getView().startAnimation(fadeIn);
                        }
                        break;
                    case R.id.navigation_messaging:
                        if (bottomNavigationView.getSelectedItemId() != R.id.navigation_messaging ) {
                            viewPagerAdapter.getItem(viewPager.getCurrentItem()).getView().startAnimation(fadeOut);
                            viewPager.setCurrentItem(4, false);
                            viewPagerAdapter.getItem(viewPager.getCurrentItem()).getView().startAnimation(fadeIn);
                        }
                        break;
                }
                return false;
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }


            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        setupViewPager(viewPager);


    }

    private void setupViewPager(final ViewPager viewPager) {

        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {


                // add code which you want to run in background thread
                viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                viewPager.setOffscreenPageLimit(5);
                homeFragment = new HomeFragment();
                viewPagerAdapter.addFragment(homeFragment);


                experiencesFragment = new ExperiencesFragment();
                profileFragment = new ProfileFragment();
                savedFragment = new SavedFragment();
                messagingFragment = new MessagingFragment();
                viewPagerAdapter.addFragment(experiencesFragment);
                viewPagerAdapter.addFragment(profileFragment);
                viewPagerAdapter.addFragment(savedFragment);
                viewPagerAdapter.addFragment(messagingFragment);


                viewPager.setAdapter(viewPagerAdapter);


                // viewPagerAdapter.notifyDataSetChanged();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // add code which you want to run in main(UI) thread
                    }
                });
            }
        });

    }


    @Override
    public void onBackPressed() {

        Fragment fragment = viewPagerAdapter.getItem(viewPager.getCurrentItem());

        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed()) {
            if (!(fragment instanceof  HomeFragment)) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_home);
            } else super.onBackPressed();
        }
    }
}







