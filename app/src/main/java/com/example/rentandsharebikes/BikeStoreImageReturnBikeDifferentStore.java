package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BikeStoreImageReturnBikeDifferentStore extends AppCompatActivity implements BikeStoreAdapterReturnBikeDifferentStore.OnItemClickListener {

    private TextView textViewBikeStoresImageReturnBikeDiffStore;
    private DatabaseReference databaseReference;
    private ValueEventListener bikeStoreEventListener;

    private RecyclerView bikeStoreRecyclerView;
    private BikeStoreAdapterReturnBikeDifferentStore bikeStoreAdapterReturnBikeDifferentStore;

    public List<BikeStores> bikeStoresList;

    private ProgressDialog progressDialog;

    String bike_Image, bike_StoreNameReturnBikesSame, bike_StoreKeyReturnBikesSame, bike_KeyReturnBikeSame;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_store_image_return_bike_different_store);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            //Receive data from ReturnRentedBikes
            bike_Image = bundle.getString("BikeImage");
            bike_StoreNameReturnBikesSame = bundle.getString("BStoreNameSame");
            bike_StoreKeyReturnBikesSame = bundle.getString("BStoreKeySame");
            bike_KeyReturnBikeSame = bundle.getString("BikeRentedKey");
        }

        textViewBikeStoresImageReturnBikeDiffStore = (TextView)findViewById(R.id.tvBikeStoresReturnDiffStore);
        textViewBikeStoresImageReturnBikeDiffStore.setText("No Bike Stores available");

        bikeStoreRecyclerView = (RecyclerView) findViewById(R.id.evRecyclerView);
        bikeStoreRecyclerView.setHasFixedSize(true);
        bikeStoreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        bikeStoresList = new ArrayList<BikeStores>();

        progressDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBikeStoresListAdmin();
    }

    private void loadBikeStoresListAdmin() {
        //initialize the bike store database
        databaseReference = FirebaseDatabase.getInstance().getReference("Bike Stores");

        bikeStoreEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikeStoresList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BikeStores bikeStores = postSnapshot.getValue(BikeStores.class);
                    assert bikeStores != null;
                    bikeStores.setBikeStore_Key(postSnapshot.getKey());
                    bikeStoresList.add(bikeStores);
                    textViewBikeStoresImageReturnBikeDiffStore.setText(bikeStoresList.size()+" Bike Stores available");
                }
                bikeStoreAdapterReturnBikeDifferentStore = new BikeStoreAdapterReturnBikeDifferentStore (BikeStoreImageReturnBikeDifferentStore.this, bikeStoresList);
                bikeStoreRecyclerView.setAdapter(bikeStoreAdapterReturnBikeDifferentStore);
                bikeStoreAdapterReturnBikeDifferentStore.setOnItmClickListener(BikeStoreImageReturnBikeDifferentStore.this);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BikeStoreImageReturnBikeDifferentStore.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Action of the menu onClick
    @Override
    public void onItemClick(int position) {

        //Send data to ReturnRentedBikes
        Intent intent = new Intent(BikeStoreImageReturnBikeDifferentStore.this, ReturnRentedBikes.class);
        BikeStores selected_BikeStores = bikeStoresList.get(position);
        intent.putExtra("BStoreNameDiff", selected_BikeStores.getBikeStore_Location());
        intent.putExtra("BStoreKeyDiff", selected_BikeStores.getBikeStore_Key());

        intent.putExtra("BikeImage",bike_Image);
        intent.putExtra("BStoreNameSame",bike_StoreNameReturnBikesSame);
        intent.putExtra("BStoreKeySame",bike_StoreKeyReturnBikesSame);
        intent.putExtra("BikeRentedKey", bike_KeyReturnBikeSame);
        startActivity(intent);
    }
}
