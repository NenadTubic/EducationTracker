package com.example.educationtracker;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private EditText etTaskName;
    private FloatingActionButton fabAddTask;
    private RecyclerView rvTasks;
    private TaskAdapter taskAdapter;
    private ArrayList<String> taskList;
    private int selectedDay = -1, selectedMonth = -1, selectedYear = -1;
    private CheckBox cbHighPriority, cbLowPriority;
    private RadioGroup rgTaskType;
    private RadioButton rbWork, rbPersonal;
    private SharedPreferences sharedPreferences;

    private static final String PREFERENCES_NAME = "TaskPreferences";
    private static final String TASKS_KEY = "Tasks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        int nightMode = sharedPreferences.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(nightMode);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnLongClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SensorDataActivity.class);
            startActivity(intent);
            return true;
        });

        etTaskName = findViewById(R.id.et_task_name);
        fabAddTask = findViewById(R.id.fab_add_task);
        rvTasks = findViewById(R.id.rv_tasks);
        cbHighPriority = findViewById(R.id.cb_high_priority);
        cbLowPriority = findViewById(R.id.cb_low_priority);
        rgTaskType = findViewById(R.id.rg_task_type);
        rbWork = findViewById(R.id.rb_work);
        rbPersonal = findViewById(R.id.rb_personal);

        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true);

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(
                this,
                taskList,
                position -> {
                    String task = taskList.get(position);
                    Toast.makeText(MainActivity.this, "Kliknuli ste na: " + task, Toast.LENGTH_SHORT).show();
                },
                position -> {
                }
        );

        rvTasks.setAdapter(taskAdapter);

        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(taskAdapter);

        rbPersonal.setChecked(true);

        loadTasks();

        findViewById(R.id.btn_date_picker).setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity.this,
                    (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                        this.selectedDay = selectedDay;
                        this.selectedMonth = selectedMonth;
                        this.selectedYear = selectedYear;
                    },
                    year, month, day);

            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

            datePickerDialog.show();
        });

        cbHighPriority.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cbLowPriority.setChecked(false);
            }
        });

        cbLowPriority.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cbHighPriority.setChecked(false);
            }
        });

        fabAddTask.setOnClickListener(view -> addTask());

        rvTasks.addOnItemTouchListener(
                new RecyclerItemClickListener(this, rvTasks, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String task = taskList.get(position);
                        Toast.makeText(MainActivity.this, "Kliknuli ste na: " + task, Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_manage_tasks) {
            Intent manageTasksIntent = new Intent(MainActivity.this, TaskManagementActivity.class);
            startActivity(manageTasksIntent);
            return true;
        } else if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void addTask() {
        String taskName = etTaskName.getText().toString().trim();

        if (taskName.isEmpty()) {
            Toast.makeText(MainActivity.this, "Unesite ime zadatka!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedDay == -1 || selectedMonth == -1 || selectedYear == -1) {
            Toast.makeText(MainActivity.this, "Izaberite datum!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbHighPriority.isChecked() && !cbLowPriority.isChecked()) {
            Toast.makeText(MainActivity.this, "Izaberite prioritet!", Toast.LENGTH_SHORT).show();
            return;
        }

        String taskDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);

        String priority = cbHighPriority.isChecked() ? "Visok" : "Nizak";
        int selectedTaskTypeId = rgTaskType.getCheckedRadioButtonId();
        String taskType = selectedTaskTypeId == rbWork.getId() ? "Posao" : "Lično";

        String task = "Zadatak: " + taskName + " - Rok: " + taskDate + " - Prioritet: " + priority + " - Vrsta: " + taskType;
        taskList.add(task);
        taskAdapter.notifyDataSetChanged();
        saveTasks();

        etTaskName.setText("");
        selectedDay = -1;
        selectedMonth = -1;
        selectedYear = -1;
        cbHighPriority.setChecked(false);
        cbLowPriority.setChecked(false);
        rgTaskType.clearCheck();
        rbPersonal.setChecked(true);

        Toast.makeText(MainActivity.this, "Zadatak dodat!", Toast.LENGTH_SHORT).show();

        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true);

        if (notificationsEnabled) {
            Intent intent = new Intent(MainActivity.this, TaskNotificationReceiver.class);
            intent.putExtra("taskName", task);
            intent.putExtra("notificationId", taskList.size());
            sendBroadcast(intent);
        }
    }

    private void saveTasks() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> taskSet = new HashSet<>(taskList);
        editor.putStringSet(TASKS_KEY, taskSet);
        editor.apply();
    }

    private void loadTasks() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        Set<String> taskSet = sharedPreferences.getStringSet(TASKS_KEY, new HashSet<>());
        if (taskSet != null) {
            taskList.clear();
            taskList.addAll(taskSet);
            taskAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }
}
