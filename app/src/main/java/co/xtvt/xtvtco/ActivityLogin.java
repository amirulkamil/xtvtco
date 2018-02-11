package co.xtvt.xtvtco;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    EditText editTextEmail;
    TextInputLayout textInputLayoutEmail;
    EditText editTextPassword;
    Button buttonLogin;
    String email, password;
    Boolean userEmailValid = false;
    Boolean userPasswordValid = false;
    final long DELAY = 1500;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_24dp);
        getSupportActionBar().setTitle(R.string.login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);

        //set progressbar
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        if (mAuth != null) {
            if (mAuth.getCurrentUser() != null) {
                Intent intent = new Intent(ActivityLogin.this, ActivityDashboard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }

        findViewById(R.id.textViewRegister).setOnClickListener(this);
        findViewById(R.id.buttonLogin).setOnClickListener(this);
        findViewById(R.id.textViewForgotPassword).setOnClickListener(this);

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
        try {
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

            editTextPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    validatePassword();
                    enableButton();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }catch (Exception e){
            Toast.makeText(ActivityLogin.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(ActivityLogin.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void validatePassword(){
        try {
            password = editTextPassword.getText().toString().trim();

            if (password.isEmpty()) {
                userPasswordValid = false;
            } else {
                userPasswordValid = true;
            }
        }catch (Exception e){
            Toast.makeText(ActivityLogin.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void enableButton(){
        try {
            email = editTextEmail.getText().toString().trim();
            password = editTextPassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty() && userEmailValid && userPasswordValid) {
                buttonLogin.setEnabled(true);
            } else {
                buttonLogin.setEnabled(false);
            }
        }catch (Exception e){
            Toast.makeText(ActivityLogin.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            Intent intent;

            switch (v.getId()) {
                case R.id.textViewRegister:
                    intent = new Intent(ActivityLogin.this, ActivitySignUp.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    break;
                case R.id.buttonLogin:
                    progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            validateEmail();
                            validatePassword();
                            if (userEmailValid && userPasswordValid) {
                                userLogin();
                            }
                        }
                    }, DELAY);
                    break;
                case R.id.textViewForgotPassword:
                    intent = new Intent(ActivityLogin.this, ActivityResetPassword.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    break;

            }
        }catch (Exception e){
            Toast.makeText(ActivityLogin.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void userLogin(){
        try {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(ActivityLogin.this, ActivityDashboard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ActivityLogin.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(ActivityLogin.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
