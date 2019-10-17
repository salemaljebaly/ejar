package com.technowd.ejar.ui;

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
import com.google.firebase.auth.FirebaseAuth;
import com.technowd.ejar.R;
import com.technowd.ejar.general.Functions;

import java.util.Objects;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class RestorePasswordActivity extends AppCompatActivity {
    Toolbar restore_toolbar;
    private EditText restore_email;
    private CircularProgressButton restore_btn;
    private FirebaseAuth mAuth;
    private Functions functions = new Functions(RestorePasswordActivity.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_password);

        // variables
        restore_btn = findViewById(R.id.restore_btn);
        restore_email = findViewById(R.id.restore_email);
        restore_toolbar = findViewById(R.id.restore_toolbar);
        mAuth = FirebaseAuth.getInstance();
        // ------------------------------------------------ //
        // Init tool bar
        setSupportActionBar(restore_toolbar);
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
            functions.goToActivityByParam(LoginActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // Recover user password by send an email
    public void recoverPasswordByEmail(View view) {
        if(functions.CheckInternet()){
            if(!(TextUtils.isEmpty(restore_email.getText()))){
                if(restore_email.getText().toString().contains("@")){
                    restore_btn.startAnimation();
                    mAuth.sendPasswordResetEmail(restore_email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                functions.custom_toast(getString(R.string.sent_msg_to_u_email));
                            } else {
                                if(Objects.requireNonNull(task.getException()).getMessage().contains("There is no user record corresponding to this identifier. The user may have been deleted.")){
                                    functions.custom_toast("هذا الايميل غير موجود مسبقا !");
                                } else {functions.custom_toast("حدث خطأ تأكد من الايميل !");}
                            }
                            restore_btn.revertAnimation();
                        }
                    });
                } else {functions.custom_toast("يجب كتابة ايميل صحيح");} // End valid email
            } else {functions.custom_toast("لايمكن ترك أين من الحقول فارغا");} // End valid inputs
        } else {functions.custom_toast("يوجد مشكلة في الشبكة");} // connection problem


    }
}
