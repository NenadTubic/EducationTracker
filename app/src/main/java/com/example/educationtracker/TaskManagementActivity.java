package com.example.educationtracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

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
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        int nightMode = sharedPreferences.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(nightMode);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_management);

        rvTasks = findViewById(R.id.rv_tasks);
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(
                this,
                taskList,
                position -> editTask(position),
                position -> deleteTask(position)
        );

        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(taskAdapter);

        fabAddTask = findViewById(R.id.fab_add_task);
        fabAddTask.setOnClickListener(view -> {
            Intent intent = new Intent(TaskManagementActivity.this, MainActivity.class);
            startActivity(intent);
        });

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

    private void deleteTask(int position) {
        if (position >= 0 && position < taskList.size()) {
            taskList.remove(position);
            taskAdapter.notifyItemRemoved(position);
            taskAdapter.notifyItemRangeChanged(position, taskList.size());
            saveTasks();
        }
    }

    private void editTask(int position) {
        String task = taskList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Uredi zadatak");

        final EditText input = new EditText(this);
        input.setText(task);
        builder.setView(input);

        builder.setPositiveButton("Sačuvaj", (dialog, which) -> {
            String editedTask = input.getText().toString();
            if (!editedTask.trim().isEmpty()) {
                taskList.set(position, editedTask);
                taskAdapter.notifyItemChanged(position);
                saveTasks();
            } else {
                Toast.makeText(this, "Zadatak ne može biti prazan!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Otkaži", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void saveTasks() {
        SharedPreferences sharedPreferences = getSharedPreferences("TaskPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> taskSet = new HashSet<>(taskList);
        editor.putStringSet("Tasks", taskSet);
        editor.apply();
    }

    private void addNewTask() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dodaj novi zadatak");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Dodaj", (dialog, which) -> {
            String newTask = input.getText().toString();
            taskList.add(newTask);
            taskAdapter.notifyItemInserted(taskList.size() - 1);
            saveTasks();
            Intent refreshIntent = new Intent(TaskManagementActivity.this, MainActivity.class);
            startActivity(refreshIntent);
        });
        builder.setNegativeButton("Otkaži", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}

