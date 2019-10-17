package com.technowd.ejar.general;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.technowd.ejar.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

// most functions used in project
public class Functions {
    private Context context;
    public Functions(Context context) {
        this.context = context;
    }
    // Check if there is internet connection
    public Boolean CheckInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    //    // Custom toast
    //    public Toast custom_toast(){
    //        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    //        View layout = inflater.inflate(R.layout.custom_toast,(ViewGroup) R.id.cu )
    //    }
    // Navigate to any activity by class param
    public void goToActivityByParam(Class mClass) {
        Intent intent = new Intent(context, mClass);
        context.startActivity(intent);
        ((Activity) context).finish();
    }
    // Custom toast
    public void custom_toast(String text){
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) ((Activity) context).findViewById(R.id.custom_toast_container));

        TextView toast_text = (TextView) layout.findViewById(R.id.toast_text);
        toast_text.setText(text);

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public  boolean validLogin (){
        Activity get = ((Activity) context);
        EditText login_email = get.findViewById(R.id.login_email);
        EditText login_password = get.findViewById(R.id.login_password);
        // check if an email
        if(!CheckInternet()){
            custom_toast("يوجد مشكلة في الشبكة");
            return false;
        }else if(TextUtils.isEmpty(login_email.getText()) && TextUtils.isEmpty(login_password.getText())) {
            custom_toast("لايمكن ترك أين من الحقول فارغا");
            login_email.setError("لايمكن ترك أين من الحقول فارغا");
            return false;
        } else if(!(login_email.getText().toString().contains("@"))){
            custom_toast("يجب كتابة ايميل صحيح");
            return false;
        } else {
            return true;
        }
    }

    GregorianCalendar calendar = new GregorianCalendar();
    public final String currentTime = (calendar.get(Calendar.YEAR) + "-" +
            (calendar.get(Calendar.MONTH) + 1) + "-" + (calendar.get(Calendar.DAY_OF_MONTH) + 1) + "-" + (calendar.get(Calendar.HOUR_OF_DAY)) + ":" + calendar.get(Calendar.MINUTE)+ ":" + calendar.get(Calendar.SECOND));
    public boolean isAppInstalled(String packageName){
        try {
            context.getPackageManager().getApplicationInfo(packageName,0);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
