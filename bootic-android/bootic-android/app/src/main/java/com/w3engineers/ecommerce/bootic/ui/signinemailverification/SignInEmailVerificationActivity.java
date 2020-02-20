package com.w3engineers.ecommerce.bootic.ui.signinemailverification;

import android.content.Intent;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BaseActivity;
import com.w3engineers.ecommerce.bootic.ui.emailverification.EmailVerificationMvpView;
import com.w3engineers.ecommerce.bootic.ui.emailverification.EmailVerificationPresenter;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserRegistrationResponse;
import com.w3engineers.ecommerce.bootic.ui.signinresendcode.SignInEmailSendActivity;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.Loader;
import com.w3engineers.ecommerce.bootic.data.util.StatusBarHelper;
import com.w3engineers.ecommerce.bootic.ui.welcome.WelcomeActivity;


public class SignInEmailVerificationActivity extends BaseActivity<EmailVerificationMvpView, EmailVerificationPresenter> implements EmailVerificationMvpView {

    private TextView textViewError;
    private Button buttonContinue;
    private PinEntryEditText editTextPinCode;
    private EditText editTextEmail;
    private String email, pinCode;
    private boolean aBoolean;
    private Loader mLoader;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_email_verification_2;
    }

    @Override
    protected void startUI() {
        initToolbar();
        mLoader = new Loader(this);
        StatusBarHelper.getStatusBarTransparent(this, R.color.black);
        getDataFromIntent();
        initViews();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            email = intent.getStringExtra(SignInEmailSendActivity.EMAIL_KEY);
        }
    }

    /**
     * init view with data
     */
    private void initViews() {
        //  textViewResendCode = findViewById(R.id.text_view_resend);
        textViewError = findViewById(R.id.text_view_email_error);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPinCode = findViewById(R.id.edit_text_pin_code);
        buttonContinue = findViewById(R.id.button_sign_in);

        if (!email.equals(""))
            editTextEmail.setText(email);
        mLoader = new Loader(this);
        setClickListener(buttonContinue);

        getPinCodeFromUser();
        checkFieldValidation();

    }

    /**
     * checking input field validation
     */
    private void checkFieldValidation() {
        editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!presenter.emailValidity(editTextEmail.getText().toString())
                            || editTextEmail.getText().toString().equals("")) {
                        textViewError.setVisibility(View.VISIBLE);
                        textViewError.setText(getString(R.string.valid_email));
                        aBoolean = false;
                    } else {
                        textViewError.setVisibility(View.INVISIBLE);
                        aBoolean = true;
                    }
                } else {
                    aBoolean = !editTextEmail.getText().toString().equals("")
                            && presenter.emailValidity(editTextEmail.getText().toString());
                    textViewError.setVisibility(View.INVISIBLE);
                }
            }
        });

        if (presenter.emailValidity(email)) {
            aBoolean = true;
        }
    }

    /**
     * get pin from input field
     */
    private void getPinCodeFromUser() {
        editTextPinCode.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
            @Override
            public void onPinEntered(CharSequence str) {
                if (str.length() == 4) {
                    pinCode = String.valueOf(str);
                }
            }
        });
    }

    /**
     * init toolbar
     */
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.button_sign_in) {

            email = editTextEmail.getText().toString();
            if (!email.equals("") && pinCode.length() == 4) {
                if (aBoolean) {
                    mLoader.show();
                    presenter.doEmailVerification(email, pinCode, this);
                } else {
                    Toast.makeText(this, getString(R.string.check_input_field), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.fiil_field), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void stopUI() {

    }

    @Override
    protected EmailVerificationPresenter initPresenter() {
        return new EmailVerificationPresenter();
    }

    @Override
    public void onEmailVerificationSuccess(UserRegistrationResponse user) {
        if (user != null) {
            if (user.statusCode == 200) {
                mLoader.stopLoader();
                CustomSharedPrefs.setLoggedInUser(this, user);
                CustomSharedPrefs.setRegisteredUser(this);
                startActivity(new Intent(this, WelcomeActivity.class));
                finish();
            } else {
                Toast.makeText(this, user.message, Toast.LENGTH_SHORT).show();
            }
        }
        mLoader.stopLoader();

    }

    @Override
    public void onEmailVeridicationError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        mLoader.stopLoader();
    }

    @Override
    public void onResendCodeSuccess(UserRegistrationResponse user) {
        Toast.makeText(this, user.message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResendCodeError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
