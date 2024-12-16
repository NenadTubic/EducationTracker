package com.example.educationtracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SensorDataActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope, orientationSensor;
    private TextView tvAccelerometer, tvGyroscope, tvOrientation;
    private static final String TAG = "SensorDataActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Učitaj temu iz SharedPreferences i postavi je
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        int nightMode = sharedPreferences.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(nightMode);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data);

        Log.d(TAG, "onCreate: Activity Created");

        // Inicijalizacija elemenata
        tvAccelerometer = findViewById(R.id.tv_accelerometer);
        tvGyroscope = findViewById(R.id.tv_gyroscope);
        tvOrientation = findViewById(R.id.tv_orientation);

        // Inicijalizacija SensorManager i senzora
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }

        // Registracija senzora
        if (accelerometer != null && gyroscope != null && orientationSensor != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.e(TAG, "Senzori nisu dostupni");
            tvAccelerometer.setText("Akcelerometar nije dostupan");
            tvGyroscope.setText("Giroskop nije dostupan");
            tvOrientation.setText("Orijentacija nije dostupna");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            tvAccelerometer.setText("Akcelerometar: \nX: " + x + "\nY: " + y + "\nZ: " + z);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            tvGyroscope.setText("Giroskop: \nX: " + x + "\nY: " + y + "\nZ: " + z);
        } else if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            float azimuth = event.values[0];
            float pitch = event.values[1];
            float roll = event.values[2];
            tvOrientation.setText("Orijentacija: \nAzimut: " + azimuth + "\nNagib: " + pitch + "\nRoll: " + roll);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Neophodna metoda, ne koristi se u ovom primeru
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        Log.d(TAG, "onPause: Listener Unregistered");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null && gyroscope != null && orientationSensor != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        Log.d(TAG, "onResume: Listener Registered");
    }
}
