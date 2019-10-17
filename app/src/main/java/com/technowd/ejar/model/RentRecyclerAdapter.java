package com.technowd.ejar.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.technowd.ejar.R;
import com.technowd.ejar.general.Functions;

import java.util.List;

public class RentRecyclerAdapter extends RecyclerView.Adapter<RentRecyclerAdapter.ViewHolder>{
    public List<RentPosts> rentPosts;
    private Context context;
    private String rent_id;
    Functions functions = new Functions(context);
    private Intent intent;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private String desc,time,user_id,place,new_rent_price; // declare variables in all class
    private static  String TAG = "RentAdapter";
    private static OnItemClickListener clickListener;
    private OnItemClickListener mListener;
    private String userName, user_phone;
    private int pos;

    // user interface to onclick method
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener mListener){
        this.mListener = mListener;
    }
    // --------------------------------------------------- //
    public RentRecyclerAdapter(Context context,List<RentPosts> rentPosts){
        this.rentPosts = rentPosts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rent_row,parent,false);
        ViewHolder viewHolder = new ViewHolder(view,mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // get all data via position by model
        place = rentPosts.get(position).getPlace(); // get place
        holder.setPlaceText(place); // set place to view
        user_id = rentPosts.get(position).getUser_id(); // get user id as
        time = rentPosts.get(position).getTime(); // get date
        place = rentPosts.get(position).getPlace(); // get place
        new_rent_price = rentPosts.get(position).getNew_rent_price(); // get date
        holder.setRentPostDate(time); // set time
        String user_set_rent_image = rentPosts.get(position).getUser_set_rent_image();
        if(rentPosts.get(position).isVacant()){
            Log.e(TAG, "onBindViewHolder: " + rentPosts.get(position).isVacant() );
            holder.isVacantIcon.setBackgroundResource(R.drawable.is_vacant_true);

        } else {
            Log.e(TAG, "onBindViewHolder: " + rentPosts.get(position).isVacant() );
            holder.isVacantIcon.setBackgroundResource(R.drawable.is_vacant_false);
        }
        // ---------------------------------------------------------------------------------- //
        // take user data from firebase firestore
        firebaseFirestore.collection("Users").document(user_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
            if (documentSnapshot.exists()) { // check if data exists
                userName = documentSnapshot.getString("user_name");
                user_phone = documentSnapshot.getString("user_phone");
                holder.getUserNameByID(userName); // take user name by id
                // take user name image
                storageReference.child("profile_images/" + rentPosts.get(position).getUser_id() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        holder.setUserNameThatWritePost(uri);
                    }
                });
                // --------------------------------------------------------------- //
            }
            }
        });

        // --------------------------------------------------------------------------- //
        if (user_set_rent_image.equals("true")) {  // check if user put image or not
            // take post image by id and date
            storageReference.child("rent_images/" + user_id + "/" + time + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    holder.setRentImage(uri);
                }
            });
        } else {
            // if no image in post
            holder.rent_post_image.setVisibility(View.GONE);
        }
        // --------------------------------------------------------------------- //
    }

    @Override
    public int getItemCount() { // return number of items
        return rentPosts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public View mView;
        private TextView place_text, rent_post_user_name,rent_post_date;
        private String user_id;
        private ImageView rent_post_image,user_post_rent_image,isVacantIcon;
//        List<RentPosts> rentPosts;
//        Context context;
        // , Context context , List<RentPosts> rentPosts
        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mView = itemView;
            rent_post_image = mView.findViewById(R.id.rent_post_image);
            user_post_rent_image = mView.findViewById(R.id.user_post_rent_image);
            isVacantIcon = mView.findViewById(R.id.isVacantIcon);
            // get position of item by interface
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

        public void setPlaceText(String desc){
            place_text = mView.findViewById(R.id.rent_post_place);
            place_text.setText(desc);
        }
        // Set rent_post image
        public void setRentImage(Uri rentImage){
            Picasso.get().load(rentImage).into(rent_post_image); // set user_image from firebase Rent_images
        }
        // set date to row
        public void setRentPostDate(String date){
            rent_post_date = mView.findViewById(R.id.rent_post_date);
            rent_post_date.setText(date);
        }
        // set user_name to row
        public void getUserNameByID(String user_id){
            rent_post_user_name = mView.findViewById(R.id.rent_post_user_name);
            rent_post_user_name.setText(user_id);
        }
        // set user name that write rent post
        public void setUserNameThatWritePost(Uri rentImage){
            Picasso.get().load(rentImage).into(user_post_rent_image); // set user_image from firebase Rent_images
        }

    }

}
