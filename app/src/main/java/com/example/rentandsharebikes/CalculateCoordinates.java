package com.example.rentandsharebikes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CalculateCoordinates extends AppCompatActivity {

    private EditText etStorePlace;
    private String store_Place;

    private TextView tvStoreAddress, tvLatitude, tvLongitude;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_coordinates);

        etStorePlace = findViewById(R.id.etBikeStorePlace);
        tvStoreAddress = findViewById(R.id.tvShowStoreAddress);
        tvLatitude = findViewById(R.id.tvStoreLatitude);
        tvLongitude = findViewById(R.id.tvStoreLongitude);

        progressDialog = new ProgressDialog(this);

        Button buttonClearStoreAddress = findViewById(R.id.btnClearStoreAddress);
        buttonClearStoreAddress.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                etStorePlace.setText("");
                tvStoreAddress.setText("Store Address");
                tvLatitude.setText("");
                tvLongitude.setText("");
            }
        });

        Button buttonShowCoordinates = findViewById(R.id.btnShowCoordinates);
        buttonShowCoordinates.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                store_Place = etStorePlace.getText().toString().trim();

                if (TextUtils.isEmpty(store_Place)) {
                    etStorePlace.setError("Enter store Address");
                    etStorePlace.requestFocus();
                } else {
                    progressDialog.setMessage("Calculate Coordinates");
                    progressDialog.show();
                    GeoLocation geoLocation = new GeoLocation();
                    geoLocation.getAddress(store_Place, getApplicationContext(), new GeoHandler());
                }
                progressDialog.dismiss();
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private class GeoHandler extends Handler {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            final String storeAddress, addressStoreLat, addressStoreLong;
            switch (msg.what) {
                case 1:
                    Bundle bundle = msg.getData();
                    storeAddress = bundle.getString("locationAddress");
                    addressStoreLat = bundle.getString("addressLat");
                    addressStoreLong = bundle.getString("addressLong");
                    break;
                default:
                    storeAddress = null;
                    addressStoreLat = null;
                    addressStoreLong = null;
            }
            tvStoreAddress.setText("Address: " + storeAddress);
            tvLatitude.setText(addressStoreLat);
            tvLongitude.setText(addressStoreLong);
            //etStorePlace.setText("");
            //finish();

            Button buttonSaveCoordinates = findViewById(R.id.btnSaveCoordinates);
            buttonSaveCoordinates.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CalculateCoordinates.this, AddBikeStore.class);
                    intent.putExtra("Address",storeAddress);
                    intent.putExtra("Latitude",addressStoreLat);
                    intent.putExtra("Longitude", addressStoreLong);
                    startActivity(intent);
                }
            });
        }
    }
}
