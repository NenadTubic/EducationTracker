package com.example.educationtracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TaskManagementActivity extends AppCompatActivity {

    private RecyclerView rvTasks;
    private TaskAdapter taskAdapter;
    private ArrayList<String> taskList;
    private FloatingActionButton fabAddTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Učitaj temu iz SharedPreferences i postavi je
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        int nightMode = sharedPreferences.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(nightMode);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_management);

        rvTasks = findViewById(R.id.rv_tasks);
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(this, taskList, position -> {
            editTask(position);
        }, position -> {
            deleteTask(position);
        });
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(taskAdapter);

        fabAddTask = findViewById(R.id.fab_add_task);
        fabAddTask.setOnClickListener(view -> {
            Intent intent = new Intent(TaskManagementActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Učitaj zadatke iz SharedPreferences ili bilo kojeg izvora podataka
        loadTasks();
    }

    private void loadTasks() {
        SharedPreferences sharedPreferences = getSharedPreferences("TaskPreferences", Context.MODE_PRIVATE);
        Set<String> taskSet = sharedPreferences.getStringSet("Tasks", new HashSet<>());
        taskList.clear();
        if (taskSet != null) {
            taskList.addAll(taskSet);
        }
        taskAdapter.notifyDataSetChanged();
    }

    private void editTask(int position) {
        String task = taskList.get(position);
        // Logika za uređivanje zadatka
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Uredi zadatak");

        final EditText input = new EditText(this);
        input.setText(task);
        builder.setView(input);

        builder.setPositiveButton("Sačuvaj", (dialog, which) -> {
            String editedTask = input.getText().toString();
            taskList.set(position, editedTask);
            taskAdapter.notifyItemChanged(position);
            saveTasks();
        });
        builder.setNegativeButton("Otkaži", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void deleteTask(int position) {
        taskList.remove(position);
        taskAdapter.notifyItemRemoved(position);
        saveTasks();
    }

    private void saveTasks() {
        SharedPreferences sharedPreferences = getSharedPreferences("TaskPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> taskSet = new HashSet<>(taskList);
        editor.putStringSet("Tasks", taskSet);
        editor.apply();
    }

    private void addNewTask() {
        // Logika za dodavanje novog zadatka
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dodaj novi zadatak");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Dodaj", (dialog, which) -> {
            String newTask = input.getText().toString();
            taskList.add(newTask);
            taskAdapter.notifyItemInserted(taskList.size() - 1);
            saveTasks();
        });
        builder.setNegativeButton("Otkaži", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
