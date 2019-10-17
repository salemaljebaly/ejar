package com.technowd.ejar.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.technowd.ejar.MainActivity;
import com.technowd.ejar.R;
import com.technowd.ejar.general.Functions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpActivity extends AppCompatActivity {
    public Spinner setup_spinner_city;
    Toolbar setup_tool_bar;
    Functions functions = new Functions(SetUpActivity.this);
    private EditText user_name ,user_phone;
    private CircleImageView user_image;
    private Uri user_image_uri = null; // image Uri
    private CircularProgressButton save_setup; // progress button
    private FirebaseAuth maAuth;
    private FirebaseUser currrnetUser;
    private String user_id;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private String selectedItem; // take selected item from spinner
    private int selectedItemID; // take selected item ID from spinner
    private boolean image_changed = false; // chaeck if image changed or not
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);
        // variables
        maAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_name = findViewById(R.id.user_name);
        user_phone = findViewById(R.id.user_phone);
        user_image = findViewById(R.id.user_image);
        save_setup = findViewById(R.id.save_setup);
        // Init toolbar
        setup_tool_bar = findViewById(R.id.setup_tool_bar);
        setSupportActionBar(setup_tool_bar);
        setup_spinner_city = findViewById(R.id.setup_spinner_city);
        // ----------------------------------------------------------------------------- //
        // set spinner items
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.city_spinner
        , android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setup_spinner_city.setAdapter(adapter);
        // Take picture from storage
        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check permission if android version greater M = 23
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(SetUpActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        // Ask user to READ_EXTERNAL_STORAGE
                        ActivityCompat.requestPermissions(SetUpActivity.this,new  String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    } else {
                        imagePicker();
                    }
                } else {
                    // Take image and crop it
                    imagePicker();
                }
                //------------------------------------------------------------------------//
            }
        });
        //------------------------------------------------------------------------//
        // check if user is logged in
        currrnetUser= maAuth.getCurrentUser(); // get current user
        user_id = maAuth.getCurrentUser().getUid();
        // if user logged in then get Data from firebase storage
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        user_name.setText(task.getResult().getString("user_name"));
                        user_phone.setText(task.getResult().getString("user_phone"));

                        setup_spinner_city.setSelection(Integer.parseInt(task.getResult().getString("city_id")));
                        Log.e("searchSpiner", "onComplete: "+task.getResult().getString("city_id") );
                        // TODO solve spinner get value problem
                        Log.e("uri", task.getResult().getString("city_id"));
                        // get Image Uri from database Storage
                        storageReference.child("profile_images/" + user_id +".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Got the download URL for 'users/me/profile.png'
                                user_image_uri = uri;
                                Picasso.get().load(uri).into(user_image); // set user_image from firebase profile_images
                            }
                        });
                        // --------------------------------------------------------- //
                    } else {
                        functions.custom_toast(getString(R.string.there_is_no_data_filled));
                    }
                }
            }
        });
        // ----------------------------------------------------------------------------- //
        // Take selected item in spinner
        setup_spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItem = adapterView.getItemAtPosition(i).toString();
                selectedItemID = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.e("spinner", adapterView.toString());
            }
        });
        // ----------------------------------------------------------------------------- //

    }
    // Pick image from gallary and crop it
    private void imagePicker() {
        // Take image and crop it
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .setMaxCropResultSize(500,500)
                .start(SetUpActivity.this);
    }
    // ----------------------------------------------------------------------------- //

    // save set up data
    public void saveSetUp(View view){
        if(functions.CheckInternet()){ // check intetnet
            if(image_changed){ // Check if iamge set from gallery or not ?

                // Check valid inputs
                if(!(TextUtils.isEmpty(user_phone.getText()) || TextUtils.isEmpty(user_phone.getText())
                        || selectedItem.equals("إختر المدينة"))){
                        save_setup.startAnimation();
                        user_id = maAuth.getCurrentUser().getUid();
                        final StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");
                        // upload image to firebase storage
                        image_path.putFile(user_image_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    storeFireStore(task);
                                } else {
                                    functions.custom_toast(task.getException().getMessage());
                                }
                                save_setup.revertAnimation();
                            }
                        });
                    //----------------------------------------------------------------------------//
                } else {functions.custom_toast("لايمكن ترك أين من الحقول فارغا");} // End valid inputs
            } else {functions.custom_toast("يجب إختيار صورة");}
//            else {
//                save_setup.startAnimation();
//                if(!selectedItem.equals("إختر المدينة")){ // check if spinner not empty
//                    storeFireStore(null);
//                } else {functions.custom_toast("يجب إختيار المدينة");}
//                save_setup.revertAnimation();
//            }
            // ------------------------------------------------------------- //
        } else {functions.custom_toast("يوجد مشكلة في الشبكة");} // connection problem
        // ------------------------------------------------------------- //
    }
    // custom func to store date in DB
    private void storeFireStore(Task<UploadTask.TaskSnapshot> task) {
        // pass values via HashMap
        String phone = user_phone.getText().toString();
        Map<String, String> userMap = new HashMap<>();
        userMap.put("user_name", user_name.getText().toString());
        userMap.put("city_id", String.valueOf(selectedItemID));
        userMap.put("city", selectedItem);
        userMap.put("user_have_image", "true");
        userMap.put("user_phone", phone);
        // update user info
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(user_name.getText().toString())
                .setPhotoUri(user_image_uri)
                .build();
        currrnetUser.updateProfile(profileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e("user", "is updated");
                    }
                });

        // ------------------------------------- //
        // Store date in firebase firestore
        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    functions.custom_toast("تمت عملية حفظ البيانات");
                    // navigate to mainactivity
                    functions.goToActivityByParam(MainActivity.class);
                } else {
                    functions.custom_toast(task.getException().getMessage());
                }
            }
        });
        //-----------------------------------------------------------------//
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
                functions.goToActivityByParam(MainActivity.class);
                return true;
//            case R.id.delete_user:
//                 deleteUserWithAllRents();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // Catch activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                user_image_uri = result.getUri();
                user_image.setImageURI(user_image_uri);
                image_changed = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
