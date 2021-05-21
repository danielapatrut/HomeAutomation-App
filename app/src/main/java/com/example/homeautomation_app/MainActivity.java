package com.example.homeautomation_app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    TextView AirQuality_text;
    TextView Humidity_text;
    TextView Temperature_text;
    TextView LPG_text;
    TextView CO_text;
    TextView Smoke_text;
    ProgressBar progressBar;
    ProgressBar progressBar2;
    Button alarmButton;
    Boolean isAlarmOn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AirQuality_text = (TextView) findViewById(R.id.AirQuality_text);
        Humidity_text = (TextView) findViewById(R.id.Humidity_text);
        Temperature_text = (TextView) findViewById(R.id.Temperature_text);
        LPG_text = (TextView) findViewById(R.id.LPG_text);
        CO_text = (TextView) findViewById(R.id.CO_text);
        Smoke_text = (TextView) findViewById(R.id.Smoke_text);
        alarmButton = (Button) findViewById(R.id.alarmButton);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference co2_ref = database.getReference("CO2_sensor");
        DatabaseReference dht11_ref = database.getReference("DHT11_sensor");
        DatabaseReference gas_ref = database.getReference("Gas_sensor");
        DatabaseReference alarm_ref = database.getReference("motionAlarm");
        DatabaseReference pir_ref = database.getReference("PIR_sensor");

        // Read from the database
        pir_ref.addValueEventListener(new ValueEventListener() {
            private static final String TAG = "";

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                int motion = parseInt(value.substring(0,1));
                if (isAlarmOn && motion==1)
                {
                    sendNotification("There was some movement detected!", "Movement detected", 3);
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });


        alarm_ref.addValueEventListener(new ValueEventListener() {
            private static final String TAG = "";

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Boolean value = dataSnapshot.getValue(Boolean.class);
                if (value==true)
                {
                    alarmButton.setText("MOTION ALARM OFF");
                }
                if (value==false)
                {
                    alarmButton.setText("MOTION ALARM ON");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                alarmButton.setText("Failed to read value." + error.toException());
            }
        });

        co2_ref.addValueEventListener(new ValueEventListener() {
            private static final String TAG = "";

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                String airquality_string_value = "Air Quality: ";
                airquality_string_value += value;
                AirQuality_text.setText(airquality_string_value);
                progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
                progressBar2.setMax(100);
                switch(value){
                    case "VERY GOOD\n": progressBar2.setProgress(100);
                        progressBar2.getProgressDrawable().setColorFilter(
                                Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
                        break;
                    case "GOOD\n": progressBar2.setProgress(75);
                        progressBar2.getProgressDrawable().setColorFilter(
                                Color.rgb(255,204,51), android.graphics.PorterDuff.Mode.SRC_IN);
                        break;
                    case "PRETTY BAD\n": progressBar2.setProgress(50);
                        progressBar2.getProgressDrawable().setColorFilter(
                                Color.rgb(255,102,0), android.graphics.PorterDuff.Mode.SRC_IN);
                        break;
                    case "BAD\n": progressBar2.setProgress(25);
                        progressBar2.getProgressDrawable().setColorFilter(
                                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                        break;
                    default: break;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                AirQuality_text.setText("Failed to read value." + error.toException());
            }
        });

        dht11_ref.addValueEventListener(new ValueEventListener() {
            private static final String TAG = "";

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);

                int hIndex = 0, dotIndex = 0;
                String temperature_string_value, humidity_string_value;
                float temperature_actual_value;
                int humidity_actual_value;

                for(int i = 0; i < value.length(); i++){
                    if (value.charAt(i) == '.') {
                        dotIndex = i;
                        break;
                    }
                }
                temperature_string_value = value.substring(4, dotIndex + 2);
                temperature_string_value += "°C";
                temperature_actual_value = parseFloat(value.substring(4, dotIndex + 2));

                for(int i = 0; i < value.length(); i++){
                    if(value.charAt(i) == 'H') hIndex = i;
                    if(value.charAt(i) == '.') dotIndex = i;
                }
                humidity_string_value = value.substring(hIndex + 4, dotIndex);
                humidity_string_value += "%";
                humidity_actual_value = parseInt(value.substring(hIndex + 4, dotIndex));

                Humidity_text.setText(humidity_string_value);
                Temperature_text.setText(temperature_string_value);

                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setMax(100);
                progressBar.setProgress(humidity_actual_value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Humidity_text.setText("Failed to read value." + error.toException());
                Temperature_text.setText("Failed to read value." + error.toException());
            }
        });

        gas_ref.addValueEventListener(new ValueEventListener() {
            private static final String TAG = "";

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);

                int lpg_actual_value, co_actual_value, smoke_actual_value;
                String lpg_string_value = "LPG: ", co_string_value = "CO: ", smoke_string_value = "Smoke: ";
                int virgulaIndex = 0, douapctIndex = 0, cIndex = 0;

                for(int i = 0; i < value.length(); i++){
                    if (value.charAt(i) == ',') {
                        virgulaIndex = i;
                        break;
                    }
                }
                lpg_string_value += value.substring(6, virgulaIndex);
                lpg_actual_value = parseInt(value.substring(6, virgulaIndex));

                for(int i = 0; i < value.length(); i++){
                    if(value.charAt(i) == 'C') cIndex = i;
                    if(value.charAt(i) == ',') virgulaIndex = i;
                }
                co_string_value += value.substring(cIndex + 5, virgulaIndex);
                co_actual_value = parseInt(value.substring(cIndex + 5, virgulaIndex));

                for(int i = 0; i < value.length(); i++){
                    if(value.charAt(i) == ':') douapctIndex = i;
                }
                smoke_string_value += value.substring(douapctIndex + 2, value.length() - 1);
                smoke_actual_value = parseInt(value.substring(douapctIndex + 2, value.length() - 1));

                LPG_text.setText(lpg_string_value);
                CO_text.setText(co_string_value);
                Smoke_text.setText(smoke_string_value);
                if (smoke_actual_value>5)
                {
                    sendNotification("Smoke level too high!", "Smoke detected!", 0);

                }

                if (lpg_actual_value>5)
                {
                    sendNotification("LPG level too high!", "Dangerous LPG", 1);
                }

                if (co_actual_value>5)
                {
                    sendNotification("CO level too high!", "Dangerous CO level", 2);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                LPG_text.setText("Failed to read value." + error.toException());
                CO_text.setText("Failed to read value." + error.toException());
                Smoke_text.setText("Failed to read value." + error.toException());
            }
        });

        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAlarmOn==true)
                {
                    isAlarmOn=false;
                    alarmButton.setText("MOTION ALARM ON");
                    alarm_ref.setValue(isAlarmOn);
                }
                else
                {
                    if (isAlarmOn==false)
                    {
                        isAlarmOn=true;
                        alarmButton.setText("MOTION ALARM OFF");
                        alarm_ref.setValue(isAlarmOn);
                    }
                }
            }
        });


    }

    public void sendNotification(String notifMessage, String notifTitle, int id)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                MainActivity.this).setSmallIcon(R.drawable.ic_danger)
                .setContentTitle(notifTitle)
                .setContentText(notifMessage)
                .setAutoCancel(true);
        Intent intent = new Intent(MainActivity.this, NotificationActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("message",notifMessage);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0,intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id,builder.build());
    }


}