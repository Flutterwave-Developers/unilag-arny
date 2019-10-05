package com.yorubadev.arny.ui.authentication.phoneverification;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.yorubadev.arny.R;
import com.yorubadev.arny.utilities.DateTimeUtils;
import com.yorubadev.arny.utilities.InputUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PhoneVerification extends AppCompatActivity {

    private ViewGroup mRoot;

    public static final String EXTRA_PHONE_NUMBER = "phone_number";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private FirebaseAuth mAuth;

    private boolean mVerificationInProgress = false, shouldResendCode = true;
    private String mVerificationId, mPhoneNumber;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private TextInputEditText etCodeOne, etCodeTwo, etCodeThree, etCodeFour, etCodeFive, etCodeSix;
    private TextView tvInfo, tvResendCode, tvResendCodeTimer;
    private CountDownTimer cdtResendCode;
    private Button btnVerify;

    private InputUtils inputUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);

        setupActionBar();

        mPhoneNumber = getIntent().getStringExtra(EXTRA_PHONE_NUMBER);
        mRoot = findViewById(R.id.root);
        ProgressBar pbWorking = findViewById(R.id.pb_working);
        btnVerify = findViewById(R.id.btn_verify);
        tvInfo = findViewById(R.id.tv_info);
        tvResendCode = findViewById(R.id.tv_resend_code);
        tvResendCodeTimer = findViewById(R.id.tv_resend_code_timer);
        etCodeOne = findViewById(R.id.et_code_one);
        etCodeTwo = findViewById(R.id.et_code_two);
        etCodeThree = findViewById(R.id.et_code_three);
        etCodeFour = findViewById(R.id.et_code_four);
        etCodeFive = findViewById(R.id.et_code_five);
        etCodeSix = findViewById(R.id.et_code_six);

        etCodeOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before == 0 && s.length() == 1) {
                    // letter entered
                    etCodeTwo.setEnabled(true);
                    etCodeTwo.requestFocus();
                } else if (before == 1 && s.length() == 0) {
                    // letter deleted
                    etCodeTwo.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etCodeTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before == 0 && s.length() == 1) {
                    // letter entered
                    etCodeOne.setEnabled(false);
                    etCodeThree.setEnabled(true);
                    etCodeThree.requestFocus();
                } else if (before == 1 && s.length() == 0) {
                    // letter deleted
                    etCodeThree.setEnabled(false);
                    etCodeOne.setEnabled(true);
                    etCodeOne.requestFocus();
                    etCodeTwo.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etCodeThree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before == 0 && s.length() == 1) {
                    // letter entered
                    etCodeTwo.setEnabled(false);
                    etCodeFour.setEnabled(true);
                    etCodeFour.requestFocus();
                } else if (before == 1 && s.length() == 0) {
                    // letter deleted
                    etCodeFour.setEnabled(false);
                    etCodeTwo.setEnabled(true);
                    etCodeTwo.requestFocus();
                    etCodeThree.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etCodeFour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before == 0 && s.length() == 1) {
                    // letter entered
                    etCodeThree.setEnabled(false);
                    etCodeFive.setEnabled(true);
                    etCodeFive.requestFocus();
                } else if (before == 1 && s.length() == 0) {
                    // letter deleted
                    etCodeThree.setEnabled(true);
                    etCodeThree.requestFocus();
                    etCodeFive.setEnabled(false);
                    etCodeFour.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etCodeFive.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before == 0 && s.length() == 1) {
                    // letter entered
                    etCodeFour.setEnabled(false);
                    etCodeSix.setEnabled(true);
                    etCodeSix.requestFocus();
                } else if (before == 1 && s.length() == 0) {
                    // letter deleted
                    etCodeSix.setEnabled(false);
                    etCodeFour.setEnabled(true);
                    etCodeFour.requestFocus();
                    etCodeFive.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etCodeSix.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (before == 0 && s.length() == 1) {
                    // letter entered
                    etCodeFive.setEnabled(false);
                    resolveVerificationAccess(true);
                } else if (before == 1 && s.length() == 0) {
                    // letter deleted
                    resolveVerificationAccess(false);
                    etCodeFive.setEnabled(true);
                    etCodeFive.requestFocus();
                    etCodeSix.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cdtResendCode = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // adding one second to millisUntilFinished because CountdownTimer starts counting
                // immediately.
                long adjustedMillisUntilFinished = millisUntilFinished + 1000;
                String timeRemaining = DateTimeUtils.getDigitalTime(adjustedMillisUntilFinished);
                if (adjustedMillisUntilFinished >= 1000) tvResendCodeTimer.setText(timeRemaining);
            }

            @Override
            public void onFinish() {
                tvResendCodeTimer.setText("");
                resolveResendCodeAccess(true);
                shouldResendCode = true;
            }
        };

        mAuth = FirebaseAuth.getInstance();

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                mVerificationInProgress = false;
                tvInfo.setText(getString(R.string.text_verification_code_sent));
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]
                Crashlytics.logException(e);

                if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(mRoot, getString(R.string.error_creating_account_unknown),
                            Snackbar.LENGTH_LONG).show();
                    // [END_EXCLUDE]
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                // Save verification ID and resending token so we can use them later
                mVerificationInProgress = false;
                mVerificationId = verificationId;
                mResendToken = token;
                tvInfo.setText(getString(R.string.text_verification_code_sent));
                etCodeOne.setEnabled(true);
                etCodeOne.requestFocus();
                if (shouldResendCode) resolveResendCodeAccess(true);
            }
        };
        // [END phone_auth_callbacks]


        btnVerify.setOnClickListener(view -> {
            try {
                String code = Objects.requireNonNull(etCodeOne.getText()).toString() +
                        Objects.requireNonNull(etCodeTwo.getText()).toString() +
                        Objects.requireNonNull(etCodeThree.getText()).toString() +
                        Objects.requireNonNull(etCodeFour.getText()).toString() +
                        Objects.requireNonNull(etCodeFive.getText()).toString() +
                        Objects.requireNonNull(etCodeSix.getText()).toString();
                disableInput();
                verifyPhoneNumberWithCode(mVerificationId, code);
            } catch (NullPointerException exception) {
                Crashlytics.logException(exception);
                Snackbar.make(mRoot, getString(R.string.error_unknown), Snackbar.LENGTH_SHORT).show();
            }
        });

        tvResendCode = findViewById(R.id.tv_resend_code);
        tvResendCode.setOnClickListener(view -> {
            if (shouldResendCode) {
                Toast.makeText(this, R.string.notif_code_resent, Toast.LENGTH_LONG).show();
                resendVerificationCode(mPhoneNumber, mResendToken);
            } else {
                Snackbar.make(mRoot, getString(R.string.notif_code_sent), Snackbar.LENGTH_LONG).show();
            }
        });

        inputUtils = InputUtils.init(pbWorking, etCodeSix);

        startPhoneNumberVerification(mPhoneNumber);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_FIRST_USER, returnIntent);
        finish();
    }

    /**
     * Performs necessary setup for the custom {@link androidx.appcompat.widget.Toolbar} for
     * this Activity.
     */
    private void setupActionBar() {
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.label_phone_verification);
        }
    }

    private void resolveVerificationAccess(boolean isAllowed) {
        btnVerify.setTextColor(ContextCompat.getColor(this,
                isAllowed ? android.R.color.white : R.color.grey_40));
        btnVerify.setAlpha(isAllowed ? 1f : 0.8f);
        btnVerify.setEnabled(isAllowed);
    }

    private void resolveResendCodeAccess(boolean isAllowed) {
        tvResendCode.setTextColor(ContextCompat.getColor(this,
                isAllowed ? R.color.colorPrimaryDark : R.color.grey_40));
        tvResendCode.setEnabled(isAllowed);
    }

    /**
     * Temporarily prevents user from changing parameters that are currently in use by an operation.
     * In this Activity, this means making the continue button ({@link this#btnVerify})
     * unresponsive to clicks, showing the progress bar on the button to
     * indicate that an operation is in progress, disabling input on the <em>verification code</em>
     * EditText, and preventing the <em>resend code</em> TextView
     * ({@link this#tvResendCode}) from being clicked.
     */
    private void disableInput() {
        inputUtils.disableInput();
        resolveVerificationAccess(false);
        resolveResendCodeAccess(false);
    }

    /**
     * Reverses all actions performed by {@link this#disableInput()}
     * In this Activity, this means making the continue button ({@link this#btnVerify})
     * responsive to clicks, hiding the progress bar on the button to
     * indicate that no operation is in progress, enabling input on the <em>verification code</em>
     * EditText, and allowing the <em>resend code</em> TextView
     * ({@link this#tvResendCode}) to be clicked.
     */
    private void enableInput() {
        inputUtils.enableInput();
        resolveVerificationAccess(true);
        if (shouldResendCode) resolveResendCodeAccess(true);
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
        etCodeOne.setEnabled(false);
        etCodeOne.setText("");
        etCodeTwo.setEnabled(false);
        etCodeTwo.setText("");
        etCodeThree.setEnabled(false);
        etCodeThree.setText("");
        etCodeFour.setEnabled(false);
        etCodeFour.setText("");
        etCodeFive.setEnabled(false);
        etCodeFive.setText("");
        etCodeSix.setEnabled(false);
        etCodeSix.setText("");
        btnVerify.setEnabled(false);
        resolveResendCodeAccess(false);
        shouldResendCode = false;
        cdtResendCode.start();
    }

    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        enableInput();
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } else {
                        // Sign in failed, display a message and update the UI
                        Exception exception = task.getException();
                        Crashlytics.logException(exception);
                        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Snackbar.make(mRoot, getString(R.string.error_incorrect_code), Snackbar.LENGTH_LONG).show();
                        } else if (exception instanceof FirebaseNetworkException) {
                            Snackbar.make(mRoot, getString(R.string.error_unable_to_connect), Snackbar.LENGTH_LONG).show();
                        }
                        enableInput();
                    }
                });
        // [END sign_in_with_phone]
    }
}