package com.technowd.ejar.ui;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.technowd.ejar.MainActivity;
import com.technowd.ejar.R;
import com.technowd.ejar.general.Functions;
import com.technowd.ejar.model.RentPosts;
import com.technowd.ejar.model.RentRecyclerAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class UserRentsActivity extends AppCompatActivity {
    private static final String TAG = "UserRentsActivity";
    private RentRecyclerAdapter rentRecyclerAdapter;
    private List<RentPosts> rentPosts;
    private Functions functions = new Functions(UserRentsActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_rents);
        // set tool bar
        Toolbar user_rent_toolbar = findViewById(R.id.user_rent_toolbar);
        setSupportActionBar(user_rent_toolbar);
        // ------------------------------------------------------------------------- //
        // firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance(); // take instance from firebase authentication
        FirebaseUser user = mAuth.getCurrentUser(); // get current user
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance(); // get current user
        // ------------------------------------------------------------------------- //
        rentPosts = new ArrayList<>(); // array list from rent post model
        RecyclerView userRentRecyclerView = findViewById(R.id.userRentRecyclerView); // get recycler view
        rentRecyclerAdapter = new RentRecyclerAdapter(this,rentPosts); // take instance from RentRecyclerAdapter class
        userRentRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // set recycler view as linear layout
        userRentRecyclerView.setAdapter(rentRecyclerAdapter); // set Adapter
        // ------------------------------------------------------------------------- //
        if(functions.CheckInternet()){
            assert user != null;
            Query firstQuery = firebaseFirestore.collection("Rents").whereEqualTo("user_id", user.getUid());
            // get data from DB depends on time desc
            firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    assert queryDocumentSnapshots != null;
                    if(!queryDocumentSnapshots.isEmpty()){ // check if data not empty
                        for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                            if(doc.getType() == DocumentChange.Type.ADDED){
                                RentPosts localrentPosts = doc.getDocument().toObject(RentPosts.class);
                                localrentPosts.setDocumentId(doc.getDocument().getId());
                                rentPosts.add(localrentPosts);
                                rentRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

            });
        }

        // --------------------------------------------------------------------------- //
        rentRecyclerAdapter.setOnItemClickListener(new RentRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.e(TAG, "onItemClick: " + position );

                final Intent intent = new Intent(UserRentsActivity.this, SingleRent.class);
                intent.putExtra("position",position);
                intent.putExtra("rentPosts", (Serializable) rentPosts);

                startActivity(intent);
            }
        });
        // ---------------------------------------------------------------------- //
    }


    // Create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.back, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // on user select option menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.back) {
            functions.goToActivityByParam(MainActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
