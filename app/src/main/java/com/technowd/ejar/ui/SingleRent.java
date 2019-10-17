package com.technowd.ejar.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.technowd.ejar.MainActivity;
import com.technowd.ejar.R;
import com.technowd.ejar.general.Functions;
import com.technowd.ejar.model.RentPosts;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SingleRent extends AppCompatActivity {
    private Functions functions = new Functions(SingleRent.this);
    // declare all variables in class
    private CircleImageView single_user_image;
    private ImageView single_main_image;
    private TextView single_rent_user,single_rent_date,single_place,single_price,single_rent_building_type,single_room_number,
            single_rent_state,single_renter_phone,single_rent_desc,daily_or_monthly,vacant_state;
    private String Latitude,Longitude;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser user;
    private StorageReference storageReference;
    private ArrayList<RentPosts> rentPosts;
    int pos;
    private String user_id;
    RecyclerView rentRecyclerView;
    private String rent_id;
    private boolean is_vacant;
    // ----------------------------------------------------------------------------------- //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_rent);
        rentRecyclerView = findViewById(R.id.rentRecyclerView);
        getAllElements(); // get all elements by id
        // Recieve data from another intent as serializable
        rentPosts =  (ArrayList<RentPosts>) getIntent().getSerializableExtra("rentPosts");
        // take position of element
        pos = getIntent().getIntExtra("position",0);
        rent_id = rentPosts.get(pos).getDocumentId();
        user_id = rentPosts.get(pos).getUser_id(); // user ID
        // ------------------------------------------------------------------------------- //
        // firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        // ---------------------------------------------------------------------------- //
        // set User data
        getUserData();
        // ------------------------------------------------------------------------ //
        // set all data come from firebase
        setRentData();
        // ------------------------------------------------------------------------------- //
        // functions.custom_toast(getIntent().getStringExtra("desc"));
        Toolbar singleToolbar = findViewById(R.id.singleToolbar);
        setSupportActionBar(singleToolbar);
        // -------------------------------
    }

    // get user data
    private void getUserData() {
        // set User data
        firebaseFirestore.collection("Users").document(rentPosts.get(pos).getUser_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                single_rent_user.setText(documentSnapshot.getString("user_name"));
                single_renter_phone.setText(String.format("00218%s", documentSnapshot.getString("user_phone")));
            }
        });
        // take user image
        storageReference.child("profile_images/" + rentPosts.get(pos).getUser_id() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(single_user_image);
            }
        });
    }
    // set all data come from firebase
    private void setRentData() {
        // set all data come from firebase
        Latitude = rentPosts.get(pos).getLatitude();
        daily_or_monthly.setText(rentPosts.get(pos).getDaily_or_monthly());
        Longitude = rentPosts.get(pos).getLongitude();
        single_rent_date.setText(rentPosts.get(pos).getTime());
        single_place.setText(rentPosts.get(pos).getPlace());
        String TAG = "singleActivity";
        Log.e(TAG, "place single: " + rentPosts.get(pos).getPlace() );
        single_price.setText(rentPosts.get(pos).getNew_rent_price());
        single_rent_building_type.setText(rentPosts.get(pos).getNew_rent_type());
        single_room_number.setText(rentPosts.get(pos).getNew_rent_room_numbers());
        single_rent_state.setText(rentPosts.get(pos).getNew_rent_state());
        single_rent_desc.setText(rentPosts.get(pos).getRent_desc());
        if(rentPosts.get(pos).isVacant()){
            vacant_state.setText("شاغر");
        } else {
            vacant_state.setText("غير شاغر");
        }
        // set main image depends on user_id and date
        String user_set_rent_image = rentPosts.get(pos).getUser_set_rent_image();
        if(user_set_rent_image.equals("true")){  // check if user put image or not

            // take post image by id and date
            storageReference.child("rent_images/"+ user_id +"/" + rentPosts.get(pos).getTime() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>(){
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(single_main_image); // user main rent image
                }
            });
        } else {
            single_main_image.setVisibility(View.GONE);
        }
    }

    // Create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // check if user login ID == user id in post
        if(user != null && user.getUid().equals(user_id)){
                inflater.inflate(R.menu.rent_menu, menu);
        }else {
            inflater.inflate(R.menu.back, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }
    // on user select option menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.switch_menu:
                isVacant();
                return true;
            case R.id.edit_rent :
                functions.goToActivityByParam(NewRentActivity.class);
                Intent intent = new Intent(this, NewRentActivity.class);
                intent.putExtra("rentPosts",rentPosts); // sent to NewRentActivity
                intent.putExtra("pos",pos); // send position
                intent.putExtra("comeFromUser",true); // send position
                startActivity(intent);
                return true;
            case R.id.delete_rent :
                deleteDialog();
                return true;
            case R.id.back :
                functions.goToActivityByParam(MainActivity.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    // delete rent and image rent from DB
    private void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_u_sure_to_delete)).setCancelable(false)
                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                      Log.e("deleteDialog", rent_id);
                                      // if there is an image
                                      if(rentPosts.get(pos).getUser_set_rent_image().equals("true")) {
                                          firebaseFirestore.collection("Rents")
                                                  .document(rent_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                  @Override
                                                  public void onSuccess(Void aVoid) {
                                                      storageReference.child("rent_images/" + user_id + "/" + single_rent_date.getText() + ".jpg").delete()
                                                              .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                  @Override
                                                                  public void onSuccess(Void aVoid) {
                                                                      Log.e("rent_data_image", "rent_images/" + user_id + "/" + single_rent_date.getText() + ".jpg");
                                                                      functions.goToActivityByParam(MainActivity.class);
                                                                  }
                                                              });
                                                  }
                                              });
                                      } else {
                                          firebaseFirestore.collection("Rents")
                                                  .document(rent_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                              @Override
                                              public void onSuccess(Void aVoid) {
                                                  functions.goToActivityByParam(MainActivity.class);
                                              }
                                          });
                                      }
                            }
                        }).setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Log.e("deleteDialog", rent_id);
                                dialogInterface.dismiss();
                            }
                        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Show rent on map
    public void showRentInaMap(View view) {
        Intent googleMap = new Intent(Intent.ACTION_VIEW);
        // get user location on map and make marker on it
        googleMap.setData(Uri.parse("geo:0,0?q=" + Latitude+","+Longitude+ "(" +single_place.getText().toString() + ")"));
        googleMap.setPackage("com.google.android.apps.maps");
        if (googleMap.resolveActivity(getPackageManager()) != null) {
            startActivity(googleMap);
        }
    }
    // Get all elements by id
    public void getAllElements(){
        // Text Views
        daily_or_monthly = findViewById(R.id.single_daily_or_monthly);
        single_rent_user = findViewById(R.id.single_rent_user);
        single_rent_date = findViewById(R.id.single_rent_date);
        single_place = findViewById(R.id.single_place);
        single_price = findViewById(R.id.single_price);
        single_rent_building_type = findViewById(R.id.single_rent_building_type);
        single_room_number = findViewById(R.id.single_room_number);
        single_rent_state = findViewById(R.id.single_rent_state);
        single_renter_phone = findViewById(R.id.single_renter_phone);
        single_rent_desc = findViewById(R.id.single_rent_desc);
        // ----------------------------------------------------- //
        // CircleImageView
        single_user_image = findViewById(R.id.single_user_image);
        // ----------------------------------------------------- //
        // Image View
        single_main_image = findViewById(R.id.single_main_image);
        vacant_state = findViewById(R.id.vacant_state);
        // ---------------------------------------------------------- //

    }

    // dail phone
    public void dialPhone(View view) {
        Intent dial = new Intent(Intent.ACTION_DIAL);
        dial.setData(Uri.parse("tel:" + single_renter_phone.getText().toString()));
        if(dial.resolveActivity(getPackageManager()) != null){
            startActivity(dial);
        }
    }

    // custom alert that used to filter results in recycler view
    private void isVacant() {
        rentPosts.clear();
        // ------------------------------------ //
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this,R.style.AlertDialogCustom);
        LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.is_vacant,null);
        Switch isVacant = view.findViewById(R.id.isVacant);
        builder.setView(view);

        isVacant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                is_vacant = b;
            }
        });
        builder.setPositiveButton("تحديث", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                firebaseFirestore.collection("Rents").document(rent_id).update("isVacant",is_vacant)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if(is_vacant){
                                    functions.custom_toast("الإيجار شاغر");
                                } else {
                                    functions.custom_toast("الإيجار غير شاغر");
                                }
                            }
                        });
            }
        });builder.setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        androidx.appcompat.app.AlertDialog dialog1 = builder.create();
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fc890d")));
        dialog1.show();
    }
}
