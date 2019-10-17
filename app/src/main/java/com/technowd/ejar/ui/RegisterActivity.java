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

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class RegisterActivity extends AppCompatActivity {
    Toolbar register_toolbar;
    private EditText register_email,register_password, register_repeat_password;
    private Functions functions = new Functions(RegisterActivity.this);
    private CircularProgressButton register_btn;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Init variables
        register_toolbar = findViewById(R.id.register_toolbar);
        register_email = findViewById(R.id.register_email);
        register_password = findViewById(R.id.register_password);
        register_repeat_password = findViewById(R.id.register_repeat_password);
        register_btn = findViewById(R.id.register_btn);
        mAuth = FirebaseAuth.getInstance();
        // Init toolbar
        setSupportActionBar(register_toolbar);
        // ------------------------------------------------------------------ //

    }
    // go to Login( activity
    public void goToLogin(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.back, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.back:
                goToMain();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // go to main activity
    private void goToMain() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    // Register
    public void register(View view) {
        if(functions.CheckInternet()){
            if(!(TextUtils.isEmpty(register_email.getText()) || TextUtils.isEmpty(register_password.getText()) ||
                    TextUtils.isEmpty(register_repeat_password.getText()))){
                if(register_email.getText().toString().contains("@")){
                    register_btn.startAnimation();
                    String email = register_email.getText().toString();
                    String pass = register_repeat_password.getText().toString();
                    // create new user via fire base auth
                    mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // check if process success
                            if(task.isSuccessful()){
                                functions.custom_toast(getString(R.string.user_added));
                                functions.goToActivityByParam(SetUpActivity.class);
                            } else if(task.getException().getMessage().contains("The email address is badly formatted")) {
                                functions.custom_toast(getString(R.string.email_not_right));
                            } else if(task.getException().getMessage().contains("The email address is already in use by another account.")){
                                functions.custom_toast(getString(R.string.email_is_exists));
                            } else if (task.getException().getMessage().contains("The given password is invalid. [ Password should be at least 6 characters ]")){
                                functions.custom_toast(getString(R.string.pass_cant_less_6));
                            }

                            register_btn.revertAnimation();
                        }
                    });
                } else {functions.custom_toast(getString(R.string.email_not_right));}
                // ------------------------------------------------------------- //
            } else {functions.custom_toast(getString(R.string.fields_empty));}
            // ------------------------------------------------------------- //
        } else {functions.custom_toast(getString(R.string.network_problem));}
    }
}
