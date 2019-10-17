package com.technowd.ejar;


import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "mainActivity";
    Toolbar main_tool_bar;
    private Functions functions = new Functions(MainActivity.this);
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private List<RentPosts> rentPosts;
    RentRecyclerAdapter rentRecyclerAdapter;
    FirebaseUser user;
    private ImageView wifi_off;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_tool_bar = findViewById(R.id.main_tool_bar);
        setSupportActionBar(main_tool_bar);
        rentPosts = new ArrayList<>();
        // Recycler view
        RecyclerView rentRecyclerView = findViewById(R.id.rentRecyclerView);
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
        String searchText = "إختر المدينة";
        Log.e("searchText", "onCreate: "+ searchText);
    }

    public void loadData(){
        Query firstQuery = firebaseFirestore.collection("Rents").orderBy("time", Query.Direction.DESCENDING);
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

    // Add new rent
    public void addNewRent(View view) {
        if(user != null){
            functions.goToActivityByParam(NewRentActivity.class);
        } else {
            functions.goToActivityByParam(LoginActivity.class);
        }
    }
}
