package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BikesImageShowOwnSharedBikesToUpdate extends AppCompatActivity {

    private DatabaseReference databaseRefShare;
    private FirebaseStorage bikesStorageShare;

    private DatabaseReference databaseRefDeleteBike;
    private FirebaseStorage bikesStorageDelete;

    private ValueEventListener shareBikesEventListener;

    private RecyclerView bikesListRecyclerView;
    private BikesAdapterShowOwnSharedBikesToUpdate bikesAdapterShowOwnSharedBikesToUpdate;

    private List<ShareBikes> upShareBikesList;

//    String customShareFirst_Name = "";
//    String customShareLast_Name = "";
    String customShare_Id = "";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bikes_image_show_own_shared_bikes_to_update);

        getIntent().hasExtra("CIdUpdate");
        customShare_Id = Objects.requireNonNull(getIntent().getExtras()).getString("CIdUpdate");

        bikesListRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikesListRecyclerView.setHasFixedSize(true);
        bikesListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        upShareBikesList = new ArrayList<>();

        progressDialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        loadBikeSharedToUpdateCustomer();
    }

    private void loadBikeSharedToUpdateCustomer() {
        //initialize the bike storage database
        bikesStorageShare = FirebaseStorage.getInstance();
        databaseRefShare = FirebaseDatabase.getInstance().getReference("Share Bikes");

        shareBikesEventListener = databaseRefShare.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                upShareBikesList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ShareBikes share_Bikes = postSnapshot.getValue(ShareBikes.class);

                    assert share_Bikes != null;
                    if (share_Bikes.getShareBikes_CustomId().equals(customShare_Id)) {
                        share_Bikes.setShareBike_Key(postSnapshot.getKey());
                        upShareBikesList.add(share_Bikes);
                    }
                }
                bikesAdapterShowOwnSharedBikesToUpdate = new BikesAdapterShowOwnSharedBikesToUpdate(BikesImageShowOwnSharedBikesToUpdate.this, upShareBikesList);
                bikesListRecyclerView.setAdapter(bikesAdapterShowOwnSharedBikesToUpdate);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikesImageShowOwnSharedBikesToUpdate.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}