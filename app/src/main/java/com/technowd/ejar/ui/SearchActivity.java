package com.technowd.ejar.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.technowd.ejar.MainActivity;
import com.technowd.ejar.R;
import com.technowd.ejar.general.Functions;
import com.technowd.ejar.model.RentPosts;
import com.technowd.ejar.model.RentRecyclerAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity {
    private Functions functions = new Functions(SearchActivity.this);
    private Spinner searchSpinner;
    private RecyclerView searchRecyclerView;
    private List<RentPosts> rentPosts;
    private RentRecyclerAdapter rentRecyclerAdapter;
    private String searchText;
    // fire base
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // toolbar
        Toolbar searchToolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(searchToolbar);
        // -------------------------------------------------------- //
        buildRecyclerView();
        // --------------------------------------------------------------------------- //
        rentRecyclerAdapter.setOnItemClickListener(new RentRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                final Intent intent = new Intent(SearchActivity.this, SingleRent.class);
                intent.putExtra("position",position);
                intent.putExtra("rentPosts", (Serializable) rentPosts);

                startActivity(intent);
            }
        });
        // ---------------------------------------------------------------------- //
    }

    private void buildRecyclerView() {
        // Recycler view
        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        rentPosts = new ArrayList<>();
        rentRecyclerAdapter = new RentRecyclerAdapter(this, rentPosts);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setAdapter(rentRecyclerAdapter);
    }

    public void searchBy(String searchText){
        Query query = firebaseFirestore.collection("Rents").whereEqualTo("place",searchText);
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                                RentPosts loacalRentPosts = doc.toObject(RentPosts.class);
                                loacalRentPosts.setDocumentId(doc.getId());
                                rentPosts.add(loacalRentPosts);
                                rentRecyclerAdapter.notifyDataSetChanged();
                            }
                        } else {
                            functions.custom_toast("لاتوجد بيانات");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        functions.custom_toast(e.getMessage());
                    }
                });

    }

    // custom alert that used to filter results in recycler view
    private void searchDialog() {
        rentPosts.clear();
        // ------------------------------------ //
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogCustom);
        LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.custom_dialog,null);
        searchSpinner = view.findViewById(R.id.search_rent);
        builder.setView(view);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.city_spinner
                , android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchSpinner.setAdapter(adapter);
        builder.setPositiveButton("فرز", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e("onClick", "onClick: "+ "clicked" );
                Log.e("onClick", searchSpinner.getSelectedItem().toString());
                if(!searchSpinner.getSelectedItem().toString().equalsIgnoreCase("إختر المدينة")){
                    searchText = searchSpinner.getSelectedItem().toString();
                    // searchBy = "city";
                    searchRecyclerView.setAdapter(rentRecyclerAdapter);
                    searchBy(searchText);
                    functions.custom_toast(searchText);
                } else {
                    functions.custom_toast("يجب إختيار مدينة");
                }

            }
        });
        builder.setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog1 = builder.create();
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fc890d")));
        dialog1.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.search_rent:
                searchDialog();
                return true;
            case R.id.about_app:
                functions.goToActivityByParam(AboutActivity.class);
                return true;
            case R.id.close_app:
                System.exit(0);
                return true;
            case R.id.back:
                functions.goToActivityByParam(MainActivity.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
