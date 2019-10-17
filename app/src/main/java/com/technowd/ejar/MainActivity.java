package com.technowd.ejar;


import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.technowd.ejar.general.Functions;
import com.technowd.ejar.model.RentPosts;
import com.technowd.ejar.model.RentRecyclerAdapter;
import com.technowd.ejar.ui.AboutActivity;
import com.technowd.ejar.ui.LoginActivity;
import com.technowd.ejar.ui.NewRentActivity;
import com.technowd.ejar.ui.SearchActivity;
import com.technowd.ejar.ui.SetUpActivity;
import com.technowd.ejar.ui.SingleRent;
import com.technowd.ejar.ui.UserRentsActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "mainActivity";
    Toolbar main_tool_bar;
    private Functions functions = new Functions(MainActivity.this);
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    // Recycler view
    private RecyclerView rentRecyclerView;
    private List<RentPosts> rentPosts;
    private DocumentSnapshot lastVisible;
    RentRecyclerAdapter rentRecyclerAdapter;
    FirebaseUser user;
    private ImageView wifi_off;
    private Boolean isFirstQueryLoaded = true;
    private String searchText = "إختر المدينة";
    private Query firstQuery;
    private String searchBy = "none";
    private String user_name,user_phone;
    private Uri user_image;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Log.e("serverTimestamp", "" +FieldValue.serverTimestamp()  );
        main_tool_bar = findViewById(R.id.main_tool_bar);
        setSupportActionBar(main_tool_bar);
        rentPosts = new ArrayList<>();
        rentRecyclerView = findViewById(R.id.rentRecyclerView);
        wifi_off = findViewById(R.id.wifi_off);
        // init firebase auth
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser(); // get Current user
        rentRecyclerAdapter = new RentRecyclerAdapter(this,rentPosts);
        rentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rentRecyclerView.setAdapter(rentRecyclerAdapter);
        rentRecyclerAdapter.setOnItemClickListener(new RentRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.e(TAG, "onItemClick: " + position );
            }
        });
        // Recycler view scrolled
        rentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Boolean reachedButton = !recyclerView.canScrollVertically(1);
                if(reachedButton){
                    // String desc = lastVisible.getString("rent_desc");
                     // loadMoreRents();
                    // functions.custom_toast(desc);
                }
            }
        });
        firebaseFirestore = FirebaseFirestore.getInstance();
        // --------------------------------------------------------------------------- //
        rentRecyclerAdapter.setOnItemClickListener(new RentRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.e(TAG, "onItemClick: " + position );

                final Intent intent = new Intent(MainActivity.this, SingleRent.class);
                intent.putExtra("position",position);
                intent.putExtra("rentPosts", (Serializable) rentPosts);

                startActivity(intent);
            }
        });
        // ---------------------------------------------------------------------- //
        //searchBy(searchBy);
        loadData();
        Log.e("searchText", "onCreate: "+ searchText );
    }

    public void loadData(){
        firstQuery = firebaseFirestore.collection("Rents").orderBy("time",Query.Direction.DESCENDING);
        if(functions.CheckInternet()){ // check internet
            wifi_off.setVisibility(View.INVISIBLE);
            // get data from DB depends on time desc

            firstQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                        RentPosts localrentPosts = doc.toObject(RentPosts.class);
                        localrentPosts.setDocumentId(doc.getId());
                        rentPosts.add(localrentPosts);
                        rentRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            });
        } else {
            functions.custom_toast(getString(R.string.network_problem));
            wifi_off.setVisibility(View.VISIBLE);
        }
    }


    // load 3 by 3 rents
    public void loadMoreRents(){ //
        Query nextQuery = null;

            nextQuery = firebaseFirestore.collection("Rents").orderBy("time",Query.Direction.DESCENDING)
                .startAfter(lastVisible).limit(3);
        if(functions.CheckInternet()){
            nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    if(!queryDocumentSnapshots.isEmpty()) { // check if data not empty
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1); // take all data in DB
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                RentPosts localrentPosts = doc.getDocument().toObject(RentPosts.class);
                                rentPosts.add(localrentPosts);
                                rentRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });
        }
    }
    // on start method check if user login then write his name in toolbar
    @Override
    protected void onStart() {
        super.onStart();
        user = mAuth.getCurrentUser();
        if(user != null){
            String name = user.getDisplayName();
            Objects.requireNonNull(getSupportActionBar()).setTitle(name);
        }
    }

    // Create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(user != null){ // check if user login use main mneu
            inflater.inflate(R.menu.main, menu);
        } else { // if user not log in use guest_menu
            inflater.inflate(R.menu.guest_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }
    // do event when user press item from menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.publish_rent:
                functions.goToActivityByParam(NewRentActivity.class);
                return true;
            case R.id.setting:
                functions.goToActivityByParam(SetUpActivity.class);
                return true;
            case R.id.login_menu:
                functions.goToActivityByParam(LoginActivity.class);
                return true;
            case R.id.search_rent_menu:
                functions.goToActivityByParam(SearchActivity.class);
                return true;
            case R.id.log_out:
                mAuth.signOut(); // Log out from app
                functions.goToActivityByParam(MainActivity.class);
                return true;
            case R.id.current_user_add:
                functions.goToActivityByParam(UserRentsActivity.class);
                return true;
            case R.id.about_app:
                functions.goToActivityByParam(AboutActivity.class);
                return true;
            case R.id.close_app:
                System.exit(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // to check if user login or not
    private boolean checkUser(){
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null;
    }
    // Add new rent
    public void addNewRent(View view) {
        if(user != null){
            functions.goToActivityByParam(NewRentActivity.class);
        } else {
            functions.goToActivityByParam(LoginActivity.class);
        }
    }
    // change language of android os
    public void setLocal(String lang){
        lang = "ar";
        Locale locale = new Locale(lang);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);
        getResources().updateConfiguration(config,displayMetrics);
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
    }
}
