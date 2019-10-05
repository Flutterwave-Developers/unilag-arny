package com.yorubadev.arny.ui.main;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.navigation.NavigationView;
import com.yorubadev.arny.R;
import com.yorubadev.arny.utilities.InjectorUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Fragments
    private int fragmentContainerId = R.id.fl_main;
    private FragmentManager fm = getSupportFragmentManager();
    private int foregroundFragment = -1;

    private DrawerLayout mDrawerLayout;
    private MainActivityViewModel mViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.dl_root);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        CircleImageView imgProfilePic = findViewById(R.id.img_profile_pic);
//        imgProfilePic.setOnClickListener(view -> launchEditProfileActivity());

        MainActivityViewModelFactory factory = InjectorUtils.provideMainActivityViewModelFactory(getApplication(), this);
        mViewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel.class);

        setupActionBar();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() != R.id.nav_settings) item.setChecked(true);
        mDrawerLayout.closeDrawers();
        /*new Handler().postDelayed(() -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    addVisitorsFragment();
                    break;
                case R.id.nav_notification:
                    addNotificationFragment();
                    break;
                case R.id.nav_feedback:
                    launchFeedback();
                    break;
                case R.id.nav_settings:
                    launchSettingsActivity();
                    break;
                case R.id.nav_sign_out:
                    signUserOut();
                    break;
            }
        }, 400);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar() {
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        setToolbarTitle(getString(R.string.app_name));
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu_icon);
        }
    }

    private void setToolbarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(title);
    }
}
