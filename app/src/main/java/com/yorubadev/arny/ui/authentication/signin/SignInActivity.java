package com.yorubadev.arny.ui.authentication.signin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;
import com.yorubadev.arny.BuildConfig;
import com.yorubadev.arny.R;
import com.yorubadev.arny.data.database.UserEntry;
import com.yorubadev.arny.ui.authentication.phoneverification.PhoneVerification;
import com.yorubadev.arny.utilities.ActivityLauncher;
import com.yorubadev.arny.utilities.ImageUtils;
import com.yorubadev.arny.utilities.InjectorUtils;
import com.yorubadev.arny.utilities.InputUtils;
import com.yorubadev.arny.utilities.PreferenceUtils;
import com.yorubadev.arny.utilities.WorkUtils;

import java.util.Arrays;
import java.util.List;


public class SignInActivity extends AppCompatActivity {

    private View mRoot;
    private SignInActivityViewModel mViewModel;
    private InputUtils inputUtils;
    static final int VERIFY_PHONE_REQUEST_CODE = 100;
    private final List<String> allowedNumbers = Arrays.asList("+2346107441591", "+2346107441592",
            "+2346107441593", "+2346107441594", "+2346107441595", "+2346107441596",
            "+2346107441597", "+2346107441598", "+2346107441599");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_phone);

        ProgressBar pbWorking = findViewById(R.id.pb_working);
        mRoot = findViewById(R.id.root);

        TextInputEditText etPhoneNumber = findViewById(R.id.et_phone_number);
        CountryCodePicker ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(etPhoneNumber);


        Button btnContinue = findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(button -> {
            if (!ccp.isValidFullNumber()) {
                if (BuildConfig.DEBUG) {
                    String phoneNumber = ccp.getFullNumberWithPlus();
                    if (!allowedNumbers.contains(phoneNumber))
                        etPhoneNumber.setError(getString(R.string.error_invalid_phone_number));
                    else {
                        inputUtils.disableInput();
                        startVerification(phoneNumber);
                    }
                } else
                    etPhoneNumber.setError(getString(R.string.error_invalid_phone_number));
            } else {
                String phoneNumber = ccp.getFullNumberWithPlus();
                inputUtils.disableInput();
                startVerification(phoneNumber);
            }
        });

        SignInActivityViewModelFactory factory = InjectorUtils.provideSignInActivityViewModelFactory(this);
        mViewModel = ViewModelProviders.of(this, factory).get(SignInActivityViewModel.class);

        /*TextView tvSignUp = findViewById(R.id.tv_sign_up);
        tvSignUp.setOnClickListener(view -> {
            ActivityLauncher.launchSignUpActivity(this);
        });*/

        inputUtils = InputUtils.init(pbWorking, ccp, etPhoneNumber, btnContinue);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VERIFY_PHONE_REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED)
                Snackbar.make(mRoot, getString(R.string.error_verify_phone_number), Snackbar.LENGTH_LONG).show();
            else if (resultCode == RESULT_OK) {
                CountryCodePicker ccp = findViewById(R.id.ccp);
                String phoneNumber = ccp.getFullNumberWithPlus();

                // rare instance of accessing Firebase from an Activity. If you find a better way
                // to do it, please implement. Done this way because WorkUtils.java has no
                // access to the Repository.
                String uid = mViewModel.getFirebaseUid();

                UserEntry userEntry = new UserEntry(uid == null ? "" : uid, "", phoneNumber, 5.0);
                WorkUtils.scheduleUserDetailsUpdateWork(this, userEntry);
                mViewModel.insertNewUserInDatabase(userEntry);
                ImageUtils.fetchUserProfilePic(this);
                WorkUtils.scheduleMessagingTokenUpdateWork(this);
                ActivityLauncher.launchResumeActivity(this);
                finish();
            }
            inputUtils.enableInput();
        }
    }

    private void startVerification(String phoneNumber) {
        Intent intent = new Intent(this, PhoneVerification.class);
        intent.putExtra(PhoneVerification.EXTRA_PHONE_NUMBER, phoneNumber);
        startActivityForResult(intent, VERIFY_PHONE_REQUEST_CODE);
    }
}