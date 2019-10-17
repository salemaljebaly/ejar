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

public class AboutActivity extends AppCompatActivity {
    Toolbar about_tool_bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        about_tool_bar = findViewById(R.id.about_tool_bar);
        setSupportActionBar(about_tool_bar);
    }

    public void facebook(View view) {
        Uri faceookLink = Uri.parse(getString(R.string.facebook_url));
        Intent facebook = new Intent(Intent.ACTION_VIEW, faceookLink);
        startActivity(facebook);
    }

    @SuppressLint("IntentReset")
    public void mail(View view) {
        String[] To = new String[] {getString(R.string.developer_email)};
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
        Uri playstore = Uri.parse(getString(R.string.google_play_developer_url));
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
        if (item.getItemId() == R.id.back) {
            goToLogin();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // go to Login( activity
    public void goToLogin() {
        Intent intent = new Intent(AboutActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Share app
    public void share(View view) {
            String shareLink = getString(R.string.app_url);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT,shareLink);
            startActivity(share);
    }
}
