package com.technowd.ejar.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.technowd.ejar.MainActivity;
import com.technowd.ejar.R;
import com.technowd.ejar.general.Functions;

public class AboutActivity extends AppCompatActivity {
    Toolbar about_tool_bar;
    private Functions functions = new Functions(AboutActivity.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        about_tool_bar = findViewById(R.id.about_tool_bar);
        setSupportActionBar(about_tool_bar);
    }

    public void facebook(View view) {
        Uri faceookLink = Uri.parse("https://www.facebook.com/AlnofaliyaAlriyadah/");
        Intent facebook = new Intent(Intent.ACTION_VIEW, faceookLink);
        startActivity(facebook);
    }

    @SuppressLint("IntentReset")
    public void mail(View view) {
        String[] To = new String[] {"salemaljebaly@gmail.com"};
        Intent sendEmail = new Intent(Intent.ACTION_SEND);
        sendEmail.setData(Uri.parse("mailto:"));
        sendEmail.setType("text/plain");
        sendEmail.putExtra(Intent.EXTRA_EMAIL,To);
        sendEmail.putExtra(Intent.EXTRA_SUBJECT,R.string.app_name);
        sendEmail.putExtra(Intent.EXTRA_TITLE,R.string.app_name);
        sendEmail.putExtra(Intent.EXTRA_TEXT,"إكتب ملاحظتك");
        startActivity(sendEmail);
    }

    public void playStore(View view) {
        Uri playstore = Uri.parse("https://play.google.com/store/apps/developer?id=salem+abdulla");
        Intent play = new Intent(Intent.ACTION_VIEW, playstore);
            if (play.resolveActivity(getPackageManager()) != null) {
                startActivity(play);
            }
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
        switch(item.getItemId()){
            case R.id.back:
                goToLogin();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // go to Login( activity
    public void goToLogin() {
        Intent intent = new Intent(AboutActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Share app
    public void share(View view) {
            String shareLink = "https://play.google.com/store/apps/details?id=com.technowd.ejar";
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT,shareLink);
            startActivity(share);
    }
}
