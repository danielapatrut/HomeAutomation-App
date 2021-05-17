package com.example.homeautomation_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    TextView CO2_text;
    TextView DHT11_text;
    TextView Gas_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CO2_text = (TextView) findViewById(R.id.CO2_text);
        DHT11_text = (TextView) findViewById(R.id.DHT11_text);
        Gas_text = (TextView) findViewById(R.id.Gas_text);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference co2_ref = database.getReference("CO2_sensor");
        DatabaseReference dht11_ref = database.getReference("DHT11_sensor");
        DatabaseReference gas_ref = database.getReference("Gas_sensor");

        // Read from the database
        co2_ref.addValueEventListener(new ValueEventListener() {
            private static final String TAG = "";

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                CO2_text.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                CO2_text.setText("Failed to read value." + error.toException());
            }
        });

        dht11_ref.addValueEventListener(new ValueEventListener() {
            private static final String TAG = "";

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                DHT11_text.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                DHT11_text.setText("Failed to read value." + error.toException());
            }
        });

        gas_ref.addValueEventListener(new ValueEventListener() {
            private static final String TAG = "";

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Gas_text.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Gas_text.setText("Failed to read value." + error.toException());
            }
        });
    }
}