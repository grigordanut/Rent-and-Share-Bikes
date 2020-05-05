package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomerPageShareBikes extends AppCompatActivity {

    //Declaring some objects
    private DrawerLayout drawerLayoutUserShare;
    private ActionBarDrawerToggle drawerToggleUserShare;
    private NavigationView navigationViewUserShare;

    //Display data from Rent Bikes table database
    private FirebaseStorage firebaseStBikesShareCustom;
    private DatabaseReference databaseRefBikesShareCustom;
    private ValueEventListener bikesShareCustomEventListener;

    //Access customer database
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    private TextView tVCustomPageShare, tVCustomPageSharePerDetails, tVCustomerBikesShared;

    private List<ShareBikes> bikesListShareCustom;
    private int numberBikesSharedCustom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_page_share_bikes);

        bikesListShareCustom = new ArrayList<>();

        //initialise the variables
        tVCustomPageShare = (TextView) findViewById(R.id.tvCustomPageShare);
        tVCustomPageSharePerDetails = (TextView)findViewById(R.id.tvCustomPageSharePerDetails);
        tVCustomerBikesShared = (TextView)findViewById(R.id.tvCustomerBikesShared);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        drawerLayoutUserShare = findViewById(R.id.activity_customer_page_share_bikes);
        drawerToggleUserShare = new ActionBarDrawerToggle(this, drawerLayoutUserShare, R.string.open_customPageShare, R.string.close_customPageShare);

        drawerLayoutUserShare.addDrawerListener(drawerToggleUserShare);
        drawerToggleUserShare.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationViewUserShare = findViewById(R.id.navViewCustomShare);

        //retrieve data from database into text views
        databaseReference = FirebaseDatabase.getInstance().getReference("Customers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "NewApi"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //retrieve data from database
                for (final DataSnapshot dsUser : dataSnapshot.getChildren()) {
                    final FirebaseUser custom_Details = firebaseAuth.getCurrentUser();

                    final Customers custom_data = dsUser.getValue(Customers.class);

                    assert custom_Details != null;
                    assert custom_data != null;
                    if (Objects.requireNonNull(custom_Details.getEmail()).equalsIgnoreCase(custom_data.getEmail_Customer())) {
                        tVCustomPageShare.setText("Welcome: " + custom_data.getfName_Customer() + " " + custom_data.getlName_Customer());
                        tVCustomPageSharePerDetails.setText("Phone: \n"+custom_data.getPhoneNumb_Customer()+"\n\nEmail: \n"+custom_data.getEmail_Customer());
                        //Adding Click Events to our navigation drawer item
                        navigationViewUserShare.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                            @Override
                            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                int id = item.getItemId();
                                switch (id) {
                                    //Activity of adding Bikes to be shared
                                    case R.id.userAdd_shareBikes:
                                        Toast.makeText(CustomerPageShareBikes.this, "Add bikes to share", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(CustomerPageShareBikes.this, ShareBikesCustomer.class));
                                        break;
                                    //Show the customer's own bikes added available to be shared
                                    case R.id.userShow_ownShareBikes:
                                        Toast.makeText(CustomerPageShareBikes.this, "Show Bikes Shared", Toast.LENGTH_SHORT).show();
                                        Intent intentShowBikes = new Intent(CustomerPageShareBikes.this, BikesImageShowSharedBikesOwner.class);
                                        intentShowBikes.putExtra("CFNameShare", custom_data.getfName_Customer());
                                        intentShowBikes.putExtra("CLNameShare", custom_data.getlName_Customer());
                                        intentShowBikes.putExtra("CIdShare", currentUser.getUid());
                                        startActivity(intentShowBikes);
                                        break;
                                    case R.id.userShow_shareBikesAvailable:
                                        Toast.makeText(CustomerPageShareBikes.this, "Share Bikes Available", Toast.LENGTH_SHORT).show();
                                        Intent intentNoOwnerBikes = new Intent(CustomerPageShareBikes.this, BikesImageShowSharedBikesNoOwner.class);
                                        intentNoOwnerBikes.putExtra("CIdNoOwner", currentUser.getUid());
                                        startActivity(intentNoOwnerBikes);
                                        break;
                                    case R.id.userUpdate_ownShareBikes:
                                        Toast.makeText(CustomerPageShareBikes.this, "Update my Bike", Toast.LENGTH_SHORT).show();
                                        Intent intentUpdate = new Intent(CustomerPageShareBikes.this, BikesImageShowSharedBikesToUpdate.class);
                                        intentUpdate.putExtra("CIdUpdate", currentUser.getUid());
                                        startActivity(intentUpdate);
                                        break;

                                    case R.id.userRemove_ownShareBikes:
                                        Toast.makeText(CustomerPageShareBikes.this, "Remove shared Bikes", Toast.LENGTH_SHORT).show();
                                        Intent intentRemoveBikes = new Intent(CustomerPageShareBikes.this, BikesImageRemoveSharedBikesOwner.class);
                                        intentRemoveBikes.putExtra("CIdRemove", currentUser.getUid());
                                        startActivity(intentRemoveBikes);
                                        break;
                                    default:
                                        return true;
                                }
                                return true;
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerPageShareBikes.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goBackShare() {
        finish();
        startActivity(new Intent(CustomerPageShareBikes.this, CustomerPageMain.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer_share_bikes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (drawerToggleUserShare.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.userShareGoBack: {
                goBackShare();
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();
        loadBikeShareCustom();
    }

    private void loadBikeShareCustom() {
        //initialize the bike storage database
        firebaseStBikesShareCustom = FirebaseStorage.getInstance();
        databaseRefBikesShareCustom = FirebaseDatabase.getInstance().getReference("Share Bikes");

        bikesShareCustomEventListener = databaseRefBikesShareCustom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bikesListShareCustom.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ShareBikes share_Bikes = postSnapshot.getValue(ShareBikes.class);
                    assert share_Bikes != null;
                    if (share_Bikes.getShareBikes_CustomId().equals(currentUser.getUid())){
                        share_Bikes.setShareBike_Key(postSnapshot.getKey());
                        bikesListShareCustom.add(share_Bikes);
                        numberBikesSharedCustom = bikesListShareCustom.size();
                        tVCustomerBikesShared.setText(String.valueOf(numberBikesSharedCustom));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerPageShareBikes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
