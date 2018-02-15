package co.xtvt.xtvtco;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityResetPassword extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextEmail;
    private TextInputLayout textInputLayoutEmail;
    private Button buttonSubmit;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String email;
    ProgressBar progressBar;
    final long DELAY = 1500;
    Boolean userEmailValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        getSupportActionBar().setTitle(R.string.resetpassword);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editTextEmail = findViewById(R.id.editTextEmail);
        buttonSubmit = findViewById(R.id.buttonSendEmail);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);

        //set progressbar
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        findViewById(R.id.buttonSendEmail).setOnClickListener(this);

        enableButton();
        callTextChangedListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void callTextChangedListener(){
        try
        {
            editTextEmail.addTextChangedListener(new TextWatcher() {
                final android.os.Handler handler = new android.os.Handler();
                Runnable runnable;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    textInputLayoutEmail.setErrorEnabled(false);
                    textInputLayoutEmail.setError(null);
                    handler.removeCallbacks(runnable);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            validateEmail();
                            enableButton();
                        }
                    };
                    handler.postDelayed(runnable, DELAY);
                }
            });
        }catch (Exception e){
            Toast.makeText(ActivityResetPassword.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void validateEmail(){
        try {
            userEmailValid = false;
            email = editTextEmail.getText().toString().trim();

            if (!email.isEmpty()) {
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    textInputLayoutEmail.setErrorEnabled(true);
                    textInputLayoutEmail.setError(getString(R.string.email_format_is_invalid));
                    userEmailValid = false;
                } else {
                    userEmailValid = true;
                }
            } else {
                userEmailValid = false;
            }
        }catch (Exception e){
            Toast.makeText(ActivityResetPassword.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void enableButton(){
        try {
            email = editTextEmail.getText().toString().trim();

            if (!email.isEmpty()) {
                buttonSubmit.setEnabled(true);
            } else {
                buttonSubmit.setEnabled(false);
            }
        }catch (Exception e){
            Toast.makeText(ActivityResetPassword.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void submitForgotPassword(){
        try {
            String email = editTextEmail.getText().toString().trim();
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ActivityResetPassword.this, "Email is already sent", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(ActivityResetPassword.this, task.getException().getLocalizedMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(ActivityResetPassword.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        try
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            Intent intent;

            switch (v.getId()){
                case R.id.buttonSendEmail:
                    progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            validateEmail();
                            if (userEmailValid) {
                                submitForgotPassword();
                            }
                        }
                    }, DELAY);
                    break;
            }
        }catch (Exception e){
            Toast.makeText(ActivityResetPassword.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
