package co.xtvt.xtvtco;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivitySignUp extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    EditText editTextUsername;
    EditText editTextEmail;
    EditText editTextPassword;
    TextInputLayout textInputLayoutUsername;
    TextInputLayout textInputLayoutEmail;
    TextInputLayout textInputLayoutPassword;
    Button buttonRegister;
    String username, email, password;
    Boolean userNameValid = false;
    Boolean userEmailValid = false;
    Boolean userPasswordValid = false;
    final long DELAY = 1500;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //hide action bar
        getSupportActionBar().hide();

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        textInputLayoutUsername = findViewById(R.id.textInputLayoutUsername);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        buttonRegister = findViewById(R.id.buttonSignUp);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        if (mAuth != null) {
            if (mAuth.getCurrentUser() != null) {
                Intent intent = new Intent(ActivitySignUp.this, ActivityDashboard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }

        findViewById(R.id.textViewLoginHere).setOnClickListener(this);
        findViewById(R.id.buttonSignUp).setOnClickListener(this);

        enableButton();
        callTextChangedListener();
    }

    private void callTextChangedListener(){
        try {
            editTextUsername.addTextChangedListener(new TextWatcher() {
                final android.os.Handler handler = new android.os.Handler();
                Runnable runnable;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    textInputLayoutUsername.setErrorEnabled(false);
                    textInputLayoutUsername.setError(null);
                    handler.removeCallbacks(runnable);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            validateUsername(new OnGetDataListener() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        userNameValid = false;
                                        textInputLayoutUsername.setErrorEnabled(true);
                                        textInputLayoutUsername.setError(getString(R.string.username_exists));
                                    } else {
                                        userNameValid = true;
                                    }
                                    enableButton();
                                }

                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onFailure() {

                                }
                            });
                        }
                    };
                    handler.postDelayed(runnable, DELAY);
                }
            });

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
                final android.os.Handler handler = new android.os.Handler();
                Runnable runnable;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    textInputLayoutPassword.setErrorEnabled(false);
                    textInputLayoutPassword.setError(null);
                    handler.removeCallbacks(runnable);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            validatePassword();
                            enableButton();
                        }
                    };
                    handler.postDelayed(runnable, DELAY);
                }
            });
        }catch (Exception e){
            Toast.makeText(ActivitySignUp.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public interface OnGetDataListener {
        //make new interface for call back
        void onSuccess(DataSnapshot dataSnapshot);
        void onStart();
        void onFailure();
    }

    public void validateUsername(final OnGetDataListener listener){
        try {
            userNameValid = false;
            username = editTextUsername.getText().toString().trim();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("usernames");

            if (username.matches("[a-zA-Z0-9]*")) {
                if (username.length() < 3) {
                    userNameValid = false;
                    textInputLayoutUsername.setErrorEnabled(true);
                    textInputLayoutUsername.setError(getString(R.string.minimum_length_3));
                } else {
                    userNameValid = true;
                    listener.onStart();
                    DatabaseReference usernameRef = FirebaseDatabase.getInstance().getReference("usernames");
                    usernameRef.orderByValue().equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            listener.onSuccess(dataSnapshot);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            listener.onFailure();
                        }
                    });
                }
            } else {
                userNameValid = false;
                textInputLayoutUsername.setErrorEnabled(true);
                textInputLayoutUsername.setError(getString(R.string.alphabetical_numerical));
            }
        }catch (Exception e){
            Toast.makeText(ActivitySignUp.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                            Boolean emailExists = !task.getResult().getProviders().isEmpty();
                            if (emailExists){
                                textInputLayoutEmail.setErrorEnabled(true);
                                textInputLayoutEmail.setError(getString(R.string.email_exists));
                                userEmailValid = false;
                            }else{
                                userEmailValid = true;
                            }
                            enableButton();
                        }
                    });
                }
            } else {
                userEmailValid = false;
            }
        }catch (Exception e){
            Toast.makeText(ActivitySignUp.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void validatePassword(){
        try {
            userPasswordValid = false;
            password = editTextPassword.getText().toString().trim();

            if (password.isEmpty()) {
                userPasswordValid = false;
            } else {
                if (password.length() < 6){
                    textInputLayoutPassword.setErrorEnabled(true);
                    textInputLayoutPassword.setError(getString(R.string.minimum_length_6));
                    userPasswordValid = false;
                }
                else {
                    userPasswordValid = true;
                }
            }
        }catch (Exception e){
            Toast.makeText(ActivitySignUp.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void enableButton(){
        try {
            username = editTextUsername.getText().toString().trim();
            email = editTextEmail.getText().toString().trim();
            password = editTextPassword.getText().toString().trim();

            if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty() &&
                    userNameValid && userEmailValid && userPasswordValid) {
                buttonRegister.setEnabled(true);
            } else {
                buttonRegister.setEnabled(false);
            }
        }catch (Exception e){
            Toast.makeText(ActivitySignUp.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            Intent intent;

            switch (v.getId()) {
                case R.id.textViewLoginHere:
                    intent = new Intent(ActivitySignUp.this, ActivityLogin.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    break;
                case R.id.buttonSignUp:
                    progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            validateUsername(new OnGetDataListener() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        userNameValid = false;
                                        textInputLayoutUsername.setErrorEnabled(true);
                                        textInputLayoutUsername.setError(getString(R.string.username_exists));
                                    } else {
                                        userNameValid = true;
                                    }
                                    enableButton();
                                }

                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onFailure() {

                                }
                            });
                            validateEmail();
                            validatePassword();
                            if (userNameValid && userEmailValid && userPasswordValid) {
                                registerUser();
                            }
                        }
                    }, DELAY);
                    break;

            }
        }catch (Exception e){
            Toast.makeText(ActivitySignUp.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser(){
        try {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        createNewUserName(task.getResult().getUser());
                        Intent intent = new Intent(ActivitySignUp.this, ActivityDashboard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(ActivitySignUp.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(ActivitySignUp.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void createNewUserName(FirebaseUser newUser){
        try {
            DatabaseReference databaseReferenceUserName =
                    FirebaseDatabase.getInstance().getReference("usernames");

            String newusername = editTextUsername.getText().toString().trim();
            String newuserid = newUser.getUid();

            databaseReferenceUserName.child(newuserid).setValue(newusername);
        }catch (Exception e){
            Toast.makeText(ActivitySignUp.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
