package com.technowd.ejar.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.technowd.ejar.MainActivity;
import com.technowd.ejar.R;
import com.technowd.ejar.general.Functions;
import com.technowd.ejar.model.RentPosts;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class NewRentActivity extends AppCompatActivity  {
    Toolbar new_ren_tool_bar;
    private Functions functions = new Functions(NewRentActivity.this);
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FirebaseUser user;
    private String user_id;// user id
    private String rent_id; // rent ID which genarate automatic from firebase
    private String daily_or_monthly_text, placeText;// spinners
    private Uri rent_image_uri;
    private ImageView rent_image;
    private CircularProgressButton new_rent_btn;
    private boolean user_set_rent_image = false; // user when user take image or not
    private EditText new_rent_price,
            new_rent_state,new_rent_type,
            new_rent_room_numbers,new_rent_desc;
    private Spinner daily_or_monthly,place;
    @SuppressLint("StaticFieldLeak")
    public static EditText new_rent_latitude;
    @SuppressLint("StaticFieldLeak")
    public static EditText new_rent_longitude; // all inputs
    private ArrayList<RentPosts> rentPosts; // used to retrieve data as rent oist model
    private Boolean comeFromUser = false; // if true edit rent else not
    private int pos,placeIndex;
    private Map<String, Object> updateMap = new HashMap<>(); // update rent map
    private static final String TAG = "NewRent";
    // ------------------------------------------------------------------------ //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_rent);
        // Init Toolbar
        new_ren_tool_bar = findViewById(R.id.new_ren_tool_bar);
        setSupportActionBar(new_ren_tool_bar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("إضافة إيجار جديد");
        // --------------------------------------------------------------------- //
        // All variables
        initAllVariables();
        // ---------------------------------------------------------------- //
        // Daily or monthly spinner items
        dailyMonthlySpinner();
        // ---------------------------------------------------------------- //
        // inti firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        if(user != null){
            user_id = user.getUid();
        }
        // --------------------------------------------------------------------- //

        rentPosts = (ArrayList<RentPosts>) getIntent().getSerializableExtra("rentPosts");
        pos = getIntent().getIntExtra("pos",0);
        comeFromUser = getIntent().getBooleanExtra("comeFromUser",false); // get value from another activity
        // --------------------------------------------------------------------- //
        // if user want to edit then fill data
        ifComeFromUserFillData();
        // --------------------------------------------------------------------- //
        // place spinner
        placeSpinner();
        // --------------------------------------------------------------------- //
        // getUserID();
        // --------------------------------------------------------------------- //
    }

    // get rent id
    private void getUserID() {
        //   get rent ID used when update
        rent_id = rentPosts.get(pos).getDocumentId();
        Log.e(TAG, "getUserID" + rent_id);
    }

    // if user want to edit then fill data
    private void ifComeFromUserFillData() {
        if(comeFromUser){
            if(rentPosts.get(pos).getUser_set_rent_image().equals("true")){
                // get image download link of current rent
                Log.e("downlosd_link","rent_images/" + user.getUid() + "/" + rentPosts.get(pos).getTime() + ".jpg");
                storageReference.child("rent_images/" + user_id + "/" + rentPosts.get(pos).getTime() + ".jpg")
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.e("uri",uri.toString());
                        rent_image_uri = uri;
                        FillRentFromDB();
                        Picasso.get().load(uri).into(rent_image); // set main rent from firebase storage rent_images
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        FillRentFromDB();
                    }
                });
            } else {
                FillRentFromDB();
            }
            // -------------------------------------------------------------------------------- //

        } else{
            Log.e("comeFromUser","false");
        }
    }

    // Daily or monthly spinner items
    private void dailyMonthlySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.daily_or_monthly
                , android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daily_or_monthly.setAdapter(adapter);
        daily_or_monthly.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                daily_or_monthly_text = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    // Init all variables
    private void initAllVariables() {
        // All variables
        rent_image =findViewById(R.id.rent_image);
        place      = findViewById(R.id.place);
        new_rent_price =findViewById(R.id.new_rent_price);
        new_rent_state =findViewById(R.id.new_rent_state);
        new_rent_type =findViewById(R.id.new_rent_type);
        new_rent_room_numbers =findViewById(R.id.new_rent_room_numbers);
        new_rent_desc =findViewById(R.id.new_rent_desc);
        new_rent_latitude =findViewById(R.id.new_rent_latitude);
        new_rent_longitude =findViewById(R.id.new_rent_longitude);
        new_rent_btn =findViewById(R.id.new_rent_btn);
        daily_or_monthly = findViewById(R.id.daily_or_monthly);
    }

    // place spinner spinner
    public void placeSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.city_spinner
                , android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        place.setAdapter(adapter);
        place.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!place.getSelectedItem().toString().equalsIgnoreCase("إختر المدينة")) {
                    placeText = adapterView.getItemAtPosition(i).toString();
                    placeIndex = i;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.e("spinner", adapterView.toString());
            }
        });
//        if(!place.getSelectedItem().toString().equalsIgnoreCase("إختر المدينة")){
//            placeText = place.getSelectedItem().toString();
//            placeIndex = String.valueOf(place.getId()); // return id og item as string
//
//            Log.e("onClick", place.getSelectedItem().toString());
//            Log.e("onClick", String.valueOf(place.getId()));
//            functions.custom_toast(placeText);
//        } else {
//
//        }

    }
    // on start method
    @Override
    protected void onStart() {
        super.onStart();
        if(user == null){
            functions.goToActivityByParam(LoginActivity.class);
        } else {
            // if user sitting is empty
            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if(!Objects.requireNonNull(task.getResult()).exists()){
                            functions.goToActivityByParam(SetUpActivity.class);
                        }
                    } else{
                        functions.custom_toast(Objects.requireNonNull(task.getException()).getMessage());
                    }
                }
            });
        }
    }

    // Select Image
    public void selectImage(View view){
        // If android OS greater than Marsha
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                // Ask user to get Permissions
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            } else{
                // user have Permissions
                imagePicker();
            }
        } else {
            // android os under M
            imagePicker();
        }
    }
    // pick image and crop it
    private void imagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(4,2)
                .setMaxCropResultSize(800,1000)
                .start(NewRentActivity.this);
    }

    // pick result from intent on CropImage
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                rent_image_uri = result.getUri();
                rent_image.setImageURI(rent_image_uri);
                user_set_rent_image = true;
            } else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
                Log.e(TAG, "onActivityResult: "+ error );
            }
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
            functions.goToActivityByParam(MainActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // post new rent to data base
    public void postNewRent(View view) {
        if(functions.CheckInternet()) { // check intetnet
            if(rentValid()){ // validate inputs
                new_rent_btn.startAnimation(); // start button progress
                Log.e("user_set_rent_image ",user_set_rent_image + "" );
                if(user_set_rent_image) { // check if user select image or not
                    Log.e("functions.currentTime ",functions.currentTime );
                    final StorageReference rent_image_path;
                    if(comeFromUser){
                        // when user edit his rent
                        // imageTimeName is image name and I wanna to override it
                        // storageReference.child("rent_images/" + user_id + "/").child(imageTimeName + ".jpg").delete();
                        // Update image
                        Log.e("imageUpload", "postNewRent: "+"rent_images/" + user_id + "/" + rentPosts.get(pos).getTime() + ".jpg" );
                        rent_image_path = storageReference.child("rent_images/" + user_id + "/" + rentPosts.get(pos).getTime() + ".jpg");
                        rent_image_path.putFile(rent_image_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    updateRentData();
                                } else {
                                    functions.custom_toast(Objects.requireNonNull(task.getException()).getMessage());
                                }
                                new_rent_btn.revertAnimation();
                            }
                        });
                        // ------------------------------------------------------------------------- //
                    } else {
                        // in default add
                        rent_image_path = storageReference.child("rent_images/" + user_id + "/").child(functions.currentTime + ".jpg");

                        rent_image_path.putFile(rent_image_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    rentFireStore(functions.currentTime); // store data in DB

                                } else {
                                    functions.custom_toast(Objects.requireNonNull(task.getException()).getMessage());
                                }
                                new_rent_btn.revertAnimation();
                            }
                        });
                    }
                } else {
                    if(comeFromUser){
                        updateRentData();
                    }else {
                        rentFireStore(functions.currentTime); // store data in DB
                    }
                }
            } else {functions.custom_toast(getString(R.string.fields_empty));} // End valid inputs
            // ------------------------------------------------------------- //
        } else {functions.custom_toast(getString(R.string.network_problem));} // connection problem
    // ------------------------------------------------------------- //
    }

    // store data in DB of firebase store
    private void rentFireStore(String currentTime) {

        final Map<String, Object> rentMap = new HashMap<>();
        if(!TextUtils.isEmpty(placeText)){
            rentMap.put("place",placeText);
            Log.e("onClick", placeText);
        }
        rentMap.put("place_index",placeIndex);
        rentMap.put("user_id",user_id);
        rentMap.put("new_rent_price",new_rent_price.getText().toString());
        rentMap.put("new_rent_state",new_rent_state.getText().toString());
        rentMap.put("new_rent_type",new_rent_type.getText().toString());
        rentMap.put("new_rent_room_numbers",new_rent_room_numbers.getText().toString());
        rentMap.put("rent_desc",new_rent_desc.getText().toString());
        rentMap.put("daily_or_monthly",daily_or_monthly_text);
        rentMap.put("user_set_rent_image",String.valueOf(user_set_rent_image));
        rentMap.put("latitude",new_rent_latitude.getText().toString());
        rentMap.put("longitude",new_rent_longitude.getText().toString());
        rentMap.put("isVacant",true);
        // ---
        rentMap.put("time",currentTime);
        firebaseFirestore.collection("Rents").add(rentMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if(task.isSuccessful()){
                    Objects.requireNonNull(getSupportActionBar()).setTitle("تعديل بيانات الايجار ");
                    functions.goToActivityByParam(MainActivity.class);
                    functions.custom_toast(getString(R.string.ejar_added));
                } else { functions.custom_toast(getString(R.string.error));}
            }
        });
    }

    // validate inputs
    public boolean rentValid(){
        return !(TextUtils.isEmpty(place.getSelectedItem().toString()) || TextUtils.isEmpty(new_rent_price.getText()) ||
                TextUtils.isEmpty(new_rent_state.getText()) || TextUtils.isEmpty(new_rent_type.getText())
                || TextUtils.isEmpty(new_rent_room_numbers.getText()) || TextUtils.isEmpty(new_rent_desc.getText())
                || TextUtils.isEmpty(new_rent_latitude.getText()) || TextUtils.isEmpty(new_rent_longitude.getText()) ||
                place.getSelectedItem().toString().equalsIgnoreCase("إختر المدينة"));
    }
    // open map activity
    public void selectPlacInMap(View view) {
        // show fragment dialog
        mapFragment mapFragment = new mapFragment();
        mapFragment.setStyle(DialogFragment.STYLE_NO_TITLE,0);
        mapFragment.show(getSupportFragmentManager(),"mapFragment");
    }

    // Fill Rent from DB
    public void FillRentFromDB(){
        // retrieve data from SingleRent activity
        rentPosts = (ArrayList<RentPosts>) getIntent().getSerializableExtra("rentPosts");
        int pos = getIntent().getIntExtra("pos",0);
        // Set all field from DB
        place.setSelection(rentPosts.get(pos).getPlace_index()); // get spinner by index
        // rentPosts.get(pos).getPlace();
        new_rent_price.setText(rentPosts.get(pos).getNew_rent_price());
        new_rent_state.setText(rentPosts.get(pos).getNew_rent_state());
        new_rent_type.setText(rentPosts.get(pos).getNew_rent_type());
        new_rent_room_numbers.setText(rentPosts.get(pos).getNew_rent_room_numbers());
        new_rent_desc.setText(rentPosts.get(pos).getRent_desc());
        new_rent_latitude.setText(rentPosts.get(pos).getLatitude());
        new_rent_longitude.setText(rentPosts.get(pos).getLongitude());
        // -------------------------------------------------------------------------------- //
    }

    // update rent
    private void updateRentData() {
        Log.e(TAG, "updateRentData: " + rent_id );
        if(!TextUtils.isEmpty(placeText)){
            updateMap.put("place",placeText);
            Log.e("onClick", placeText);
        }
        updateMap.put("place_index",placeIndex);
        updateMap.put("user_id",user_id);
        updateMap.put("new_rent_price",new_rent_price.getText().toString());
        updateMap.put("new_rent_state",new_rent_state.getText().toString());
        updateMap.put("new_rent_type",new_rent_type.getText().toString());
        updateMap.put("new_rent_room_numbers",new_rent_room_numbers.getText().toString());
        updateMap.put("rent_desc",new_rent_desc.getText().toString());
        updateMap.put("daily_or_monthly",daily_or_monthly_text);
        updateMap.put("latitude",new_rent_latitude.getText().toString());
        updateMap.put("longitude",new_rent_longitude.getText().toString());
        updateMap.put("user_set_rent_image",String.valueOf(user_set_rent_image));
        Log.e("updateRentData", "" + rent_image_uri );
        if(user_set_rent_image || rent_image_uri != null){
            updateMap.put("user_set_rent_image","true");
        } else {
            updateMap.put("user_set_rent_image","false");
        }

        updateMap.put("time",rentPosts.get(pos).getTime());
        // image name is by date
        String imageTimeName = rentPosts.get(pos).getTime();
        getUserID();
        Log.e("imageTimeName", "updateRentData: "+ imageTimeName);
        Log.e("updateRentData", "updateRentData: "+rent_id );
        firebaseFirestore.collection("Rents").document(rent_id).update(updateMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("rent_id update",rent_id);
                        Log.e("updated","true");
                        functions.custom_toast(getString(R.string.data_updated));
                        new_rent_btn.revertAnimation();
                        functions.goToActivityByParam(MainActivity.class);
                    }
                });

    }
    // ------------------------------------------------------------------------------------- //

    // fragment to get location of rent
    public static class mapFragment extends DialogFragment implements OnMapReadyCallback {
        private View mView;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            mView = inflater.inflate(R.layout.fragment_map, container, false);
            return mView;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            // to get map from map view in layout
            MapView mapView = mView.findViewById(R.id.mapFragment);
            if(mapView != null){
                mapView.onCreate(savedInstanceState);
                mapView.onResume();
                mapView.getMapAsync(this);
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {

            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); // change map type
            // Take current LatLng when user long press on mappostNewRent
            // Check if user select latitude and longitude
            // Add Marker to map
            LatLng lastLocationSelected = new LatLng(28.386641,17.768575);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocationSelected,5));
            // -------------------------------------------------------------------- //
            googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() { // Long click from user
                @Override
                public void onMapLongClick(LatLng latLng) {
                    // -------------------------------------------------------------------- //
                    new_rent_latitude.setText(String.format("%s", latLng.latitude));
                    new_rent_longitude.setText(String.format("%s", latLng.longitude));
                    dismiss();

                }
            });
            // -------------------------------------------------------------------------------- //
        }
    }
}
