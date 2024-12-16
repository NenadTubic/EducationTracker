package com.example.educationtracker;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.widget.Switch;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    private Switch swNotifications;
    private Button btnChangeTheme;
    private SharedPreferences sharedPreferences;  // Dodata promenljiva klase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Učitaj temu iz SharedPreferences i postavi je
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        int nightMode = sharedPreferences.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(nightMode);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        swNotifications = findViewById(R.id.sw_notifications);
        btnChangeTheme = findViewById(R.id.btn_change_theme);

        // Postavljanje podrazumevanih vrednosti
        swNotifications.setChecked(sharedPreferences.getBoolean("notifications_enabled", true));
        nightMode = sharedPreferences.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_NO);  // Izmena vrednosti
        AppCompatDelegate.setDefaultNightMode(nightMode);

        // Ostatak koda...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            }
        }

        // Promena teme
        btnChangeTheme.setOnClickListener(view -> {
            int currentNightMode = AppCompatDelegate.getDefaultNightMode();
            int newNightMode = currentNightMode == AppCompatDelegate.MODE_NIGHT_YES
                    ? AppCompatDelegate.MODE_NIGHT_NO
                    : AppCompatDelegate.MODE_NIGHT_YES;
            AppCompatDelegate.setDefaultNightMode(newNightMode);
            sharedPreferences.edit().putInt("night_mode", newNightMode).apply();
        });

        // Postavke notifikacija
        swNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply();

            if (isChecked) {
                // Kreiraj notifikacioni kanal (samo za verzije Androida 8.0 i više)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "TaskNotificationChannel";
                    String description = "Channel for Task Notifications";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel("TASK_NOTIFICATION_CHANNEL", name, importance);
                    channel.setDescription(description);
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }

                // Prikaz test notifikacije
                NotificationCompat.Builder builder = new NotificationCompat.Builder(SettingsActivity.this, "TASK_NOTIFICATION_CHANNEL")
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("Notifikacije omogućene")
                        .setContentText("Sada ćeš primati notifikacije za zadatke.")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(SettingsActivity.this);
                notificationManager.notify(1, builder.build());
            } else {
                // Uklanjanje postojećih notifikacija
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(SettingsActivity.this);
                notificationManager.cancelAll();
            }
        });
    }
}

