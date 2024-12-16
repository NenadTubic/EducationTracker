package com.example.educationtracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<String> taskList;
    private LayoutInflater mInflater;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public TaskAdapter(Context context, List<String> taskList,
                       OnItemClickListener clickListener,
                       OnItemLongClickListener longClickListener) {
        this.taskList = taskList;
        this.mInflater = LayoutInflater.from(context);
        this.onItemClickListener = clickListener;
        this.onItemLongClickListener = longClickListener;
    }


    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.task_list_item, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        String task = taskList.get(position);
        holder.taskItemView.setText(task);

        // Rukovanje klikom
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(position));

        // Rukovanje dugim pritiskom
        holder.itemView.setOnLongClickListener(v -> {
            onItemLongClickListener.onItemLongClick(position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        public final TextView taskItemView;

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskItemView = itemView.findViewById(R.id.task_item);
        }
    }
}

