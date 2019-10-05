package com.yorubadev.arny.ui;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yorubadev.arny.R;
import com.yorubadev.arny.utilities.ActivityLauncher;


public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }

    private boolean areNecessaryPermissionsGranted() {
        boolean result = true;
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_DENIED) result = false;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) result = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
                    == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED)
                result = false;
        }*/

        return result;
    }

    public boolean checkPlayServices() {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int resultCode = api.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (resultCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
                api.makeGooglePlayServicesAvailable(this);
                api.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else if (api.isUserResolvableError(resultCode))
                api.getErrorDialog((this), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            else {
                Toast.makeText(getApplicationContext(), R.string.error_google_play_services, Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void resolvePermissions() {
        if (checkPlayServices()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                ActivityLauncher.launchResumeActivity(this);
                finish();
                /*if (areNecessaryPermissionsGranted()) {
                    //initialize the blocker class
                    //for phones below Android 6
                    ActivityLauncher.launchResumeActivity(this);
                    finish();
                } else {
                    ActivityLauncher.launchPermissionsCheck(this);
                    finish();
                }*/
            } else {
                new Handler().postDelayed(() -> {
                    ActivityLauncher.launchSignInActivity(this);
                    finish();
                }, 3000);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resolvePermissions();
    }
}
