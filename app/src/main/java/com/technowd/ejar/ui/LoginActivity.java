package com.technowd.ejar.ui;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.technowd.ejar.MainActivity;
import com.technowd.ejar.R;
import com.technowd.ejar.general.Functions;

import java.util.Objects;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {
    Toolbar login_toolbar;
    private EditText login_email,login_password;
    private CircularProgressButton login_btn;
    private Functions functions = new Functions(LoginActivity.this);
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // get Variables
        login_toolbar = findViewById(R.id.login_toolbar);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_btn = findViewById(R.id.login_btn);
        mAuth = FirebaseAuth.getInstance();
        // Init toolbar
        setSupportActionBar(login_toolbar);
        // Log in
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logIn();
            }
        });
        //-------------------------------------------------------------------------------------//
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // go to Register activity
    public void goToRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
    // go to Restore password activity
    public void goToRestorePassword(View view) {
        Intent intent = new Intent(LoginActivity.this, RestorePasswordActivity.class);
        startActivity(intent);
        finish();
    }

    // Create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.back, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // do event when user press item from menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.back) {
            functions.goToActivityByParam(MainActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // ------------------------------------------------------------------//
    // Log in via firebase
    public void logIn() {
        if(functions.CheckInternet()){
            // ------------------------------------------------------------- //
            if(!(TextUtils.isEmpty(login_email.getText()) || TextUtils.isEmpty(login_password.getText()))) {
                // ------------------------------------------------------------- //
                if(login_email.getText().toString().contains("@")) {
                    login_btn.startAnimation();
                    final String email = login_email.getText().toString();
                    final String pass = login_password.getText().toString();
                    mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                functions.goToActivityByParam(NewRentActivity.class);
                            }else if(Objects.requireNonNull(task.getException()).getMessage().contains("The password is invalid or the user does not have a password")){
                                    functions.custom_toast(getString(R.string.password_incorrect));
                            }else if(task.getException().getMessage().contains("There is no user record corresponding to this identifier. The user may have been deleted")){
                                functions.custom_toast(getString(R.string.there_no_user));
                            }
                            // stop animation
                            login_btn.revertAnimation();
                        }
                    });
                } else {functions.custom_toast(getString(R.string.email_not_right));}
                // ------------------------------------------------------------- //
            } else {functions.custom_toast(getString(R.string.fields_empty));}
            // ------------------------------------------------------------- //
        } else {functions.custom_toast(getString(R.string.network_problem));}
    }// End log in

    @Override
    protected void onDestroy() {
        super.onDestroy();
        login_btn.dispose();
    }
}
